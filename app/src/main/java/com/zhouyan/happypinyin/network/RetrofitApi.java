package com.zhouyan.happypinyin.network;

import com.zhouyan.happypinyin.entities.BaseEntity;
import com.zhouyan.happypinyin.entities.PrePayModel;
import com.zhouyan.happypinyin.entities.UserInfo;
import com.zhouyan.happypinyin.entities.VideoModel;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


/**
 * Created by Mohaifeng on 2017/6/7.
 */

public interface RetrofitApi {


    /**
     * 请求验证码
     * 参数：手机号码 phone
     */
    @FormUrlEncoded
    @POST("qzVideo/appSendCode.do")
    Observable<BaseEntity<String>> appSendCode(
            @Field("phone") String phone
    );

    /**
     * 注册接口
     * 参数：手机号码 phone 验证码 code 密码 password
     */
    @FormUrlEncoded
    @POST("qzVideo/appregister.do")
    Observable<BaseEntity<String>> appregister(
            @Field("nickName") String nickName,
            @Field("phone") String phone,
            @Field("code") String code,
            @Field("password") String password
    );

    /**
     * 登录接口
     * 参数：手机号码 phone  密码 password  设备id deviceId
     */
    @FormUrlEncoded
    @POST("qzVideo/applogin.do")
    Observable<BaseEntity<UserInfo>> applogin(
            @Field("phone") String phone,
            @Field("password") String password,
            @Field("deviceId") String deviceId

    );

    /**
     * 退出登录
     */
    @FormUrlEncoded
    @POST("qzVideo/appLogout.do")
    Observable<BaseEntity<String>> appLogout(
            @Field("deviceId") String deviceId
    );


    /**
     * 视频列表(所有视频)
     */
    @FormUrlEncoded
    @POST("qzVideo/appVideoList.do")
    Observable<BaseEntity<List<VideoModel>>> appVideoList(@Field("phone") String phone);

    /**
     *  付费接口
     *  isTotalPay,全部视频购买传值yes,  total_fee,支付费用  userId,用户id
     */
    @FormUrlEncoded
    @POST("qzVideo/app/tenpay/prepay.do")
    Observable<BaseEntity<PrePayModel>> prepay(

            @Field("total_fee") double totalFee,
            @Field("videoId") String videoId,
            @Field("isTotalPay") String isTotalPay,
            @Field("userId") String userId
    );

    /**
     * 修改个人信息
     */
    @Multipart
    @POST("qzVideo/appUpdateInfo.do")
    Observable<BaseEntity<UserInfo>> appUpdateInfo(@Part List<MultipartBody.Part> files);


}

