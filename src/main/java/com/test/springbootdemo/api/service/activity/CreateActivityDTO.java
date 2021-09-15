package com.test.springbootdemo.api.service.activity;

import lombok.Data;

import java.util.List;

@Data
public class CreateActivityDTO {

    private Long activityId;

    private List<Long> goodsList;
}
