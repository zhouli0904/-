package com.test.springbootdemo.api.controller;

import com.test.springbootdemo.api.request.CouponTemplateReq;
import com.test.springbootdemo.api.request.goodsDetailPage.CouponListReq;
import com.test.springbootdemo.api.request.goodsDetailPage.ReceiveCouponReq;
import com.test.springbootdemo.api.request.goodsDetailPage.ShowActivityTagsReq;
import com.test.springbootdemo.api.service.inter.GoodsDetailPageService;
import com.test.springbootdemo.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goodsDetailPage")
public class GoodsDetailPageController {

    @Autowired
    GoodsDetailPageService goodsDetailPageService;

    @PostMapping("/showActivityTags")
    public Result<List<String>> showActivityTags(@RequestBody ShowActivityTagsReq showActivityTagsReq) {
        return goodsDetailPageService.showActivityTags(showActivityTagsReq);
    }

    @PostMapping("/showCouponsList")
    public Result<?> showCouponsList(@RequestBody CouponListReq couponListReq) {
        return goodsDetailPageService.couponList(couponListReq);
    }

    @PostMapping("/receiveCoupon")
    public Result<?> receiveCoupon(@RequestBody ReceiveCouponReq receiveCouponReq) {
        return goodsDetailPageService.receiveCoupon(receiveCouponReq);
    }
}
