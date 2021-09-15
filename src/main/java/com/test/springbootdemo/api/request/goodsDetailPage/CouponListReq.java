package com.test.springbootdemo.api.request.goodsDetailPage;

import lombok.Data;

@Data
public class CouponListReq {

    private Long goodsId;

    private Long userId;
}
