package com.test.springbootdemo.api.service.activity;

import com.test.springbootdemo.api.service.activity.CreateActivityDTO;
import lombok.Data;

import java.util.List;

@Data
public class CouponActivityDTO extends CreateActivityDTO {

    private List<Long> templateIdList;
}
