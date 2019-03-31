package com.kangtong.btgtouristregister.model;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;
import org.litepal.crud.LitePalSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IDCard extends LitePalSupport {
    private String peopleName;//姓名
    private String sex;//性别
    private String ethnic;//民族
    private String birthday;//生日
    private String address;//地址
    private String number;//身份证号
    private String department;//公安分局
    private String startDate;//有效期限开始
    private String endDate;//有效时间结束

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPeopleName() {
        return peopleName;
    }

    public void setPeopleName(String peopleName) {
        this.peopleName = peopleName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEthnic() {
        return ethnic;
    }

    public void setEthnic(String ethnic) {
        this.ethnic = ethnic;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");// 设置日期格式
        this.birthday = df.format(birthday);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @NotNull
    @Override
    public String toString() {
        return "IDCard{" +
                "peopleName='" + peopleName + '\'' +
                ", sex='" + sex + '\'' +
                ", ethnic='" + ethnic + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", number='" + number + '\'' +
                ", department='" + department + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
