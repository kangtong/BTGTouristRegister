package com.kangtong.btgtouristregister.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String formatDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        return df.format(date);
    }
}
