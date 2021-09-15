package com.test.springbootdemo.api.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.test.springbootdemo.api.entity.ActivityDetail;
import com.test.springbootdemo.api.entity.CouponTemplate;
import com.test.springbootdemo.api.entity.GoodsDetail;
import com.test.springbootdemo.api.entity.ReceivedCouponRecord;
import com.test.springbootdemo.api.entity.activityExtend.DiscountExtend;
import com.test.springbootdemo.api.entity.activityExtend.PostageExtend;
import com.test.springbootdemo.api.mapper.*;
import com.test.springbootdemo.api.request.goodsDetailPage.CouponListReq;
import com.test.springbootdemo.api.request.goodsDetailPage.ReceiveCouponReq;
import com.test.springbootdemo.api.request.goodsDetailPage.ShowActivityTagsReq;
import com.test.springbootdemo.api.service.ActivityType;
import com.test.springbootdemo.api.service.common.goodsDetailPage.ActivityDTO;
import com.test.springbootdemo.api.service.common.goodsDetailPage.CouponListDTO;
import com.test.springbootdemo.api.service.inter.GoodsDetailPageService;
import com.test.springbootdemo.common.BizException;
import com.test.springbootdemo.common.Result;
import com.test.springbootdemo.utils.BizTimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsDetailPageServiceImpl implements GoodsDetailPageService {

    private static final ThreadLocal<ActivityDTO> THREAD_LOCAL = ThreadLocal.withInitial(ActivityDTO::new);

    @Resource
    ActivityDetailMapper activityDetailMapper;

    @Resource
    GoodsActivityInfoMapper goodsActivityInfoMapper;

    @Resource
    GoodsDetailMapper goodsDetailMapper;

    @Resource
    CouponTemplateMapper couponTemplateMapper;

    @Resource
    ReceivedCouponRecordMapper receivedCouponRecordMapper;

    @Override
    public Result<List<String>> showActivityTags(ShowActivityTagsReq showActivityTagsReq) {
        List<String> tagList = new ArrayList<>();

        Long goodsId = showActivityTagsReq.getGoodsId();

        getActivityDTO(goodsId);

        ActivityDTO activityDTO = THREAD_LOCAL.get();
        Long goodsPrice = activityDTO.getPrice();
        List<ActivityDetail> activityDetailList = activityDTO.getActivityDetailList();

        // 查询【折扣标签】
        generateDiscountTag(tagList, activityDetailList, goodsPrice);

        // 查询【包邮标签】
        generatePostageTag(tagList, activityDetailList, goodsPrice);

        return Result.ofSuccess(tagList);
    }

    private void getActivityDTO(Long goodsId) {
        // 根据商品id 到商品详情表中 查到 商品的price
        GoodsDetail goodsDetail = goodsDetailMapper.selectByPrimaryKey(goodsId);
        if (goodsDetail == null) {
            throw new BizException(508, "此商品不存在");
        }
        Long goodsPrice = goodsDetail.getPrice();

        // 过滤 无价格 下架 无库存商品
        if (goodsPrice == null || !goodsDetail.getOnShelf() || goodsDetail.getGoodsNum() == 0) {
            throw new BizException(506, "商品无价格或下架或无库存");
        }

        // 根据商品id 到商品活动信息表中  查到所有的活动id
        List<Long> activityIdList = goodsActivityInfoMapper.selectActivityIdByGoodsId(goodsId);
        if (CollectionUtils.isEmpty(activityIdList)) {
            throw new BizException(507, goodsId + "未参加活动");
        }

        // 根据活动id 到活动详情表中 查到 活动详情
        List<ActivityDetail> activityDetailList = activityDetailMapper.selectByActivityIdBatch(activityIdList);

        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setPrice(goodsPrice);
        activityDTO.setActivityDetailList(activityDetailList);
        THREAD_LOCAL.set(activityDTO);
    }

    private void generateDiscountTag(List<String> tagList, List<ActivityDetail> activityDetailList, Long goodsPrice) {

        List<Integer> discountList = activityDetailList.stream()
                .filter(activityDetail -> (activityDetail.getActivityType() == ActivityType.DISCOUNT.getType()))
                .filter(activityDetail -> activityDetail.getEndTime().getTime() >= System.currentTimeMillis())
                .filter(activityDetail -> activityDetail.getState() != 0)
                .filter(activityDetail -> activityDetail.getExtend() != null)
                .map(ActivityDetail::getExtend)
                .filter(extend -> JSONObject.parseObject(extend, DiscountExtend.class).getBasePrice() < goodsPrice)
                .map(extend -> JSONObject.parseObject(extend, DiscountExtend.class).getDiscount())
                .collect(Collectors.toList());

//        Optional<List<Integer>> optionalList = Optional.ofNullable(discountList);
//        optionalList.ifPresent(list -> tagList.add("限时折扣" + handleDiscountNum(Collections.min(list)) + "折"));

        if (!CollectionUtils.isEmpty(discountList)) {
            tagList.add("限时折扣" + handleDiscountNum(Collections.min(discountList)) + "折");
        }

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

        List<Long> basePriceList = activityDetailList.stream()
                .filter(activityDetail -> activityDetail.getActivityType() == ActivityType.POSTAGE.getType())
                .filter(activityDetail -> activityDetail.getEndTime().getTime() >= System.currentTimeMillis())
                .filter(activityDetail -> activityDetail.getExtend() != null)
                .map(ActivityDetail::getExtend)
                .map(extend -> JSONObject.parseObject(extend, PostageExtend.class).getBasePrice())
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(basePriceList)) {
            if (goodsPrice >= Collections.min(basePriceList)) {
                tagList.add("包邮");
            }
        }

    }

    @Override
    public Result<?> couponList(CouponListReq couponListReq) {

        Long goodsId = couponListReq.getGoodsId();

        getActivityDTO(goodsId);

        ActivityDTO activityDTO = THREAD_LOCAL.get();
        List<ActivityDetail> activityDetailList = activityDTO.getActivityDetailList();

        // 过滤不符合条件的活动id 得到的有效活动Id列表
        List<Long> activityIdList = activityDetailList.stream()
                .filter(activityDetail -> activityDetail.getActivityType() == ActivityType.COUPON.getType())
                .filter(activityDetail -> activityDetail.getEndTime().getTime() >= System.currentTimeMillis())
                .filter(activityDetail -> activityDetail.getState() != 0)
                .map(ActivityDetail::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(activityIdList)) {
            return Result.ofFail(5111, "未参加代金券活动，无优惠券");
        }

        // 券模板 list(已按优惠金额降序排列)
        List<CouponTemplate> couponTemplateList = couponTemplateMapper.selectByActivityId(activityIdList);

        List<Long> receivedSuccessTemplateIdList = receivedCouponRecordMapper.selectTemplateIdByUserIdAndState(couponListReq.getUserId(), 1);

        List<CouponListDTO> showCouponList = new ArrayList<>();

        for (CouponTemplate couponTemplate : couponTemplateList) {

            CouponListDTO couponListDTO = new CouponListDTO();
            couponListDTO.setTemplateName(couponTemplate.getTemplateName());
            couponListDTO.setBasePrice(couponTemplate.getBasePrice());
            couponListDTO.setPreferentialAmount(couponTemplate.getPreferentialAmount());
            couponListDTO.setStartTime(BizTimeUtil.dateFormat(couponTemplate.getStartTime(), ""));
            couponListDTO.setEndTime(BizTimeUtil.dateFormat(couponTemplate.getEndTime(), ""));
            couponListDTO.setRemainedNum(couponTemplate.getRemainedNum());
            couponListDTO.setActivityId(couponTemplate.getActivityId());
            couponListDTO.setTemplateId(couponTemplate.getId());
            if (!receivedSuccessTemplateIdList.contains(couponTemplate.getId())) {
                couponListDTO.setReceiveState(0);
            }else {
                couponListDTO.setReceiveState(1);
            }
            showCouponList.add(couponListDTO);
        }

        // 先按活动状态排序 再按优惠金额排序
        List<CouponListDTO> showCouponListSorted = showCouponList.stream()
                .sorted(Comparator.comparing(CouponListDTO::getReceiveState)
                        .thenComparing(Comparator.comparing(CouponListDTO::getPreferentialAmount).reversed()))
                .collect(Collectors.toList());

        return Result.ofSuccess(showCouponListSorted);
    }

    @Override
    @Transactional
    public Result<?> receiveCoupon(ReceiveCouponReq receiveCouponReq) {
        Long templateId = receiveCouponReq.getTemplateId();
        Long userId = receiveCouponReq.getUserId();

        CouponTemplate couponTemplate = couponTemplateMapper.selectByPrimaryKey(templateId);
        if (couponTemplate == null) {
            throw new BizException(509, "券模板id不存在");
        }
        if (couponTemplate.getRemainedNum() == 0) {
            throw new BizException(510, "券剩余数量为0");
        }
        Integer state = receivedCouponRecordMapper.selectStateByUserIdAndTemplateId(userId, templateId);
        if (state != null && state == 1) {
            throw new BizException(511, "该张券已领取,不能重复领取");
        }
        // 领取成功 领取记录表增加一天领取状态为1的记录
        // 领取失败 领取记录表增加一天领取状态为0的记录
        if (!receiveCouponMethod(receiveCouponReq)) {
            addReceivedCouponRecord(receiveCouponReq, 0);
            return Result.ofFail(513, "领取失败");
        }
        return Result.ofSuccess("领取成功");
    }

    private Boolean receiveCouponMethod(ReceiveCouponReq receiveCouponReq) {
        Long templateId = receiveCouponReq.getTemplateId();
        // 更新券模板表中剩余数量字段
        int update = couponTemplateMapper.updateRemainNum(templateId);
        if (update == 1) {
            // 领取记录表中增加记录
            int record = addReceivedCouponRecord(receiveCouponReq, 1);
            return record == 1;
        }
        return false;
    }

    private int addReceivedCouponRecord(ReceiveCouponReq receiveCouponReq, int state) {
        ReceivedCouponRecord record = new ReceivedCouponRecord();
        record.setUserId(receiveCouponReq.getUserId());
        record.setTemplateId(receiveCouponReq.getTemplateId());
        record.setGoodsId(receiveCouponReq.getGoodsId());
        record.setActivityId(receiveCouponReq.getActivityId());
        record.setReceiveState(state);
        return receivedCouponRecordMapper.insertSelective(record);
    }


}

