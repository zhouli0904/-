package com.test.springbootdemo.api.controller;

import com.test.springbootdemo.api.request.CreateActivityReq;
import com.test.springbootdemo.api.request.CreateDiscountReq;
import com.test.springbootdemo.api.request.CreatePostageActivity;
import com.test.springbootdemo.api.request.ShowActivityTagsReq;
import com.test.springbootdemo.api.service.inter.ActivityDetailService;
import com.test.springbootdemo.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activity")
public class ActivityDetailController {

    @Autowired
    ActivityDetailService activityDetailService;

//    final ActivityDetailService activityDetailService;
//
//    public ActivityDetailController(ActivityDetailService activityDetailService) {
//        this.activityDetailService = activityDetailService;
//    }

//    @Autowired
//    public void setActivityDetailService(ActivityDetailService activityDetailService) {
//        this.activityDetailService = activityDetailService;
//    }

    @PostMapping("/createActivity")
    public Result<?> createActivity(@RequestBody CreateActivityReq createActivityReq) {
        return activityDetailService.createActivity(createActivityReq);
    }

    @PostMapping("/createDiscountActivity")
    public Result<?> createDiscountActivity(@RequestBody CreateDiscountReq createDiscountReq) {
        return activityDetailService.createActivityNew(createDiscountReq);
    }

    @PostMapping("/createPostageActivity")
    public Result<?> createPostageActivity(@RequestBody CreatePostageActivity createPostageActivity) {
        return activityDetailService.createActivityNew(createPostageActivity);
    }

    @PostMapping("/showActivityTags")
    public Result<?> showActivityTags(@RequestBody ShowActivityTagsReq showActivityTagsReq) {
        return activityDetailService.showActivityTags(showActivityTagsReq);
    }




}
