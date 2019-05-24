package com.kangtong.btgtouristregister.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kangtong.btgtouristregister.R;
import com.kangtong.btgtouristregister.view.guide.GuideActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private TextView mTextHello;


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
        String timeFormat = String.format(getString(R.string.hello_fragment), date);
        mTextHello.setText(timeFormat);
    }

}
