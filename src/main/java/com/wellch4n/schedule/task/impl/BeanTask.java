package com.wellch4n.schedule.task.impl;

import com.alibaba.fastjson.JSONObject;
import com.wellch4n.schedule.enums.TaskTypeEnum;
import com.wellch4n.schedule.namespace.TaskPrefixNamespace;
import com.wellch4n.schedule.task.Task;
import com.wellch4n.schedule.task.TaskHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
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
    public void add(Jedis jedis, ConcurrentHashMap<String, Runnable> taskMap, String key, Integer delayTime, Object... bizParam) {
        Long now = System.currentTimeMillis();

        log.info("Add schedule bean task [{}] task, delayTime={}, time={}", key, delayTime, now);
        jedis.setex(TaskPrefixNamespace.BEAN + key, delayTime, "");

        BeanTaskDTO beanTaskDTO = new BeanTaskDTO();
        beanTaskDTO.setBean((String) bizParam[0]);
        beanTaskDTO.setMethod((String) bizParam[1]);

        List<Object> paramList = Arrays.asList(bizParam);
        paramList = paramList.subList(1, paramList.size() - 1);
        beanTaskDTO.setParam(paramList);

        // 参数延迟一天过期
        int oneDayLater = delayTime + 3600 * 24;
        log.info("bean task [{}] param expired time={}", key, oneDayLater);
        jedis.setex(TaskPrefixNamespace.PARAM + key, oneDayLater, JSONObject.toJSONString(beanTaskDTO));
    }

    @Override
    public Runnable taskBody(TaskHandler taskHandler, String message) {
        try {

            Jedis jedis = taskHandler.getJedisPool().getResource();

            String paramKey = getParamKey(message);
            String paramValue = jedis.get(paramKey);
            BeanTaskDTO beanTaskDTO = getScheduleParam(paramValue);

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
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Data
    private static class BeanTaskDTO {
        private String bean;

        private String method;

        private List<Object> param;
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
