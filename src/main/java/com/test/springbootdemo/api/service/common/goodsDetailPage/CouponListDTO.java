package com.test.springbootdemo.api.service.common.goodsDetailPage;

import lombok.Data;

import java.util.Date;

@Data
public class CouponListDTO {

    private String templateName;

    private Long basePrice;

    private Long preferentialAmount;

    private String startTime;

    private String endTime;

    private Long remainedNum;

    private Integer receiveState;

    private Long activityId;

    private Long templateId;

}
