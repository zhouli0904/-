package com.test.springbootdemo.api.service.Impl;

import com.test.springbootdemo.api.entity.ActivityDetail;
import com.test.springbootdemo.api.entity.GoodsActivityInfo;
import com.test.springbootdemo.api.entity.GoodsDetail;
import com.test.springbootdemo.api.mapper.ActivityDetailMapper;
import com.test.springbootdemo.api.mapper.GoodsActivityInfoMapper;
import com.test.springbootdemo.api.mapper.GoodsDetailMapper;
import com.test.springbootdemo.api.request.CreateActivityReq;
import com.test.springbootdemo.api.service.inter.ActivityDetailService;
import com.test.springbootdemo.common.BizException;
import com.test.springbootdemo.common.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ActivityDetailServiceImpl implements ActivityDetailService {

    // 桌子
    private static final ThreadLocal<List<GoodsDetail>> THREAD_LOCAL = ThreadLocal.withInitial(ArrayList::new);
    @Resource
    ActivityDetailMapper activityDetailMapper;

    @Resource
    GoodsActivityInfoMapper goodsActivityInfoMapper;

    @Resource
    GoodsDetailMapper goodsDetailMapper;

    @Override
    @Transactional
    public Result<?> createActivity(CreateActivityReq createActivityReq) {

        checkParam(createActivityReq);

        ActivityDetail activityDetail = new ActivityDetail();
        activityDetail.setActivityName(createActivityReq.getActivityName());
        activityDetail.setStartTime(new Date(createActivityReq.getStartTime()));
        activityDetail.setEndTime(new Date(createActivityReq.getEndTime()));
        activityDetail.setActivityType(createActivityReq.getActivityType());
        activityDetail.setState(0);

        if (activityDetailMapper.insertSelective(activityDetail) == 1) {
            updateGoodsActivityInfo(createActivityReq, activityDetail);
            return Result.ofSuccess(createActivityReq.getGoodsList());
        }

        return Result.ofFail("创建活动失败");
    }

    private void checkParam(CreateActivityReq createActivityReq) {
        List<Long> ids = createActivityReq.getGoodsList();

        if (createActivityReq.getStartTime() <= System.currentTimeMillis()) {
            throw new BizException(504, "活动开始时间小于当前时间");
        }

        if (createActivityReq.getStartTime() >= createActivityReq.getEndTime()) {
            throw new BizException(501, "活动开始时间大于结束时间");
        }

        // 批量查询所有入参的id
        List<GoodsDetail> result = goodsDetailMapper.selectListId(ids);
        if (result.size() < ids.size()) {
            List<Long> unExistedId = compareId(result, ids);
            throw new BizException(503, "不存在的商品id是:" + unExistedId.toString());
        }

        for (GoodsDetail goodsDetail : result) {
           if (goodsDetail.getGoodsNum() == 0) {
               throw new BizException(502, "商品：" + goodsDetail.getId() + "无库存");
           }
           if (!goodsDetail.getOnShelf()) {
               throw new BizException(505, "商品：" + goodsDetail.getId() + "未上架");
           }
        }

        // 我把result放入桌子上, 以便后续使用
        THREAD_LOCAL.set(result);

    }

    private List<Long> compareId(List<GoodsDetail> result, List<Long> goodsListReq) {
        List<Long> unExisted = new ArrayList<>();
        List<Long> existed = new ArrayList<>();
        for (GoodsDetail goodsDetail : result) {
            existed.add(goodsDetail.getId());
        }

        for (Long id : goodsListReq) {
            if (!existed.contains(id)) {
                unExisted.add(id);
            }
        }
        return unExisted;
    }


    /**
     * 1、拿到入参的商品id的list 2、商品活动关系表中增加这些商品的记录
     * @param createActivityReq
     */
    private void updateGoodsActivityInfo(CreateActivityReq createActivityReq, ActivityDetail activityDetail) {

        // 我从桌子上拿到result
        List<GoodsDetail> goodsDetails = THREAD_LOCAL.get();
        List<GoodsActivityInfo> list = new ArrayList<>();

        for (GoodsDetail goodsDetail : goodsDetails) {
            GoodsActivityInfo goodsActivityInfo = new GoodsActivityInfo();
            goodsActivityInfo.setActivityId(activityDetail.getId());
            goodsActivityInfo.setGoodsId(goodsDetail.getId());
            goodsActivityInfo.setActivityType(createActivityReq.getActivityType());
            goodsActivityInfo.setGoodsName(goodsDetail.getGoodsName());
            list.add(goodsActivityInfo);
        }
        goodsActivityInfoMapper.insertBatch(list);
    }

}
