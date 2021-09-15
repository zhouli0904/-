package com.test.springbootdemo.api.service.inter;

import com.test.springbootdemo.api.request.goodsDetailPage.CouponListReq;
import com.test.springbootdemo.api.request.goodsDetailPage.ReceiveCouponReq;
import com.test.springbootdemo.api.request.goodsDetailPage.ShowActivityTagsReq;
import com.test.springbootdemo.common.Result;

import java.util.List;

public interface GoodsDetailPageService {

    Result<List<String>> showActivityTags(ShowActivityTagsReq showActivityTagsReq);

    Result<?> couponList(CouponListReq couponListReq);

    Result<?> receiveCoupon(ReceiveCouponReq receiveCouponReq);
}
