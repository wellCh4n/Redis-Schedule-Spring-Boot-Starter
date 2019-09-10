package com.wellch4n.schedule.enums;

import com.wellch4n.schedule.task.Task;
import com.wellch4n.schedule.task.impl.BeanTask;
import com.wellch4n.schedule.task.impl.SimpleTask;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

@SuppressWarnings("unused")
public enum TaskTypeEnum {
    /**
     * Runnable任务
     */
    RUNNABLE("runnable", new SimpleTask()),

    /**
     * Spring Bean任务
     */
    BEAN("bean", new BeanTask()),

    /**
     * 跳过的任务
     */
    IGNORE("ignore", null),

    // ----------------
    /**
     * 参数
     */
    PARAM("param", null);

    public String code;

    public Task taskClazz;

    TaskTypeEnum(String code, Task taskClazz) {
        this.code = code;
        this.taskClazz = taskClazz;
    }

    public Task getTaskClass() {
        return this.taskClazz;
    }

    public static TaskTypeEnum getType(String code) {
        if (code == null || "".equals(code.trim())) {
            return IGNORE;
        }

        for (TaskTypeEnum e : TaskTypeEnum.class.getEnumConstants()) {
            if (code.equalsIgnoreCase(e.code)) {
                return e;
            }
        }

        return IGNORE;
    }
}
