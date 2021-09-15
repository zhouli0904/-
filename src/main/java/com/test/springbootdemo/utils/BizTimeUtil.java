package com.test.springbootdemo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BizTimeUtil {

    public static String dateFormat(Date date, String pattern) {
        if (pattern == null || pattern.equals("")) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }
}
