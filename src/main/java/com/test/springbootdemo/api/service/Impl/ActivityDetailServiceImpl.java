package com.test.springbootdemo.api.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.test.springbootdemo.api.entity.ActivityDetail;
import com.test.springbootdemo.api.entity.CouponTemplate;
import com.test.springbootdemo.api.entity.GoodsActivityInfo;
import com.test.springbootdemo.api.entity.GoodsDetail;
import com.test.springbootdemo.api.mapper.ActivityDetailMapper;
import com.test.springbootdemo.api.mapper.CouponTemplateMapper;
import com.test.springbootdemo.api.mapper.GoodsActivityInfoMapper;
import com.test.springbootdemo.api.mapper.GoodsDetailMapper;
import com.test.springbootdemo.api.request.*;
import com.test.springbootdemo.api.service.activity.CouponActivityDTO;
import com.test.springbootdemo.api.service.activity.CreateActivityDTO;
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

    @Resource
    CouponTemplateMapper couponTemplateMapper;

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
            CreateActivityDTO activityDTO = new CreateActivityDTO();
            activityDTO.setActivityId(activityDetail.getId());
            activityDTO.setGoodsList(createActivityReq.getGoodsList());
            return Result.ofSuccess(activityDTO);
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

        if (createActivityReq instanceof CreateCouponReq) {
            CreateCouponReq createCouponReq = (CreateCouponReq) createActivityReq;
            if (createCouponReq.getCouponTemplateReqList().size() > 5) {
                throw new BizException(507, "券模板的数量大于5");
            }
        }

        // 我把result放入桌子上, 以便后续使用 查到所有有效的商品id
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

        // 我从桌子上拿到result--拿到有效的商品id
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
            CreateActivityDTO activityDTO = new CreateActivityDTO();
            activityDTO.setActivityId(activityDetail.getId());
            activityDTO.setGoodsList(createActivityReq.getGoodsList());
            return Result.ofSuccess(activityDTO);
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
        } else if (createActivityReq instanceof CreatePostageActivityReq) {
            activityDetail.setExtend(JSONObject.toJSONString(((CreatePostageActivityReq) createActivityReq).getExtend()));
        }
        return activityDetail;
    }



    @Override
    @Transactional
    public Result<?> createCouponActivity(CreateCouponReq createCouponReq) {
        checkParam(createCouponReq);
        ActivityDetail activityDetail = convertActivityModel(createCouponReq);

        if (activityDetailMapper.insertSelective(activityDetail) == 1) {
            updateGoodsActivityInfo(createCouponReq, activityDetail);
            List<Long> templateIdList = configureCouponTemplate(createCouponReq, activityDetail);

            CouponActivityDTO activityDTO = new CouponActivityDTO();
            activityDTO.setActivityId(activityDetail.getId());
            activityDTO.setGoodsList(createCouponReq.getGoodsList());
            activityDTO.setTemplateIdList(templateIdList);
            return Result.ofSuccess(activityDTO);
        }
        return Result.ofFail("创建活动失败");
    }


    private List<Long> configureCouponTemplate(CreateCouponReq createCouponReq, ActivityDetail activityDetail){
        List<CouponTemplateReq> couponTemplateReqList = createCouponReq.getCouponTemplateReqList();
        List<Long> templateIdList = new ArrayList<>();
        for (CouponTemplateReq couponTemplateReq : couponTemplateReqList) {
            CouponTemplate couponTemplate = new CouponTemplate();
            couponTemplate.setActivityId(activityDetail.getId());
            couponTemplate.setTemplateName(couponTemplateReq.getTemplateName());
            couponTemplate.setBasePrice(couponTemplateReq.getBasePrice());
            couponTemplate.setPreferentialAmount(couponTemplateReq.getPreferentialAmount());
            couponTemplate.setStartTime(new Date(couponTemplateReq.getStartTime()));
            couponTemplate.setEndTime(new Date(couponTemplateReq.getEndTime()));
            couponTemplate.setCouponNum(couponTemplateReq.getCouponNum());
            couponTemplate.setRemainedNum(couponTemplateReq.getCouponNum()); // 券剩余数量默认是券总数
            couponTemplateMapper.insertSelective(couponTemplate);
            templateIdList.add(couponTemplate.getId());
        }
        return templateIdList;
    }

    @Override
    public Result<?> reviewActivity(ReviewActivityReq reviewActivityReq) {
        Long activityId = reviewActivityReq.getActivityId();
        ActivityDetail select = activityDetailMapper.selectByPrimaryKey(activityId);
        if (select.getState() == 1) {
            return Result.ofFail(601, "活动已启用，无法审核");
        }

        ActivityDetail activityDetail = new ActivityDetail();
        activityDetail.setId(activityId);
        activityDetail.setState(1);
        activityDetailMapper.updateByPrimaryKeySelective(activityDetail);
        return Result.ofSuccess("活动审核成功");
    }
}
