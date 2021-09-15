package com.test.springbootdemo.api.request;

import com.test.springbootdemo.api.entity.activityExtend.PostageExtend;
import lombok.Data;

@Data
public class CreatePostageActivityReq extends CreateActivityReq{

    private PostageExtend extend;
}
