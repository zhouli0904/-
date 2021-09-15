package com.test.springbootdemo.api.entity;

import java.io.Serializable;
import java.util.Date;

public class CouponTemplate implements Serializable {
    private Long id;

    private Long activityId;

    private String templateName;

    private Long basePrice;

    private Long preferentialAmount;

    private Date startTime;

    private Date endTime;

    private Long couponNum;

    private Long remainedNum;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName == null ? null : templateName.trim();
    }

    public Long getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Long basePrice) {
        this.basePrice = basePrice;
    }

    public Long getPreferentialAmount() {
        return preferentialAmount;
    }

    public void setPreferentialAmount(Long preferentialAmount) {
        this.preferentialAmount = preferentialAmount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getCouponNum() {
        return couponNum;
    }

    public void setCouponNum(Long couponNum) {
        this.couponNum = couponNum;
    }

    public Long getRemainedNum() {
        return remainedNum;
    }

    public void setRemainedNum(Long remainedNum) {
        this.remainedNum = remainedNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}