package com.test.springbootdemo.api.service.inter;

import com.test.springbootdemo.api.request.CreateActivityReq;
import com.test.springbootdemo.api.request.CreateCouponReq;
import com.test.springbootdemo.api.request.ReviewActivityReq;
import com.test.springbootdemo.common.Result;

public interface ActivityDetailService {

    Result<?> createActivity(CreateActivityReq createActivityReq);

    Result<?> createActivityNew(CreateActivityReq createActivityReq);

    Result<?> createCouponActivity(CreateCouponReq createCouponReq);

    Result<?> reviewActivity(ReviewActivityReq reviewActivityReq);
}
