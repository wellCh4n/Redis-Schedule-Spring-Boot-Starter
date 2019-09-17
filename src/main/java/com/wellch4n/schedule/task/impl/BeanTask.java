package com.wellch4n.schedule.task.impl;

import com.alibaba.fastjson.JSONObject;
import com.wellch4n.schedule.enums.TaskTypeEnum;
import com.wellch4n.schedule.namespace.TaskPrefixNamespace;
import com.wellch4n.schedule.task.Task;
import com.wellch4n.schedule.task.TaskHandler;
import com.wellch4n.schedule.task.TaskParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 *
 * 基于Spring的Bean实现的任务调度，使用BeanFactory的Bean反射实现
 */

@Slf4j
public class BeanTask implements Task {
    private final static String SPLIT_STRING = "::";

    @Override
    public void add(Jedis jedis, ConcurrentHashMap<String, Runnable> taskMap, String key, Integer delayTime, TaskParam param) {
        Param beanParam = (Param) param;
        String paramKey = TaskPrefixNamespace.PARAM + key;
        int oneDayLater = delayTime + 3600 * 24;
        log.info("bean task [{}] param expired time={}", key, oneDayLater);
        jedis.setex(paramKey, oneDayLater, JSONObject.toJSONString(beanParam));
    }

    @Override
    public Runnable taskBody(TaskHandler taskHandler, String message) {
        try {
            Jedis jedis = taskHandler.getJedisPool().getResource();

            String paramKey = getParamKey(message);
            String paramValue = jedis.get(paramKey);
            Param beanTaskDTO = getScheduleParam(paramValue);

            // 立即回收缓存资源
            jedis.del(paramKey);
            jedis.close();

            Object beanObject = taskHandler.getApplicationContext().getBean(beanTaskDTO.getBean());
            Method method = beanObject.getClass()
                    .getMethod(beanTaskDTO.getMethod(), getParamsClass(beanTaskDTO.getParam()));

            log.info("Schedule task [{}], time={}", message, System.currentTimeMillis());
            return () -> {
                try {
                    method.invoke(beanObject, getParamsValue(beanTaskDTO.getParam()));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Data
    public static class Param extends TaskParam {
        private String bean;
        private String method;
        private List<Object> param;
    }

    private String getParamKey(String message) {
        String[] keyArr = message.split(SPLIT_STRING);
        keyArr[0] = TaskTypeEnum.PARAM.code;

        return String.join(SPLIT_STRING, keyArr);
    }

    private Param getScheduleParam(String value) {
        return JSONObject.parseObject(value, Param.class);
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
