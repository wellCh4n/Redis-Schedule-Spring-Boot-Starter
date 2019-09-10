package com.wellch4n.schedule.task;

import com.wellch4n.schedule.enums.TaskTypeEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

    public TaskHandler(ExecutorService executorService, JedisPool jedisPool) {
        this.taskPool = executorService;
        this.jedisPool = jedisPool;
    }

    /**
     * 增加延迟任务
     * @param key
     * @param delayTime
     * @param taskTypeEnum
     * @param bizParam
     */
    public void add(String key, Integer delayTime, TaskTypeEnum taskTypeEnum, Object... bizParam) {
        try (Jedis jedis = jedisPool.getResource()) {
            taskTypeEnum.taskClazz.add(jedis, taskMap, key, delayTime, bizParam);
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
