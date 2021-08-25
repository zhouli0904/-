package com.test.springbootdemo.api.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.test.springbootdemo.api.entity.ActivityDetail;
import com.test.springbootdemo.api.entity.GoodsActivityInfo;
import com.test.springbootdemo.api.entity.GoodsDetail;
import com.test.springbootdemo.api.entity.activityExtend.DiscountExtend;
import com.test.springbootdemo.api.entity.activityExtend.PostageExtend;
import com.test.springbootdemo.api.mapper.ActivityDetailMapper;
import com.test.springbootdemo.api.mapper.GoodsActivityInfoMapper;
import com.test.springbootdemo.api.mapper.GoodsDetailMapper;
import com.test.springbootdemo.api.request.CreateActivityReq;
import com.test.springbootdemo.api.request.CreateDiscountReq;
import com.test.springbootdemo.api.request.CreatePostageActivity;
import com.test.springbootdemo.api.request.ShowActivityTagsReq;
import com.test.springbootdemo.api.service.ActivityType;
import com.test.springbootdemo.api.service.inter.ActivityDetailService;
import com.test.springbootdemo.common.BizException;
import com.test.springbootdemo.common.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
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
     *
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

    @Transactional
    @Override
    public Result<?> createActivityNew(CreateActivityReq createActivityReq) {
        checkParam(createActivityReq);
        ActivityDetail activityDetail = convertActivityModel(createActivityReq);

        if (activityDetailMapper.insertSelective(activityDetail) == 1) {
            updateGoodsActivityInfo(createActivityReq, activityDetail);
            return Result.ofSuccess(createActivityReq.getGoodsList());
        }
        return Result.ofFail("创建活动失败");
    }


    private ActivityDetail convertActivityModel(CreateActivityReq createActivityReq) {
        ActivityDetail activityDetail = new ActivityDetail();
        activityDetail.setActivityName(createActivityReq.getActivityName());
        activityDetail.setStartTime(new Date(createActivityReq.getStartTime()));
        activityDetail.setEndTime(new Date(createActivityReq.getEndTime()));
        activityDetail.setActivityType(createActivityReq.getActivityType());
        activityDetail.setState(0);
        if (createActivityReq instanceof CreateDiscountReq) {
            activityDetail.setExtend(JSONObject.toJSONString(((CreateDiscountReq) createActivityReq).getExtend()));
        } else if (createActivityReq instanceof CreatePostageActivity) {
            activityDetail.setExtend(JSONObject.toJSONString(((CreatePostageActivity) createActivityReq).getExtend()));
        }
        return activityDetail;
    }

    @Override
    public Result<?> showActivityTags(ShowActivityTagsReq showActivityTagsReq) {
        List<String> tagList = new ArrayList<>();

        Long goodsId = showActivityTagsReq.getGoodsId();
        // 根据商品id 到商品详情表中 查到 商品的price
        GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(goodsId);
        if (goodsDetail == null) {
            return Result.ofFail(508, "此商品不存在");
        }
        Long goodsPrice = goodsDetail.getPrice();

        // 过滤 无价格 下架 无库存商品
        if (goodsPrice == null || !goodsDetail.getOnShelf() || goodsDetail.getGoodsNum() == 0) {
            return Result.ofFail(506, "商品无价格或下架或无库存");
        }

        // 根据商品id 到商品活动信息表中  查到所有的活动id
        List<Long> activityIdList = goodsActivityInfoMapper.selectActivityIdByGoodsId(goodsId);
        if (activityIdList == null) {
            return Result.ofFail(507, goodsId + "未参加活动");
        }

        // 根据活动id 到活动详情表中 查到 活动详情
        List<ActivityDetail> activityDetailList = activityDetailMapper.selectByActivityIdBatch(activityIdList);

        // 查询【折扣标签】
        generateDiscountTag(tagList, activityDetailList, goodsPrice);

        // 查询【包邮标签】
        generatePostageTag(tagList, activityDetailList, goodsPrice);

        return Result.ofSuccess(tagList);
    }

    private void generateDiscountTag(List<String> tagList, List<ActivityDetail> activityDetailList, Long goodsPrice) {

        List<Integer> discountList = new ArrayList<>();

        for (ActivityDetail activityDetail : activityDetailList) {
            // 过滤 非折扣活动 失效活动
            if ((activityDetail.getActivityType() != ActivityType.DISCOUNT.getType()) || (activityDetail.getEndTime().getTime() < System.currentTimeMillis())) {
                continue;
            }
            String extend = activityDetail.getExtend();
            if (extend == null) {
                continue;
            }
            DiscountExtend discountExtend = JSONObject.parseObject(extend, DiscountExtend.class);

            if (discountExtend != null) {
                if (goodsPrice >= discountExtend.getBasePrice()) {
                    discountList.add(discountExtend.getDiscount());
                }
            }
        }

        tagList.add("限时折扣" + handleDiscountNum(Collections.min(discountList)) + "折");
    }

    // 80 处理成 8; 66 不做处理
    private String handleDiscountNum(int discount) {
        if (discount % 10 == 0) {
            discount = discount / 10;
            return String.valueOf(discount);
        }
        return String.valueOf(discount);
    }

    private void generatePostageTag(List<String> tagList, List<ActivityDetail> activityDetailList, Long goodsPrice) {
        List<Long> basePriceList = new ArrayList<>();
        for (ActivityDetail activityDetail : activityDetailList) {
            // 过滤 非包邮活动 失效活动
            if (activityDetail.getActivityType() != ActivityType.POSTAGE.getType() || activityDetail.getEndTime().getTime() < System.currentTimeMillis()) {
                continue;
            }
            String extend = activityDetail.getExtend();
            if (extend == null) {
                continue;
            }
            PostageExtend postageExtend = JSONObject.parseObject(extend, PostageExtend.class);
            if (postageExtend != null) {
                basePriceList.add(postageExtend.getBasePrice());
            }
        }
        if (goodsPrice >= Collections.min(basePriceList)) {
            tagList.add("包邮");
        }

    }


}
