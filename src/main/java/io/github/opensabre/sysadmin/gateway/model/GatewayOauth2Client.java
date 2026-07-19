package io.github.opensabre.sysadmin.gateway.model;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/** 网关可用的一种 OAuth2/OIDC 登录认证方式。 */
public class GatewayOauth2Client {
    @NotBlank(message = "注册名不能为空")
    private String registrationId;
    @NotBlank(message = "Provider 名称不能为空")
    private String provider;
    @NotBlank(message = "Issuer URI 不能为空")
    private String issuerUri;
    @NotBlank(message = "客户端 ID 不能为空")
    private String clientId;
    /** 仅在新增、密钥轮换或重新绑定时传入；读取时始终返回掩码。 */
    private String clientSecret;
    @NotBlank(message = "回调地址不能为空")
    private String redirectUri;
    private List<String> scopes = new ArrayList<>();
    private boolean enabled = true;
    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getIssuerUri() { return issuerUri; }
    public void setIssuerUri(String issuerUri) { this.issuerUri = issuerUri; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
    public List<String> getScopes() { return scopes; }
    public void setScopes(List<String> scopes) { this.scopes = scopes == null ? new ArrayList<>() : scopes; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
