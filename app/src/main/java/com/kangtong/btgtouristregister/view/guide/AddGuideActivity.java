package com.kangtong.btgtouristregister.view.guide;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huashi.serialport.sdk.HsSerialPortSDK;
import com.huashi.serialport.sdk.IDCardInfo;
import com.kangtong.btgtouristregister.R;
import com.kangtong.btgtouristregister.model.Guide;
import com.kangtong.btgtouristregister.util.HsUtlis;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddGuideActivity extends AppCompatActivity implements Handler.Callback {

    private static final int MSG_SHOW_PROGRESS_DIALOG = 7;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 8;
    private static final int READER_ID_CARD_SUCCEED = 1;

    // 授权文件路径
    static String filepath = "";
    // 展示用的 tv
    private TextView textView = null;
    // 录入信息的所需的 sdk
    private HsSerialPortSDK sdk = null;
    // loading
    private ProgressDialog mProgressDialog = null;
    // 全局需要用的 handler
    private Handler mHandler = new Handler(this);
    // 正在读卡
    private Disposable reading = null;
    // 读卡
    private FlowableOnSubscribe<IDCardInfo> mReadCardObs = emitter -> {
        // 初始初始化
        if (sdk.Authenticate(1000) == 0) { // == 0成功
            IDCardInfo ic = new IDCardInfo();
            // 读卡
            if (sdk.Read_Card(ic, 2500) == 0) { // 成功
                emitter.onNext(ic);
                emitter.onComplete();
            } else {
                emitter.onError(new Throwable());
            }
        } else {
            emitter.onError(new Throwable());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guide);
        setupView();
        setupRead();
        setupLoading();
    }

    private void setupView() {
        textView = findViewById(R.id.textView);
    }

    private void setupRead() {
        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> onReadCard());
    }

    private void setupLoading() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
    }

    private void showProgressDialog(String title, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, new String[]{title, message}));
    }

    private void dismissProgressDialog() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_DISMISS_PROGRESS_DIALOG));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 异步初始化 sdk
        openDevice();
    }

    private void openDevice() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                int version = Build.VERSION.SDK_INT;
                if (version == 22) {
                    int r = HsUtlis.IDCardPonwer2();
                    if (r != 1) {
                        toast("上电失败");
                    }
                } else {
                    try {
                        HsUtlis.IDCardPonwer1();
                    } catch (IOException e) {
                        toast("上电失败");
                        return;
                    }
                }

                if (sdk == null) {
                    try {
                        sdk = new HsSerialPortSDK(AddGuideActivity.this);
                        showProgressDialog("正在加载", "设备正在准备,请稍后……");
                    } catch (Exception e) {
                        toast("初始化失败");
                    } finally {
                        SystemClock.sleep(1500);
                        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 授权目录
                        int ret = sdk.init("/dev/ttyMT1", 115200, 0);
                        if (ret == 0) {
                            toast("身份证模块准备成功");
                        } else {
                            toast("身份证模块准备失败");
                        }
                        dismissProgressDialog();
                    }
                }
            }
        }.start();
    }

    /**
     * 开始读卡
     */
    private void onReadCard() {
        // 重置
        if (reading != null) {
            if (!reading.isDisposed()) {
                reading.dispose();
            }
            reading = null;
        }
        // dialog
        showProgressDialog("请稍后……", "正在读卡");
        // 开始读卡
        reading = Flowable.create(mReadCardObs, BackpressureStrategy.LATEST)
                .retryWhen(new RetryWithDelay())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(info -> {
                    // 关闭 dialog
                    dismissProgressDialog();
                    // 成功提示
                    Toast.makeText(AddGuideActivity.this, "读卡成功", Toast.LENGTH_LONG).show();
                    mHandler.sendMessage(mHandler.obtainMessage(READER_ID_CARD_SUCCEED, info));
                }, throwable -> {
                    throwable.printStackTrace();
                    // 失败提示
                    Toast.makeText(AddGuideActivity.this, "卡认证失败", Toast.LENGTH_LONG).show();
                    // 关闭 dialog
                    dismissProgressDialog();
                    // 关闭观察者
                    release();
                });
    }

    /**
     * handler 处理
     */
    @Override
    public boolean handleMessage(Message msg) {
        boolean result = false;
        switch (msg.what) {
            case MSG_SHOW_PROGRESS_DIALOG: {
                String[] info = (String[]) msg.obj;
                mProgressDialog.setTitle(info[0]);
                mProgressDialog.setMessage(info[1]);
                mProgressDialog.show();
                result = true;
                break;
            }
            case MSG_DISMISS_PROGRESS_DIALOG: {
                mProgressDialog.dismiss();
                result = true;
                break;
            }
            case READER_ID_CARD_SUCCEED: {
                IDCardInfo ic = (IDCardInfo) msg.obj;
                Guide guide = new Guide();
                guide.setPeopleName(ic.getPeopleName());
                guide.setSex(ic.getSex());
                guide.setEthnic(ic.getPeople());
                guide.setBirthday(ic.getBirthDay());
                guide.setAddress(ic.getAddr());
                guide.setNumber(ic.getIDCard());
                guide.setDepartment(ic.getDepartment());
                guide.setStartDate(ic.getStrartDate());
                guide.setEndDate(ic.getEndDate());
                updateText(guide);
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 更新 扫描结果 显示
     */
    @SuppressLint("SetTextI18n")
    private void updateText(Guide guide) {
        textView.setText(guide.getPeopleName()
                + guide.getSex()
                + guide.getEthnic()
                + guide.getBirthday()
                + guide.getAddress()
                + guide.getNumber()
                + guide.getDepartment()
                + guide.getStartDate()
                + guide.getEndDate());
    }

    /**
     * 释放观察者
     */
    private void release() {
        if (reading != null) {
            if (!reading.isDisposed()) {
                reading.dispose();
            }
        }
        reading = null;
    }

    private void toast(String msg) {
        mHandler.post(() -> Toast.makeText(AddGuideActivity.this, msg, Toast.LENGTH_LONG).show());
    }

    /**
     * 结束时, 释放观察者
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }
}