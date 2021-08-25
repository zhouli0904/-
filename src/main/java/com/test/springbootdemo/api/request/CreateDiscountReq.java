package com.test.springbootdemo.api.request;

import com.test.springbootdemo.api.entity.activityExtend.DiscountExtend;
import lombok.Data;

@Data
public class CreateDiscountReq extends CreateActivityReq{

    private DiscountExtend extend;

}
