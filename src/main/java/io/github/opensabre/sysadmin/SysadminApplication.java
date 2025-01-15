package io.github.opensabre.sysadmin;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMethodCache(basePackages = {"io.github.opensabre.sysadmin"})
public class SysadminApplication {
    public static void main(String[] args) {
        SpringApplication.run(SysadminApplication.class, args);
    }
}
