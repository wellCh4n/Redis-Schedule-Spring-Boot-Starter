package com.wellch4n.schedule.listener;

import com.wellch4n.schedule.enums.TaskTypeEnum;
import com.wellch4n.schedule.task.Task;
import com.wellch4n.schedule.task.TaskHandler;
import com.wellch4n.schedule.utils.KeyParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.JedisPubSub;


/**
 * @author wellCh4n
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
            // 解析任务类型
            TaskTypeEnum type = KeyParserUtils.parserKey(message);
            Task task = type.getTaskClass();

            if (task == null) {
                return;
            }

            // 实例化任务并到线程池执行
            Runnable runnable = task.taskBody(taskHandler, message);
            taskHandler.getTaskPool().execute(runnable);
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
