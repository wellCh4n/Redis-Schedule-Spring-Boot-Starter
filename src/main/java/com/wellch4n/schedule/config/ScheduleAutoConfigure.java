package com.wellch4n.schedule.config;

import com.wellch4n.schedule.core.Schedule;
import com.wellch4n.schedule.task.TaskHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.Executors;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

@Configuration
@ConditionalOnClass(RedisProperties.class)
@EnableConfigurationProperties(RedisProperties.class)
public class ScheduleAutoConfigure {

    @Autowired
    private JedisPool jedisPool;

    @Bean
    @ConditionalOnMissingBean
    public Schedule schedule() {
        return new Schedule(Executors.newCachedThreadPool(), jedisPool);
    }
}
