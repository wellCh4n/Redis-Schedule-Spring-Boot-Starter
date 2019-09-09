package com.wellch4n.schedule.utils;

import com.alibaba.fastjson.JSONObject;
import com.wellch4n.schedule.domain.BeanTaskDTO;
import com.wellch4n.schedule.enums.TaskTypeEnum;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

public class KeyParserUtils {
    private final static String SPLIT_STRING = "::";

    public static TaskTypeEnum parserKey (String message) {
        String[] keyArr = message.split(SPLIT_STRING);
        if (keyArr.length <= 1) {
            return TaskTypeEnum.IGNORE;
        }

        return TaskTypeEnum.getType(keyArr[0]);
    }

    public static String getParamKey(String message) {
        String[] keyArr = message.split(SPLIT_STRING);
        keyArr[0] = TaskTypeEnum.PARAM.code;

        return String.join(SPLIT_STRING, keyArr);
    }

    public static BeanTaskDTO getParam(String value) {
        return JSONObject.parseObject(value, BeanTaskDTO.class);
    }
}
