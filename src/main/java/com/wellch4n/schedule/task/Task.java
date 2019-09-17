package com.wellch4n.schedule.task;

import redis.clients.jedis.Jedis;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

public interface Task {

    /**
     * 任务提交方法
     * @param jedis Jedis实例
     * @param taskMap Runnable实例映射
     * @param key 任务Id
     * @param delayTime 延迟时间
     * @param param 业务参数
     */
    void add(Jedis jedis, ConcurrentHashMap<String, Runnable> taskMap, String key, Integer delayTime, TaskParam param);

    /**
     * 任务具体方法
     * @param taskHandler 任务Handler
     * @param message 过期Key
     * @return
     */
    Runnable taskBody(TaskHandler taskHandler, String message);
}
