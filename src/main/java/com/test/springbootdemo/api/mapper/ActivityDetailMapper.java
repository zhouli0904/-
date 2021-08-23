package com.test.springbootdemo.api.mapper;

import com.test.springbootdemo.api.entity.ActivityDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ActivityDetail record);

    int insertSelective(ActivityDetail record);

    ActivityDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ActivityDetail record);

    int updateByPrimaryKey(ActivityDetail record);
}