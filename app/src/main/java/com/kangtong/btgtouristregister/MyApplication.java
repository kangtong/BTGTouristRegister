package com.kangtong.btgtouristregister;

import android.content.Context;

import org.litepal.LitePalApplication;

import androidx.multidex.MultiDex;

public class MyApplication extends LitePalApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
