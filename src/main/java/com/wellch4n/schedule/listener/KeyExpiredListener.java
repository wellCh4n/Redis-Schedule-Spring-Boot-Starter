package com.wellch4n.schedule.listener;

import com.wellch4n.schedule.task.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPubSub;

/**
 * @author wellCh4n
 * @description
 * @create 2019/09/06
 * 找到银弹，然后开枪
 */

@Slf4j
public class KeyExpiredListener extends JedisPubSub {
    private TaskHandler taskHandler;

    public KeyExpiredListener(TaskHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    @Override
    public void onMessage(String channel, String message) {
        try {
            Runnable task = taskHandler.getTaskMap().get(message);
            if (task == null) {
                return;
            }
            log.info("Schedule task [{}], time={}", message, System.currentTimeMillis());
            taskHandler.getTaskPool().execute(task);
        } catch (Exception e) {
            log.info("Schedule task error, key={}, message={}", message, e.getMessage());
            e.printStackTrace();
        } finally {
            taskHandler.remove(message);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        log.info("Subscribe [{}] success", channel);
    }
}
