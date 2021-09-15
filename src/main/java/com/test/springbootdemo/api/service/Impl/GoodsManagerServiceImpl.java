package com.test.springbootdemo.api.service.Impl;

import com.test.springbootdemo.api.entity.GoodsDetail;
import com.test.springbootdemo.api.mapper.GoodsDetailMapper;
import com.test.springbootdemo.api.request.AddGoodsReq;
import com.test.springbootdemo.api.request.UpdateGoodsNumReq;
import com.test.springbootdemo.api.request.UpdateShelfReq;
import com.test.springbootdemo.api.service.inter.goodsManagerService;
import com.test.springbootdemo.common.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GoodsManagerServiceImpl implements goodsManagerService {

    @Resource
    GoodsDetailMapper goodsDetailMapper;

    @Override
    public Integer addGoods(AddGoodsReq addGoodsReq) {
        GoodsDetail goodsDetail = new GoodsDetail();
        goodsDetail.setGoodsName(addGoodsReq.getGoodsName());
        goodsDetail.setOriginPrice(addGoodsReq.getOriginPrice());
        goodsDetail.setPrice(addGoodsReq.getOriginPrice());
        if (addGoodsReq.getGoodsNum() == null) {
            goodsDetail.setGoodsNum(0L);
        }else {
            goodsDetail.setGoodsNum(addGoodsReq.getGoodsNum());
        }
        if (addGoodsReq.getOnShelf() == null) {
            goodsDetail.setOnShelf(false);
        }else {
            goodsDetail.setOnShelf(addGoodsReq.getOnShelf());
        }
        return goodsDetailMapper.insertSelective(goodsDetail);

    }

    /**
     * 先判断传入的id 是否存在，存在：上架商品，并且商品有库存 上架成功 下架商品，无需判断库存
     * @param updateShelfReq
     * @return
     */
    @Override
    public Integer updateShelf(UpdateShelfReq updateShelfReq) {
        GoodsDetail select = goodsDetailMapper.selectByPrimaryKey(updateShelfReq.getId());

        if (select == null) {
            return -1;
        }

        if (updateShelfReq.getShelf()) {

            return onShelf(updateShelfReq, select);

        }else {

           return offShelf(updateShelfReq, select);
        }

    }

    private Integer onShelf(UpdateShelfReq updateShelfReq, GoodsDetail select) {
        GoodsDetail goodsDetail = new GoodsDetail();
        if (select.getOnShelf()) {
            return -1;
        }
        if (select.getGoodsNum() == 0) {
            return -1;
        }
        goodsDetail.setId(updateShelfReq.getId());
        goodsDetail.setOnShelf(updateShelfReq.getShelf());
        return goodsDetailMapper.updateByPrimaryKeySelective(goodsDetail);

    }

    private  Integer offShelf(UpdateShelfReq updateShelfReq, GoodsDetail select) {
        GoodsDetail goodsDetail = new GoodsDetail();
        if (!select.getOnShelf()) {
            return -1;
        }
        goodsDetail.setId(updateShelfReq.getId());
        goodsDetail.setOnShelf(updateShelfReq.getShelf());
        return goodsDetailMapper.updateByPrimaryKeySelective(goodsDetail);
    }


    @Override
    public Result<?> addGoodsNum(UpdateGoodsNumReq updateGoodsNumReq) {
        GoodsDetail select = goodsDetailMapper.selectByPrimaryKey(updateGoodsNumReq.getId());
        if (select == null) {
            return Result.ofFail("商品不存在");
        }
        if (updateGoodsNumReq.getGoodsNum() < 0) {
            return Result.ofFail("添加库存失败");
        }
        GoodsDetail goodsDetail = new GoodsDetail();
        goodsDetail.setId(updateGoodsNumReq.getId());
        goodsDetail.setGoodsNum(select.getGoodsNum() + updateGoodsNumReq.getGoodsNum());
        goodsDetailMapper.updateByPrimaryKeySelective(goodsDetail);
        return Result.ofSuccess("添加库存成功");
    }

    @Override
    public Result<?> reduceGoodsNum(UpdateGoodsNumReq updateGoodsNumReq) {
        GoodsDetail select = goodsDetailMapper.selectByPrimaryKey(updateGoodsNumReq.getId());
        if (select == null) {
            return Result.ofFail("商品不存在");
        }
        if (updateGoodsNumReq.getGoodsNum() < 0) {
            return Result.ofFail("扣减库存失败");
        }
        if (select.getGoodsNum() < updateGoodsNumReq.getGoodsNum()) {
            return Result.ofFail("扣减失败，商品库存不足");
        }

        GoodsDetail goodsDetail = new GoodsDetail();
        goodsDetail.setId(updateGoodsNumReq.getId());
        goodsDetail.setGoodsNum(select.getGoodsNum() - updateGoodsNumReq.getGoodsNum());
        goodsDetailMapper.updateByPrimaryKeySelective(goodsDetail);
        return Result.ofSuccess("扣减库存成功");
    }

    @Override
    public Result<GoodsDetail> selectGoods(Long id) {
        GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(id);
        return Result.ofSuccess(goodsDetail);
    }
}
