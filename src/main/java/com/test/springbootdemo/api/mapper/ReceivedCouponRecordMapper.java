package com.test.springbootdemo.api.mapper;

import com.test.springbootdemo.api.entity.ReceivedCouponRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReceivedCouponRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ReceivedCouponRecord record);

    int insertSelective(ReceivedCouponRecord record);

    ReceivedCouponRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ReceivedCouponRecord record);

    int updateByPrimaryKey(ReceivedCouponRecord record);

    List<Long> selectTemplateIdByUserIdAndState(@Param("userId") Long userId, @Param("receiveState") Integer receiveState);

    Integer selectStateByUserIdAndTemplateId(@Param("userId") Long userId, @Param("templateId") Long templateId);
}