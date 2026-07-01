package io.github.opensabre.sysadmin.ratelimit.dimension;

import io.github.opensabre.webmvc.util.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 设备维度提取器
 * 基于User-Agent和IP生成设备指纹
 *
 * <p>设备指纹生成规则：
 * <ol>
 *   <li>获取User-Agent头</li>
 *   <li>获取客户端IP（使用X-Forwarded-For）</li>
 *   <li>使用FNV哈希算法生成设备ID</li>
 * </ol>
 * </p>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class DeviceDimensionExtractor implements DimensionExtractor {

    /**
     * 提取设备ID
     *
     * @param request HTTP 请求
     * @return 设备ID，如果无法提取则返回null
     */
    @Override
    public String extract(HttpServletRequest request) {
        // 复用 HttpUtils 中的方法
        return HttpUtils.getDeviceId(request);
    }
}
