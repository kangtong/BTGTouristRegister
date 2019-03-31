package com.kangtong.btgtouristregister.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Device {
    private static final String TAG = "callBackInfo";
    private static final int callBackMessageType = 0xff;

    static {
        try {
            //System.loadLibrary("hw/utk_jni");
            System.load("/system/lib64/libutk_jni.so");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private onResultListener listener = null;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case callBackMessageType:
                    if (listener != null)
                        listener.onResult(msg.getData().getString(TAG));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public int FingerprintPower(int on) {
        return DeviceFingerprintPower(on);
    }

    private native int DeviceFingerprintPower(int on);

    public int IDCardPower(int on) {
        return DeviceIDCardPower(on);
    }

    private native int DeviceIDCardPower(int on);

    public void setListener(onResultListener listener) {
        this.listener = listener;
    }

    public void callBack(String strInfo) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(TAG, strInfo);
        message.setData(bundle);
        message.what = callBackMessageType;

        handler.sendMessage(message);
    }
}
