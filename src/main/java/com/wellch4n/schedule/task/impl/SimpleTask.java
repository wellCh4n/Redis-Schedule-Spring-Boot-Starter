package com.wellch4n.schedule.task.impl;

import com.wellch4n.schedule.task.Task;
import com.wellch4n.schedule.task.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 *
 * 基于内存实现的延迟任务，重启会导致任务调度失败
 */

@Slf4j
public class SimpleTask implements Task {

    @Override
    public Runnable taskBody(TaskHandler taskHandler, String message, ApplicationContext applicationContext) {
        Runnable task = taskHandler.getTaskMap().get(message);
        log.info("Schedule task [{}], time={}", message, System.currentTimeMillis());
        return task;
    }

//    @Override
//    public void add(Jedis jedis, ConcurrentHashMap<String, Runnable> taskMap, String key, Integer delayTime, Object bizObj) {
//        Long now = System.currentTimeMillis();
//        SimpleBizParam param = (SimpleBizParam) bizObj;
//
//        log.info("Add schedule task [{}] task, delayTime={}, time={}", key, delayTime, now);
//        jedis.setex(TaskPrefixNamespace.RUNNABLE + key, delayTime, "");
//        taskMap.put(key, param.getRunnable());
//    }
//
//
//    public static class SimpleBizParam {
//        private Runnable runnable;
//
//        public Runnable getRunnable() {
//            return runnable;
//        }
//
//        public void setRunnable(Runnable runnable) {
//            this.runnable = runnable;
//        }
//    }
}
