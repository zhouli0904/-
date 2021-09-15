package com.test.springbootdemo.api.service.common.goodsDetailPage;

import com.test.springbootdemo.api.entity.ActivityDetail;
import lombok.Data;

import java.util.List;

/**
 * 商品详情页 根据商品id 校验商品，并返回商品价格和商品参加活动的list，用于券列表展示 和 展示活动tags方法
 */
@Data
public class ActivityDTO {

    private Long price;

    private List<ActivityDetail> activityDetailList;

}
