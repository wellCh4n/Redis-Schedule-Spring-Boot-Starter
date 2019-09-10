package com.wellch4n.schedule.task;

import org.springframework.context.ApplicationContext;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

public interface Task {

    /**
     * 任务具体方法
     * @param taskHandler
     * @param message
     * @param applicationContext
     * @return
     */
    Runnable taskBody(TaskHandler taskHandler, String message, ApplicationContext applicationContext);

}
