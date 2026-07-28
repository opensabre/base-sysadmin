package io.github.opensabre.sysadmin.usage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 对象使用计次事件的异步执行配置。
 */
@Configuration
@EnableAsync
public class UsageCounterAsyncConfig {

    @Bean(name = "usageCounterTaskExecutor")
    public Executor usageCounterTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(10_000);
        executor.setThreadNamePrefix("usage-counter-");
        executor.initialize();
        return executor;
    }
}
