package com.zhouyan.happypinyin.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mo on 2018/4/16.
 */

public class UserInfo implements Serializable{

    /**
     * userId : 0
     * phone : 13422202923
     * password : 123456
     * nickName : null
     * sex : 0
     * birthday : null
     * status : 0
     * photo : null
     * type : 0
     * createTime : null
     * updateTime : null
     * birthdayStr : null
     * createTimeStr : null
     */

    private String userId;
    private String phone;
    private String password;
    private String nickName;
    private int sex;
    private long birthday;
    private int status;
    private String photo;
    private int type;
    private Long createTime;
    private Long updateTime;
    private String birthdayStr;
    private String createTimeStr;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getBirthdayStr() {
        return birthdayStr;
    }

    public void setBirthdayStr(String birthdayStr) {
        this.birthdayStr = birthdayStr;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }
}
