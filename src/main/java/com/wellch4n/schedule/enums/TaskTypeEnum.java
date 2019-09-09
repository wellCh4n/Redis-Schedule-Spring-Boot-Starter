package com.wellch4n.schedule.enums;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

public enum TaskTypeEnum {
    /**
     * Runnable任务
     */
    RUNNABLE("runnable"),

    /**
     * Spring Bean任务
     */
    BEAN("bean"),

    /**
     * 跳过的任务
     */
    IGNORE("ignore"),

    // ----------------
    /**
     * 参数
     */
    PARAM("param");

    public String code;

    TaskTypeEnum(String code) {
        this.code = code;
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
