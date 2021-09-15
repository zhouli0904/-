package com.test.springbootdemo.api.mapper;

import com.test.springbootdemo.api.entity.CouponTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CouponTemplateMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CouponTemplate record);

    int insertSelective(CouponTemplate record);

    CouponTemplate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CouponTemplate record);

    int updateByPrimaryKey(CouponTemplate record);

    List<CouponTemplate> selectByActivityId(@Param("activityId") List<Long> activityId);

    int updateRemainNum(@Param("templateId") Long templateId);

}