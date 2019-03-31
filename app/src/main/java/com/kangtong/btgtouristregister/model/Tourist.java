package com.kangtong.btgtouristregister.model;

import com.kangtong.btgtouristregister.util.DateUtil;

import java.util.Date;

public class Tourist extends IDCard {
    private String addTime;
    private String guideName;

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }


    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = DateUtil.formatDate(addTime);
    }
}
