package com.test.springbootdemo.api.service;

public enum ActivityType {

    DISCOUNT(0,"折扣活动"),
    POSTAGE(1, "包邮活动"),
    COUPON(2, "代金券活动");


    private int type;
    private String desc;

    ActivityType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
