package com.wellch4n.schedule.utils;

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
}
