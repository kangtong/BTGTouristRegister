package com.kangtong.btgtouristregister.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kangtong.btgtouristregister.R;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
// TODO: 2019/3/30 这里有一个切换Fragment就会异常的错误
public class MainFragment extends Fragment {

    private TextView mTextHello;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mTextHello == null) mTextHello = getView().findViewById(R.id.text_hello);
            switch (msg.what) {
                case 1:
                    Date date = new Date();
                    mTextHello.setText(String.format(getString(R.string.hello_fragment), date, date));
                    break;
            }
        }
    };

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextHello = view.findViewById(R.id.text_hello);
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        };
        mTimer.schedule(mTimerTask, new Date(), 1000);
    }


}
