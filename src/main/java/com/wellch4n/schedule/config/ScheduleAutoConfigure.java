package com.wellch4n.schedule.config;

import com.wellch4n.schedule.core.Schedule;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
public class ScheduleAutoConfigure implements ApplicationContextAware {

    @Autowired
    private JedisPool jedisPool;

    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public Schedule schedule() {
        return new Schedule(Executors.newCachedThreadPool(), jedisPool, applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
