package com.wellch4n.schedule.utils;

import java.util.Date;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

public class TimeUtils {
    public static int deltaTime(Date now, Date time) {
        Long timeNumber = time.getTime() / 1000;
        Long nowNumber = now.getTime() / 1000;
        return Integer.parseInt("" + (timeNumber - nowNumber));
    }
}
