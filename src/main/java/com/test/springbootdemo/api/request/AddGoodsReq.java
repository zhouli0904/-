package com.test.springbootdemo.api.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddGoodsReq implements Serializable {

    private String goodsName;

    private Long originPrice;

    private Long goodsNum;

    private Boolean onShelf;

}
