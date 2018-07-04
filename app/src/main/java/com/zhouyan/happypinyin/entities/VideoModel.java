package com.zhouyan.happypinyin.entities;

/**
 * Created by shuyu on 2016/11/11.
 */

public class VideoModel {


    /**
     * videoId : 1
     * file : ??
     * name : Wildlife.wmv
     * url : https://video1-1256005692.cos.ap-guangzhou.myqcloud.com/??/Wildlife.wmv
     * etag : null
     * status : 1
     * createTime : 1524123642000
     * updateTime : 1524123642000
     * createTimeStr : 2018-04-19
     * isMoney : 0
     * much : 0.0
     */

    private String videoId;
    private String file;
    private String name;
    private String url;
    private String imgUrl;
    private Object etag;
    private int status;
    private long createTime;
    private long updateTime;
    private String createTimeStr;
    private int isMoney;
    private double much;
    private Boolean charge;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Boolean getCharge() {
        return charge;
    }

    public void setCharge(Boolean charge) {
        this.charge = charge;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getEtag() {
        return etag;
    }

    public void setEtag(Object etag) {
        this.etag = etag;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public int getIsMoney() {
        return isMoney;
    }

    public void setIsMoney(int isMoney) {
        this.isMoney = isMoney;
    }

    public double getMuch() {
        return much;
    }

    public void setMuch(double much) {
        this.much = much;
    }
}
