package com.test.springbootdemo.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.test.springbootdemo.api.entity.GoodsDetail;
import com.test.springbootdemo.api.mapper.GoodsDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/v1")
public class TestDemo {

    @Resource
    GoodsDetailMapper goodsDetailMapper;

    @GetMapping("/select")
    public GoodsDetail getGoodsName(@RequestParam Long id) {
        GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(id);
        return goodsDetail;
    }
}
