package io.github.opensabre.sysadmin.internaltoken.repository;

import io.github.opensabre.sysadmin.internaltoken.config.InternalTokenKeyManagementProperties;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

/**
 * Nacos v1 configuration repository with CAS publication.
 */
@Repository
public class NacosInternalTokenSharedConfigRepository
        implements InternalTokenSharedConfigRepository {

    private final InternalTokenKeyManagementProperties properties;
    private final HttpClient httpClient;

    public NacosInternalTokenSharedConfigRepository(
            InternalTokenKeyManagementProperties properties) {
        this(properties, HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
    }

    NacosInternalTokenSharedConfigRepository(
            InternalTokenKeyManagementProperties properties,
            HttpClient httpClient) {
        this.properties = properties;
        this.httpClient = httpClient;
    }

    @Override
    public InternalTokenSharedConfigSnapshot read() {
        HttpRequest request = HttpRequest.newBuilder(configUri())
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() == 404) {
                return new InternalTokenSharedConfigSnapshot("", "", false);
            }
            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                        "读取内部 Token 共享配置失败，Nacos 返回 HTTP " + response.statusCode());
            }
            return new InternalTokenSharedConfigSnapshot(
                    response.body(), md5(response.body()), true);
        } catch (IOException exception) {
            throw new IllegalStateException("无法连接配置中心读取内部 Token 共享配置", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("读取内部 Token 共享配置被中断", exception);
        }
    }

    @Override
    public void publish(String content, InternalTokenSharedConfigSnapshot expected) {
        StringBuilder body = new StringBuilder(baseForm())
                .append("&type=yaml")
                .append("&content=").append(encode(content));
        if (expected.exists()) {
            body.append("&casMd5=").append(encode(expected.casVersion()));
        }
        HttpRequest request = HttpRequest.newBuilder(configEndpoint())
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                        "发布内部 Token 共享配置失败，Nacos 返回 HTTP " + response.statusCode());
            }
            if (!Boolean.parseBoolean(response.body())) {
                throw new IllegalStateException("内部 Token 共享配置已变化，请刷新后重试");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("无法连接配置中心发布内部 Token 共享配置", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("发布内部 Token 共享配置被中断", exception);
        }
    }

    private URI configUri() {
        return URI.create(configEndpoint() + "?" + baseForm());
    }

    private URI configEndpoint() {
        String serverUrl = properties.getNacosServerUrl().replaceAll("/$", "");
        return URI.create(serverUrl + "/nacos/v1/cs/configs");
    }

    private String baseForm() {
        String value = "dataId=" + encode(properties.getDataId())
                + "&group=" + encode(properties.getGroup());
        if (properties.getNamespace() != null && !properties.getNamespace().isBlank()) {
            value += "&tenant=" + encode(properties.getNamespace());
        }
        return value;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String md5(String content) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("MD5")
                            .digest(content.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("MD5 algorithm is unavailable", exception);
        }
    }
}
