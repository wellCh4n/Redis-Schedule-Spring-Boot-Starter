package com.wellch4n.schedule.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

/**
 * @author wellCh4n
 * @description
 * @create 2019/09/06
 * 找到银弹，然后开枪
 */

@Configuration
public class JedisConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public JedisPool jedisPool() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMinIdle(redisProperties.getMinIdle());
        genericObjectPoolConfig.setMaxIdle(redisProperties.getMaxIdle());
        genericObjectPoolConfig.setMaxTotal(redisProperties.getMaxActive());
        return new JedisPool(genericObjectPoolConfig, redisProperties.getHost(), redisProperties.getPort());
    }
}
