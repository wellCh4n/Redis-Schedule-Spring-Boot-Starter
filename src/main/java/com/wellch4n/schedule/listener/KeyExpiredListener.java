package com.wellch4n.schedule.listener;

import com.wellch4n.schedule.domain.BeanTaskDTO;
import com.wellch4n.schedule.enums.TaskTypeEnum;
import com.wellch4n.schedule.task.TaskHandler;
import com.wellch4n.schedule.utils.KeyParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

@Slf4j
public class KeyExpiredListener extends JedisPubSub {
    private TaskHandler taskHandler;

    private ApplicationContext applicationContext;

    public KeyExpiredListener(TaskHandler taskHandler, ApplicationContext applicationContext) {
        this.taskHandler = taskHandler;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onMessage(String channel, String message) {
        try {
            // 1.解析任务类型
            TaskTypeEnum type = KeyParserUtils.parserKey(message);

            // 2.分配不同的执行方式
            if (TaskTypeEnum.RUNNABLE.equals(type)) {
                Runnable task = taskHandler.getTaskMap().get(message);
                if (task == null) {
                    return;
                }
                log.info("Schedule task [{}], time={}", message, System.currentTimeMillis());
                taskHandler.getTaskPool().execute(task);
            } else if (TaskTypeEnum.BEAN.equals(type)) {
                Jedis jedis = taskHandler.getJedisPool().getResource();

                String paramKey = KeyParserUtils.getParamKey(message);
                String paramValue = jedis.get(paramKey);
                BeanTaskDTO beanTaskDTO = KeyParserUtils.getParam(paramValue);
                jedis.close();

                Object beanObject = applicationContext.getBean(beanTaskDTO.getBean());
                Method method = beanObject.getClass().getMethod(beanTaskDTO.getMethod());

                taskHandler.getTaskPool().execute(() -> {
                    try {
                        method.invoke(beanObject, beanTaskDTO.getParam().toArray());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            } else if (TaskTypeEnum.PARAM.equals(type)) {
                log.info("param [{}] expired", message);
            } else {
                log.error("unknown task [{}]", message);
            }
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
