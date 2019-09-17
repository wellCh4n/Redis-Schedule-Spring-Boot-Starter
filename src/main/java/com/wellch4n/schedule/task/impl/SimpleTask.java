package com.wellch4n.schedule.task.impl;

import com.wellch4n.schedule.namespace.TaskPrefixNamespace;
import com.wellch4n.schedule.task.Task;
import com.wellch4n.schedule.task.TaskHandler;
import com.wellch4n.schedule.task.TaskParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
     */
    @Override
    public void add(Jedis jedis, ConcurrentHashMap<String, Runnable> taskMap, String key, Integer delayTime, TaskParam param) {
        Param param1 = (Param) param;
        taskMap.put(TaskPrefixNamespace.RUNNABLE + key, param1.getRunnable());
    }

    @Override
    public Runnable taskBody(TaskHandler taskHandler, String message) {
        Runnable task = taskHandler.getTaskMap().get(message);
        log.info("Schedule task [{}], time={}", message, System.currentTimeMillis());
        return task;
    }

    @Data
    public static class Param extends TaskParam {
        private Runnable runnable;
    }
}
