package com.test.springbootdemo.api.mapper;

import com.test.springbootdemo.api.entity.ActivityDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ActivityDetail record);

    int insertSelective(ActivityDetail record);

    ActivityDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ActivityDetail record);

    int updateByPrimaryKey(ActivityDetail record);

    List<ActivityDetail> selectByActivityIdBatch(@Param("ids") List<Long> ids);

}