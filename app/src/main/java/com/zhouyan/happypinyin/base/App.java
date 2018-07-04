package com.zhouyan.happypinyin.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by Mohaifeng on 2018/2/2.
 */

public class App extends Application {
    private static Context mAPPcontext;

    //解决jar重复的一句话跟build.gradle里是配套的
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAPPcontext = this;
    }

    public static Context getAPPcontext(){
        return mAPPcontext;
    }
}
