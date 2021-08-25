package com.test.springbootdemo.api.service.inter;

import com.test.springbootdemo.api.request.CreateActivityReq;
import com.test.springbootdemo.api.request.CreateDiscountReq;
import com.test.springbootdemo.api.request.CreatePostageActivity;
import com.test.springbootdemo.api.request.ShowActivityTagsReq;
import com.test.springbootdemo.common.Result;

public interface ActivityDetailService {

    Result<?> createActivity(CreateActivityReq createActivityReq);

    Result<?> createActivityNew(CreateActivityReq createActivityReq);

    Result<?> showActivityTags(ShowActivityTagsReq showActivityTagsReq);
}
