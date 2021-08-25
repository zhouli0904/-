package com.test.springbootdemo.api.entity.activityExtend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountExtend implements Serializable {

    private Long basePrice;

    private int discount;

    private Long maxDiscountMoney;
}
