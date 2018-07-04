package com.zhouyan.happypinyin.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mohaifeng on 2017/6/7.
 */

public class BaseEntity<E> {


    @SerializedName("status")
    private int status;
    @SerializedName("errorMsg")
    private String errorMsg;
    @SerializedName("body")
    private E body;
    /**
     * status : -1
     * errorMsg : 电话号码不能为空！
     * body : null
     */

    public boolean isSuccess() {
        return status == 0;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public E getBody() {
        return body;
    }

    public void setBody(E body) {
        this.body = body;
    }
}