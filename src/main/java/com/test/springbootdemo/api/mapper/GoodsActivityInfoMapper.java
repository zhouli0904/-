package com.test.springbootdemo.api.mapper;

import com.test.springbootdemo.api.entity.GoodsActivityInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsActivityInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GoodsActivityInfo record);

    int insertSelective(GoodsActivityInfo record);

    GoodsActivityInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoodsActivityInfo record);

    int updateByPrimaryKey(GoodsActivityInfo record);

    int insertBatch(@Param("list") List<GoodsActivityInfo> list);
}