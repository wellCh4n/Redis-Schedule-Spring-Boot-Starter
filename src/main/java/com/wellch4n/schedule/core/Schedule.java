package com.wellch4n.schedule.core;

import com.wellch4n.schedule.listener.KeyExpiredListener;
import com.wellch4n.schedule.task.TaskHandler;
import com.wellch4n.schedule.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * @author wellCh4n
 * @description
 * @create 2019/09/06
 * 找到银弹，然后开枪
 */

@Slf4j
public class Schedule {
    private TaskHandler taskHandler;

    public Schedule(ExecutorService executorService, JedisPool jedisPool) {
        if (executorService == null || jedisPool == null) {
            throw new NullPointerException();
        }
        this.taskHandler = new TaskHandler(executorService, jedisPool);
        start();
    }

    private void start() {
        log.info("Starting Schedule Module...");
        JedisPool jedisPool = taskHandler.getJedisPool();
        Jedis jedis = jedisPool.getResource();
        log.info("Redis loading success...");

        jedis.configSet("notify-keyspace-events", "Ex");

        log.info("Starting subscribe expired key...");
        Runnable runnable = () -> jedis.subscribe(new KeyExpiredListener(taskHandler), "__keyevent@0__:expired");
        Thread subThread = new Thread(runnable);
        subThread.start();
        log.info("Schedule started!");
    }

    public void add(String key, Integer delayTime, Runnable task) {
        this.taskHandler.add(key, delayTime, task);
    }

    public void add(String key, Date invokeTime, Runnable task) {
        int delayTime = TimeUtils.deltaTime(new Date(), invokeTime);
        add(key, delayTime, task);
    }

    public void remove(String key) {
        this.taskHandler.remove(key);
    }
}
