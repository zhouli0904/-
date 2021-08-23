package com.test.springbootdemo.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class UpdateShelfReq {
    /**
     * 上架商品 true 下架商品 false
     */
    private Long id;
    private Boolean shelf;


}
