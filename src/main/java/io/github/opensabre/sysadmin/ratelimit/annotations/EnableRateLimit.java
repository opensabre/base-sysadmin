package io.github.opensabre.sysadmin.ratelimit.annotations;

import io.github.opensabre.sysadmin.ratelimit.aspect.RateLimitAspect;
import io.github.opensabre.sysadmin.ratelimit.service.impl.RateLimitServiceImpl;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用限次模块
 * 添加在主应用类上，自动导入限次相关组件
 *
 * <p>示例用法：</p>
 * <pre>
 * {@code
 * @SpringBootApplication
 * @EnableRateLimit
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * }
 * </pre>
 *
 * @author Opensabre Team
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RateLimitAspect.class, RateLimitServiceImpl.class})
public @interface EnableRateLimit {
}
