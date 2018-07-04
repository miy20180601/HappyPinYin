package com.zhouyan.happypinyin.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo on 2018/5/3.
 */

public class PrePayModel {

    /**
     * code : 0
     * package : Sign=WXPay
     * appid : wx31bc8a7896499573
     * sign : 92CCD007E088E466C967173FA71B3B71
     * prepayid : wx03160310282585d9a53c53671576870025
     * partnerid :
     * noncestr : 5c5bc7df3d37b2a7ea29e1b47b2bd4ab
     * info : success
     * timestamp : 1525334590
     */

    private int code;
    @SerializedName("package")
    private String packageX;
    private String appid;
    private String sign;
    private String prepayid;
    private String partnerid;
    private String noncestr;
    private String info;
    private String timestamp;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getPackageX() {
        return packageX;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
