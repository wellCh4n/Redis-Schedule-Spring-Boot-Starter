package com.wellch4n.schedule.task;

import com.wellch4n.schedule.enums.TaskTypeEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

@Getter
@Slf4j
public class TaskHandler {
    private ExecutorService taskPool;

    private ConcurrentHashMap<String, Runnable> taskMap = new ConcurrentHashMap<>();

    private JedisPool jedisPool;

    private ApplicationContext applicationContext;

    public TaskHandler(ExecutorService executorService, JedisPool jedisPool, ApplicationContext applicationContext) {
        this.taskPool = executorService;
        this.jedisPool = jedisPool;
        this.applicationContext = applicationContext;
    }

    /**
     * 增加延迟任务
     * @param key
     * @param delayTime
     * @param taskTypeEnum
     * @param taskParam
     */
    public void add(String key, Integer delayTime, TaskTypeEnum taskTypeEnum, TaskParam taskParam) {
        Long now = System.currentTimeMillis();
        try (Jedis jedis = jedisPool.getResource()) {
            log.info("Add [{}] task, key={}, delayTime={}s, now={}", taskTypeEnum.code, key, delayTime, now);
            jedis.setex(taskTypeEnum.code + "::" + key, delayTime, "");
            taskTypeEnum.getTaskClass().add(jedis, taskMap, key, delayTime, taskParam);
        }
    }

    /**
     * 释放资源
     * @param key
     */
    public void remove(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
            this.taskMap.remove(key);
        } catch (Exception e) {
            log.error("Delete key={}, error, Message={}", key, e.getMessage());
            e.printStackTrace();
        }
    }
}
