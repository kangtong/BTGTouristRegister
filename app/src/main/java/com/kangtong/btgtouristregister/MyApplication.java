package com.kangtong.btgtouristregister;

import android.content.Context;

import com.tencent.bugly.Bugly;

import org.litepal.LitePalApplication;

import io.reactivex.plugins.RxJavaPlugins;

public class MyApplication extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "1d6212cd61", false);
        RxJavaPlugins.setErrorHandler(throwable -> {
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

}
