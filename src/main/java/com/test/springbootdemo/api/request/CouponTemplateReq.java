package com.test.springbootdemo.api.request;

import lombok.Data;


@Data
public class CouponTemplateReq {
    private String templateName;

    private Long basePrice;

    private Long preferentialAmount;

    private Long startTime;

    private Long endTime;

    private Long couponNum;

}
