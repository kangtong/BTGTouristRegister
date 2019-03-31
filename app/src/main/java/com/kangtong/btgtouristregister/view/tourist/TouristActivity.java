package com.kangtong.btgtouristregister.view.tourist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.kangtong.btgtouristregister.R;

public class TouristActivity extends AppCompatActivity {

    private static final String EXTRA_GUIDE_NAME = "extra_guide_name";
    private String guideName;

    public static void start(Context context, String guideName) {
        Intent intent = new Intent(context, TouristActivity.class);
        intent.putExtra(EXTRA_GUIDE_NAME, guideName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        guideName = getIntent().getStringExtra(EXTRA_GUIDE_NAME);
        toolbar.setTitle("当前导游：" + guideName);


    }
}
