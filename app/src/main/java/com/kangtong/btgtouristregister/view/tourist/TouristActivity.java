package com.kangtong.btgtouristregister.view.tourist;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.huashi.serialport.sdk.HsSerialPortSDK;
import com.huashi.serialport.sdk.IDCardInfo;
import com.kangtong.btgtouristregister.R;
import com.kangtong.btgtouristregister.model.Guide;
import com.kangtong.btgtouristregister.model.Tourist;
import com.kangtong.btgtouristregister.util.DateUtil;
import com.kangtong.btgtouristregister.util.HsUtlis;
import com.kangtong.btgtouristregister.view.excel.ExcelActivity;
import com.kangtong.btgtouristregister.view.guide.RetryWithDelay;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TouristActivity extends AppCompatActivity implements Handler.Callback {

    private static final String EXTRA_GUIDE_NAME = "extra_guide_name";
    private String guideName;
    private static final int MSG_SHOW_PROGRESS_DIALOG = 7;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 8;
    private static final int READER_ID_CARD_SUCCEED = 1;
    // 授权文件路径
    static String filepath = "";
    private ListView listTourist;
    private TextView textNoneTourist;
    List<String> touristString = new ArrayList<>();//导游信息
    private ArrayAdapter<String> mAdapter;

    public static void start(Context context, String guideName) {
        Intent intent = new Intent(context, TouristActivity.class);
        intent.putExtra(EXTRA_GUIDE_NAME, guideName);
        context.startActivity(intent);
    }

    private TextView mTextName;
    private TextView mTextNumber;
    private ToggleButton mTbnAutoRead;


    //下面是关于读卡的内容
    private TextView mTextSex;
    private TextView mTextBirthday;
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
            emitter.onError(new Throwable("对卡失败,请先把卡拿开,再重新读取!"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist);
        setupView();
        setupList();
        setupLoading();
    }


    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        guideName = getIntent().getStringExtra(EXTRA_GUIDE_NAME);
        toolbar.setTitle("当前导游：" + guideName);
        setSupportActionBar(toolbar);

        mTextName = findViewById(R.id.text_name);
        mTextNumber = findViewById(R.id.text_number);
        mTextSex = findViewById(R.id.text_sex);
        mTextBirthday = findViewById(R.id.text_birthday);
        listTourist = findViewById(R.id.list_tourist);
        textNoneTourist = findViewById(R.id.text_none_tourist);

        Button btnRead = findViewById(R.id.btn_read);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReadCard();
            }
        });
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                onReadCard();
            }
        };
        mTbnAutoRead = findViewById(R.id.btn_auto_read);
        mTbnAutoRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    btnRead.setEnabled(false);
                    timer.schedule(timerTask, 3000, 4000);
                } else {
                    btnRead.setEnabled(true);
                    timer.cancel();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExcelActivity.start(TouristActivity.this, guideName, DateUtil.formatDate(new Date()));
            }
        });
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, touristString);
        listTourist.setAdapter(mAdapter);
    }

    private void setupList() {
        List<Tourist> touristList = LitePal.where("guideName=? AND addTime=?", guideName, DateUtil.formatDate(new Date())).order("id desc").find(Tourist.class);
        touristString.clear();
        for (Tourist tourist :
                touristList) {
            touristString.add(tourist.getPeopleName() + "   " + tourist.getNumber());
        }
        if (touristString.isEmpty()) {
            textNoneTourist.setVisibility(View.VISIBLE);
        } else {
            textNoneTourist.setVisibility(View.INVISIBLE);
        }
        mAdapter.notifyDataSetChanged();
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
                        sdk = new HsSerialPortSDK(TouristActivity.this);
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
                    Toast.makeText(TouristActivity.this, "读卡成功", Toast.LENGTH_LONG).show();
                    mHandler.sendMessage(mHandler.obtainMessage(READER_ID_CARD_SUCCEED, info));
                }, throwable -> {
                    throwable.printStackTrace();
                    // 失败提示
                    Toast.makeText(TouristActivity.this, "卡认证失败", Toast.LENGTH_LONG).show();
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
                addTourist(ic);
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 更新 扫描结果 显示
     *
     * @param ic
     */
    @SuppressLint("SetTextI18n")
    private void addTourist(IDCardInfo ic) {
        Tourist tourist = new Tourist();
        tourist.setPeopleName(ic.getPeopleName());
        tourist.setSex(ic.getSex());
        tourist.setEthnic(ic.getPeople());
        tourist.setBirthday(ic.getBirthDay());
        tourist.setAddress(ic.getAddr());
        tourist.setNumber(ic.getIDCard());
        tourist.setDepartment(ic.getDepartment());
        tourist.setStartDate(ic.getStrartDate());
        tourist.setEndDate(ic.getEndDate());
        tourist.setAddTime(new Date());
        tourist.setGuideName(guideName);
        tourist.save();
        mTextName.setText(tourist.getPeopleName());
        mTextNumber.setText(tourist.getNumber());
        mTextSex.setText(tourist.getSex());
        mTextBirthday.setText(tourist.getBirthday());
        setupList();
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
        mHandler.post(() -> Toast.makeText(TouristActivity.this, msg, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 异步初始化 sdk
        openDevice();
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
