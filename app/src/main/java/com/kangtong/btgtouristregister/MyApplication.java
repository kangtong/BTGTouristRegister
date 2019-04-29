package com.kangtong.btgtouristregister;

import android.content.Context;

import com.tencent.bugly.Bugly;

import org.litepal.LitePalApplication;

import androidx.multidex.MultiDex;

public class MyApplication extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "1d6212cd61", false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
