package com.wellch4n.schedule.task;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wellCh4n
 * @description
 * @create 2019/09/06
 * 找到银弹，然后开枪
 */

@Getter
@Slf4j
public class TaskHandler {
    private ExecutorService taskPool;

    private ConcurrentHashMap<String, Runnable> taskMap = new ConcurrentHashMap<>();

    private JedisPool jedisPool;

    public TaskHandler() {
        this.taskPool = Executors.newCachedThreadPool();
    }

    public TaskHandler(ExecutorService executorService, JedisPool jedisPool) {
        this.taskPool = executorService;
        this.jedisPool = jedisPool;
    }

    /**
     * 增加延迟任务
     * @param key
     * @param delayTime
     * @param task
     */
    public void add(String key, Integer delayTime, Runnable task) {
        // 写入过期Key
        Long now = System.currentTimeMillis();
        try (Jedis jedis = jedisPool.getResource()) {
            log.info("Add schedule task [{}] task, delayTime={}, time={}", key, delayTime, now);
            jedis.setex(key, delayTime, "");
            this.taskMap.put(key, task);
        } catch (Exception e) {
            log.error("Add key={}, delayTime={} error={}, message={}, time={}", key, delayTime, e.getMessage(), now);
            e.printStackTrace();
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
