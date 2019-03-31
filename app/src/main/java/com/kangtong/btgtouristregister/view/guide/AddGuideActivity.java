package com.kangtong.btgtouristregister.view.guide;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huashi.serialport.sdk.HsSerialPortSDK;
import com.huashi.serialport.sdk.IDCardInfo;
import com.kangtong.btgtouristregister.R;
import com.kangtong.btgtouristregister.model.Guide;
import com.kangtong.btgtouristregister.util.HsUtlis;

import java.io.IOException;

public class AddGuideActivity extends AppCompatActivity {

    private static final int MSG_SHOW_PROGRESS_DIALOG = 7;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 8;
    private static final int MSG_SHOW_ERROR = 9;
    private static final int MSG_SHOW_INFO = 6;
    private static final int READER_IDCARD_SUCCEED = 1;
    private static final int PHOTO_SUCCEED = 2;
    static String filepath = "";
    private TextView textName;
    private EditText editName;
    private HsSerialPortSDK sdk;
    private ProgressDialog mProgressDialog;
    private Guide mGuide;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SHOW_PROGRESS_DIALOG: {
                    String[] info = (String[]) msg.obj;
                    mProgressDialog.setTitle(info[1]);
                    mProgressDialog.setMessage(info[1]);
                    // TODO: 2019/3/31 弹出进度条
//                    mProgressDialog.show();
                    break;
                }
                case MSG_DISMISS_PROGRESS_DIALOG: {
                    // TODO: 2019/3/31 关闭进度条
//                    mProgressDialog.dismiss();
                    break;
                }
                case MSG_SHOW_ERROR: {
                    showDialog(0, (Bundle) msg.obj);
                    break;
                }
                case MSG_SHOW_INFO: {
                    Toast.makeText(AddGuideActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                case READER_IDCARD_SUCCEED: {
                    IDCardInfo ic = (IDCardInfo) msg.obj;

                    updateText(ic);
                    break;
                }
            }
        }
    };

    private void updateText(IDCardInfo ic) {
        mGuide.setPeopleName(ic.getPeopleName());
        mGuide.setSex(ic.getSex());
        mGuide.setEthnic(ic.getPeople());
        mGuide.setBirthday(ic.getBirthDay());
        mGuide.setAddress(ic.getAddr());
        mGuide.setNumber(ic.getIDCard());
        mGuide.setDepartment(ic.getDepartment());
        mGuide.setStartDate(ic.getStrartDate());
        mGuide.setEndDate(ic.getEndDate());
        editName.setText(mGuide.getPeopleName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guide);
        textName = findViewById(R.id.text_name);
        editName = findViewById(R.id.edit_name);
        Button btnGetIDCard = findViewById(R.id.btn_guide_get);
        btnGetIDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IDCardTask().execute();
            }
        });
        Button btnEnter = findViewById(R.id.btn_enter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guide guide = new Guide();
                guide = mGuide;
                guide.setPeopleName(editName.getText().toString());
                guide.save();
                finish();
            }
        });
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sdk != null) {
            return;
        }
        openDevice();
    }

    private void openDevice() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                int version = Integer.parseInt(Build.VERSION.SDK);
                if (version == 22) {
                    int r = HsUtlis.IDCardPonwer2();
                    if (r != 1) {
                        Toast.makeText(AddGuideActivity.this, "上电失败", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        HsUtlis.IDCardPonwer1();
                    } catch (IOException e) {
                        Toast.makeText(AddGuideActivity.this, "上电失败", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if (sdk == null) {
                    try {
                        sdk = new HsSerialPortSDK(AddGuideActivity.this);
                        showProgressDialog("正在加载", "设备正在准备,请稍后……");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "初始化失败", Toast.LENGTH_LONG).show();
                    } finally {
                        SystemClock.sleep(1500);
                        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 授权目录
                        int ret = sdk.init("/dev/ttyMT1", 115200, 0);
                        if (ret == 0) {
                            // TODO: 2019/3/31  制作一个提示框
                            // Toast.makeText(getApplicationContext(),"身份证模块准备成功",Toast.LENGTH_LONG).show();
                        } else {
                            // TODO: 2019/3/31 制作一个提示框
//                    Toast.makeText(getApplicationContext(),"身份证模块准备失败",Toast.LENGTH_LONG).show();
                        }
                        dismissProgressDialog();
                    }
                }

            }
        }.start();
    }

    private void showProgressDialog(String title, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, new String[]{title, message}));
    }

    private void dismissProgressDialog() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_DISMISS_PROGRESS_DIALOG));
    }

    private class IDCardTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            showProgressDialog("请稍后……", "正在读卡");
            int i = 8;
            while (i > 0) {
                i--;
                if (sdk.Authenticate(1000) == 0) {
                    IDCardInfo ic = new IDCardInfo();
                    if (sdk.Read_Card(ic, 2500) == 0) {
                        i = 0;
                        mHandler.sendMessage(mHandler.obtainMessage(READER_IDCARD_SUCCEED, ic));

                        int ret = sdk.Unpack(ic.getwltdata());
                        if (ret == 0) {
                            // TODO: 2019/3/31  Toast.makeText(AddGuideActivity.this,"读卡成功",Toast.LENGTH_LONG).show();

                            mHandler.sendMessage(mHandler.obtainMessage(PHOTO_SUCCEED));

                        } else {

                            // TODO: 2019/3/31  Toast.makeText(AddGuideActivity.this,"照片解码失败",Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    if (i == 0) {
                        // TODO: 2019/3/31  Toast.makeText(AddGuideActivity.this,"卡认证失败",Toast.LENGTH_LONG).show();
                        break;
                    }
                    SystemClock.sleep(200);
                }
            }
            dismissProgressDialog();
            return null;
        }


    }
}
