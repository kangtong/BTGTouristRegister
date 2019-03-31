package com.kangtong.btgtouristregister.util;

/**
 * Created by luan on 16/8/4.
 */
public class HS550 {

//    public native int Open();

    static {
        System.loadLibrary("hs550");
    }

    public native int PowerOnUSB();

    public native int PowerOffUSB();

}
