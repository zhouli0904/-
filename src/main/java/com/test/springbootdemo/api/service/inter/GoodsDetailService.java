package com.test.springbootdemo.api.service.inter;

import com.test.springbootdemo.api.request.AddGoodsReq;
import com.test.springbootdemo.api.request.UpdateGoodsNumReq;
import com.test.springbootdemo.api.request.UpdateShelfReq;
import com.test.springbootdemo.common.Result;

public interface GoodsDetailService {

    Integer addGoods(AddGoodsReq addGoodsReq);

    Integer updateShelf(UpdateShelfReq updateShelfReq);

    Result<?> addGoodsNum(UpdateGoodsNumReq updateGoodsNumReq);

    Result<?> reduceGoodsNum(UpdateGoodsNumReq updateGoodsNumReq);

}
