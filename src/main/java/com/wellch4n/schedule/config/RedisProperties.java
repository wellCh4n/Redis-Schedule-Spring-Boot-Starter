package com.wellch4n.schedule.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

@Data
@ConfigurationProperties("spring.redis")
public class RedisProperties {
    private Integer database;
    private String host;
    private Integer port;
    private String password;

    private Integer maxActive = 8;

    private Integer maxIdle = 8;

    private Integer minIdle = 0;
}
