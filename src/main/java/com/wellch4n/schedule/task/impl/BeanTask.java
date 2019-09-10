package com.wellch4n.schedule.task.impl;

import com.alibaba.fastjson.JSONObject;
import com.wellch4n.schedule.domain.BeanTaskDTO;
import com.wellch4n.schedule.enums.TaskTypeEnum;
import com.wellch4n.schedule.task.Task;
import com.wellch4n.schedule.task.TaskHandler;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 *
 * 基于Spring的Bean实现的任务调度，使用BeanFactory的Bean反射实现
 */

public class BeanTask implements Task {

    private final static String SPLIT_STRING = "::";

    @Override
    public Runnable taskBody(TaskHandler taskHandler, String message, ApplicationContext applicationContext) {
        try {

            Jedis jedis = taskHandler.getJedisPool().getResource();

            String paramKey = getParamKey(message);
            String paramValue = jedis.get(paramKey);
            BeanTaskDTO beanTaskDTO = getScheduleParam(paramValue);

            // 立即回收缓存资源
            jedis.del(paramKey);
            jedis.close();

            Object beanObject = applicationContext.getBean(beanTaskDTO.getBean());
            Method method = beanObject.getClass()
                    .getMethod(beanTaskDTO.getMethod(), getParamsClass(beanTaskDTO.getParam()));

            return () -> {
                try {
                    method.invoke(beanObject, getParamsValue(beanTaskDTO.getParam()));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            };
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getParamKey(String message) {
        String[] keyArr = message.split(SPLIT_STRING);
        keyArr[0] = TaskTypeEnum.PARAM.code;

        return String.join(SPLIT_STRING, keyArr);
    }

    private BeanTaskDTO getScheduleParam(String value) {
        return JSONObject.parseObject(value, BeanTaskDTO.class);
    }

    private Class[] getParamsClass(List<Object> params) {
        if (params == null || params.isEmpty()) {
            return new Class[]{};
        }

        return params.stream()
                .map(Object::getClass)
                .collect(Collectors.toList())
                .toArray(new Class[]{});
    }

    private Object[] getParamsValue(List<Object> params) {
        if (params == null || params.isEmpty()) {
            return new Class[]{};
        }
        return params.toArray();
    }
}
