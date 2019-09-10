package com.wellch4n.schedule.task.impl;

import com.wellch4n.schedule.namespace.TaskPrefixNamespace;
import com.wellch4n.schedule.task.Task;
import com.wellch4n.schedule.task.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 *
 * 基于内存实现的延迟任务，重启会导致任务调度失败
 */

@Slf4j
public class SimpleTask implements Task {

    /**
     * 简单任务的增加方法
     * @param bizParam [Runnable]
     */
    @Override
    public void add(Jedis jedis, ConcurrentHashMap<String, Runnable> taskMap, String key, Integer delayTime,
                    Object... bizParam) {
        Long now = System.currentTimeMillis();

        log.info("Add schedule simple task [{}] task, delayTime={}, time={}", key, delayTime, now);
        jedis.setex(TaskPrefixNamespace.RUNNABLE + key, delayTime, "");
        taskMap.put(TaskPrefixNamespace.RUNNABLE + key, (Runnable) bizParam[0]);
    }

    @Override
    public Runnable taskBody(TaskHandler taskHandler, String message, ApplicationContext applicationContext) {
        Runnable task = taskHandler.getTaskMap().get(message);
        log.info("Schedule task [{}], time={}", message, System.currentTimeMillis());
        return task;
    }
}
