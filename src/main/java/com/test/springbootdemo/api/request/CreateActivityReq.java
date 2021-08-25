package com.test.springbootdemo.api.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CreateActivityReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String activityName;

    private Long startTime;

    private Long endTime;

    /**
     * 0:折扣活动，1:包邮活动，2:代金券活动
     */
    private Integer activityType;

    /**
     * 0:待审核 1:未启用 2:已启用
     */
//    private Integer state;

    private List<Long> goodsList;

}
