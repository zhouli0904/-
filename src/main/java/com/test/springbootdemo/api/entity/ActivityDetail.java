package com.test.springbootdemo.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ActivityDetail implements Serializable {
    private Long id;

    private Date createTime;

    private Date updateTime;

    private String activityName;

    private Date startTime;

    private Date endTime;

    private Integer activityType;

    private Integer state;

    private String extend;

    private static final long serialVersionUID = 1L;

}