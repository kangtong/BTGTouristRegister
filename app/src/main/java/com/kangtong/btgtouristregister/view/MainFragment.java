package com.kangtong.btgtouristregister.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kangtong.btgtouristregister.R;
import com.kangtong.btgtouristregister.view.guide.GuideActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private TextView mTextHello;

    // 定时器
    private Disposable interval = null;
    private MutableLiveData<String> time = new MutableLiveData<>();

    public MainFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextHello = view.findViewById(R.id.text_hello);

        Button mBtnSelectGuide = view.findViewById(R.id.btn_select_guide);
        mBtnSelectGuide.setOnClickListener(v -> startActivity(new Intent(getActivity(), GuideActivity.class)));

        // 初始化数据
        Date date = new Date();
        String timeFormat = String.format(getString(R.string.hello_fragment), date, date);
        mTextHello.setText(timeFormat);
        // 观察者, 数据改变时, 更新 UI
        time.observe(this, s -> mTextHello.setText(s));
    }

    /**
     * 开启定时器
     */
    private void setupTime() {
        if (interval == null) {
            interval = Flowable.interval(1, TimeUnit.SECONDS)
                    .onBackpressureDrop()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .subscribe(aLong -> {
                        Date date = new Date();
                        String timeFormat = String.format(getString(R.string.hello_fragment), date, date);
                        time.postValue(timeFormat);
                    }, throwable -> {
                        throwable.printStackTrace();
                        if (!interval.isDisposed()) {
                            interval.dispose();
                        }
                        interval = null;
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (interval == null) {
            setupTime();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!interval.isDisposed()) {
            interval.dispose();
        }
        interval = null;
    }
}
