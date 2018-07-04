package com.zhouyan.happypinyin.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Administrator on 2016/12/20 0020.
 * 拦截器，用于添加请求头
 */
public class NetWorkInterceptor implements Interceptor {

    private static final String TAG = "NetWorkInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest = request;

//        String tokenUserId = SharedUtil.getString(App.getAppContext(), "userId");
//        String accessToken = SharedUtil.getString(App.getAppContext(), "accessToken");
//
//        if (!"string".equals(tokenUserId) && !"string".equals(accessToken)) {
//            newRequest = request.newBuilder()
//                    .addHeader("tokenUserId", tokenUserId)
//                    .addHeader("accessToken", accessToken)
//                    .build();
//
//        }
        //Log.d("ssss",tokenUserId+" :"+accessToken);

        return chain.proceed(newRequest);
    }
}