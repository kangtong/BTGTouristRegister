package com.kangtong.btgtouristregister.view.excel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kangtong.btgtouristregister.R;
import com.kangtong.btgtouristregister.view.util.TouristExportType;
import com.liyu.sqlitetoexcel.SQLiteToExcel;

import java.io.File;

public class ExcelActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_GUIDE_NAME = "extra_guide_name";
    private static final String EXTRA_DATE = "extra_date";
    private static final String EXTRA_TYPE = "extra_type";
    private String mGuideName;
    private String mDate;
    private String mFilePath;
    private ProgressBar mIndeterminateBar;
    private TextView mTextLoading;
    private Button mBtnShare;
    private ImageView mImageView;

    public static void start(Context context, String guideName, String date, @TouristExportType.ExportType int touristExportType) {
        Intent intent = new Intent(context, ExcelActivity.class);
        intent.putExtra(EXTRA_GUIDE_NAME, guideName);
        intent.putExtra(EXTRA_DATE, date);
        intent.putExtra(EXTRA_TYPE, touristExportType);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel);
        mGuideName = getIntent().getStringExtra(EXTRA_GUIDE_NAME);
        mDate = getIntent().getStringExtra(EXTRA_DATE);
        int mType = getIntent().getIntExtra(EXTRA_TYPE, TouristExportType.ITINERARY);
        mIndeterminateBar = findViewById(R.id.indeterminateBar);
        mTextLoading = findViewById(R.id.text_loading);
        mBtnShare = findViewById(R.id.btn_share);
        mImageView = findViewById(R.id.imageView);
        mBtnShare.setOnClickListener(this);
        String sql = "select addTime as '登记日期', guideName as '导游姓名', peopleName as '姓名', sex as '性别(男、女)', number as '证件号码', birthday as '出生日期' " +
                "from tourist where guideName like '%" + mGuideName + "%' AND addTime LIKE '%" + mDate + "%'";
        switch (mType) {
            case TouristExportType.ITINERARY:
                sql = "select addTime as '登记日期', guideName as '导游姓名', peopleName as '姓名', sex as '性别(男、女)', number as '证件号码', birthday as '出生日期' " +
                        "from tourist where guideName like '%" + mGuideName + "%' AND addTime LIKE '%" + mDate + "%'";
                break;
            case TouristExportType.TICKETS:
                sql = "select addTime as '登记日期', guideName as '导游姓名', peopleName as '姓名', sex as '性别(男、女)', number as '证件号码', birthday as '出生日期' " +
                        "from tourist where guideName like '%" + mGuideName + "%' AND addTime LIKE '%" + mDate + "%'";
                break;
            default:
                break;

        }
        String filePath = Environment.getExternalStorageDirectory() + "/游客记录";
        File destDir = new File(filePath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        new SQLiteToExcel
                .Builder(this)
                .setDataBase(getDatabasePath("btg.db").getPath()) //必须。 小提示：内部数据库可以通过 context.getDatabasePath("internal.db").getPath() 获取。
                .setSQL(sql)
//                .setTables(table1, table2) //可选, 如果不设置，则默认导出全部表。
                .setOutputPath(filePath) //可选, 如果不设置，默认输出路径为 app ExternalFilesDir。
                .setOutputFileName("(" + mDate + ")" + mGuideName + " 游客信息.xls") //可选, 如果不设置，输出的文件名为 xxx.db.xls。
//                .setEncryptKey("1234567") //可选，可对导出的文件进行加密。
//                .setProtectKey("9876543") //可选，可对导出的表格进行只读的保护。
                .start(new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {
                        mBtnShare.setEnabled(false);
                        mIndeterminateBar.setVisibility(View.VISIBLE);
                        mTextLoading.setText(R.string.excel_loading);
                    }

                    @Override
                    public void onCompleted(String filePath) {
                        mBtnShare.setEnabled(true);
                        mIndeterminateBar.setVisibility(View.GONE);
                        mTextLoading.setText("文件导出成功，文件创建在：" + filePath);
                        mFilePath = filePath;
                        mImageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        mBtnShare.setEnabled(true);
                        mIndeterminateBar.setVisibility(View.GONE);
                        mTextLoading.setText(e.getMessage());
                        mImageView.setVisibility(View.INVISIBLE);
                    }
                }); // 或者使用 .start() 同步方法。
    }

    @Override
    public void onClick(View view) {
// 調用系統方法分享文件
        if (!TextUtils.isEmpty(mFilePath)) {
            File file = new File(mFilePath);
            if (file.exists()) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                share.setType("application/vnd.ms-excel");//此处可发送多种文件
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, "分享文件"));
            } else {
                Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
            }
        }


    }
}
