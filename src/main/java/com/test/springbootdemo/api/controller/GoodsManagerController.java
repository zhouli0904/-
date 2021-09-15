package com.test.springbootdemo.api.controller;

import com.test.springbootdemo.api.entity.GoodsDetail;
import com.test.springbootdemo.api.request.AddGoodsReq;
import com.test.springbootdemo.api.request.UpdateGoodsNumReq;
import com.test.springbootdemo.api.request.UpdateShelfReq;
import com.test.springbootdemo.api.service.inter.goodsManagerService;
import com.test.springbootdemo.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods")
public class GoodsManagerController {

//    @Resource(name = "goodsDetailServiceImpl")
    @Autowired
    goodsManagerService goodsManagerService;

    @GetMapping("/select")
    public Result<GoodsDetail> select(@RequestParam Long id) {
        return goodsManagerService.selectGoods(id);
    }

    @PostMapping("/create")
    public Result<?> create(@RequestBody AddGoodsReq addGoodsReq) {
        if (addGoodsReq == null || addGoodsReq.getGoodsName() == null
                || addGoodsReq.getGoodsName().equals("") || addGoodsReq.getOriginPrice() == null
        || addGoodsReq.getOriginPrice() < 0) {
            return Result.ofFail("参数不正确");
        }

        Integer result = goodsManagerService.addGoods(addGoodsReq);
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
        Integer result = goodsManagerService.updateShelf(updateShelfReq);
        if (result == null || result == -1) {
            return Result.ofFail("更新失败");
        }else {
            return Result.ofSuccess("更新成功");
        }
    }

    @PostMapping("/addGoodsNum")
    public Result<?> addGoodsNum(@RequestBody UpdateGoodsNumReq updateGoodsNumReq) {
        return goodsManagerService.addGoodsNum(updateGoodsNumReq);
    }

    @PostMapping("reduceGoodsNum")
    public Result<?> reduceGoodsNum(@RequestBody UpdateGoodsNumReq updateGoodsNumReq) {
        return goodsManagerService.reduceGoodsNum(updateGoodsNumReq);
    }


}
