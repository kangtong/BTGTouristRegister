package com.kangtong.btgtouristregister.view.util;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.kangtong.btgtouristregister.R;
import com.kangtong.btgtouristregister.view.excel.ExcelActivity;

public class ChooseDialog {
    public static AlertDialog createDialog(Context context, String guideName, String date) {
        return new AlertDialog.Builder(context).setTitle("请选择想要导出的类型").setItems(R.array.excel_choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        ExcelActivity.start(context, guideName, date, TouristExportType.ITINERARY);
                        break;
                    case 1:
                        ExcelActivity.start(context, guideName, date, TouristExportType.TICKETS);
                        break;
                    default:
                        break;
                }
            }
        }).create();
    }

}
