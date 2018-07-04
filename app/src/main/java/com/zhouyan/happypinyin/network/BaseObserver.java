package com.zhouyan.happypinyin.network;

import android.util.Log;

import com.zhouyan.happypinyin.entities.BaseEntity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * Created by Mohaifeng on 2017/6/7.
 */

public abstract class BaseObserver<T> implements Observer<BaseEntity<T>> {

    private static final String TAG = "BaseObserver";

    protected BaseObserver() {
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(BaseEntity<T> value) {
        if (value.isSuccess()) {
            T t = value.getBody();
            String msg = value.getErrorMsg();
            onHandleSuccess(t,msg);
        } else {
            onHandleError(value.getStatus(),value.getErrorMsg());
        }
    }


    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "error:" + e.toString());
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete");
    }


    protected abstract void onHandleSuccess(T t, String msg);

    protected void onHandleError(int statusCode, String msg) {

    }
}
