package com.wellch4n.schedule.task;

import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

public interface Task {

    /**
     * 任务提交方法
     * @param jedis
     * @param taskMap
     * @param key
     * @param delayTime
     * @param bizObj
     */
    void add(Jedis jedis, ConcurrentHashMap<String, Runnable> taskMap, String key, Integer delayTime, Object... bizObj);

    /**
     * 任务具体方法
     * @param taskHandler
     * @param message
     * @param applicationContext
     * @return
     */
    Runnable taskBody(TaskHandler taskHandler, String message, ApplicationContext applicationContext);
}
