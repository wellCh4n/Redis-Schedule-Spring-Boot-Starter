package com.wellch4n.schedule.domain;

import lombok.Data;

import java.util.List;

/**
 * @author wellCh4n
 * 找到银弹，然后开枪
 */

@Data
public class BeanTaskDTO {
    private String bean;

    private String method;

    private List<Object> param;
}
