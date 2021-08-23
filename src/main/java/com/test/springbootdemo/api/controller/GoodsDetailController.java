package com.test.springbootdemo.api.controller;

import com.test.springbootdemo.api.request.AddGoodsReq;
import com.test.springbootdemo.api.request.UpdateGoodsNumReq;
import com.test.springbootdemo.api.request.UpdateShelfReq;
import com.test.springbootdemo.api.service.inter.GoodsDetailService;
import com.test.springbootdemo.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsDetailController {

//    @Resource(name = "goodsDetailServiceImpl")
    @Autowired
    GoodsDetailService goodsDetailService;

    @PostMapping("/create")
    public Result<?> create(@RequestBody AddGoodsReq addGoodsReq) {
        if (addGoodsReq == null || addGoodsReq.getGoodsName() == null
                || addGoodsReq.getGoodsName().equals("") || addGoodsReq.getOriginPrice() == null
        || addGoodsReq.getOriginPrice() < 0) {
            return Result.ofFail("参数不正确");
        }

        Integer result = goodsDetailService.addGoods(addGoodsReq);
        if (result != null) {
            return Result.ofSuccess(result);
        }else {
            return Result.ofFail("add failed");
        }
    }

    @PostMapping("updateShelf")
    public Result<?> updateShelf(@RequestBody UpdateShelfReq updateShelfReq) {
        Boolean shelf = updateShelfReq.getShelf();
        Long id = updateShelfReq.getId();
        if (shelf == null || id == null) {
            return Result.ofFail("参数不正确");
        }
        Integer result = goodsDetailService.updateShelf(updateShelfReq);
        if (result == null || result == -1) {
            return Result.ofFail("更新失败");
        }else {
            return Result.ofSuccess("更新成功");
        }
    }

    @PostMapping("/addGoodsNum")
    public Result<?> addGoodsNum(@RequestBody UpdateGoodsNumReq updateGoodsNumReq) {
        return goodsDetailService.addGoodsNum(updateGoodsNumReq);
    }

    @PostMapping("reduceGoodsNum")
    public Result<?> reduceGoodsNum(@RequestBody UpdateGoodsNumReq updateGoodsNumReq) {
        return goodsDetailService.reduceGoodsNum(updateGoodsNumReq);
    }


}
