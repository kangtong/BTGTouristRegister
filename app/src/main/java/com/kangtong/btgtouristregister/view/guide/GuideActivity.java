package com.kangtong.btgtouristregister.view.guide;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kangtong.btgtouristregister.R;
import com.kangtong.btgtouristregister.model.Guide;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuideActivity extends AppCompatActivity {

    private ListView listGuide;
    private TextView textNoneGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listGuide = findViewById(R.id.list_guide);
        textNoneGuide = findViewById(R.id.text_none_guide);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        addGuide();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GuideActivity.this, AddGuideActivity.class));
            }
        });
        listGuide.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = ((TextView) view).getText().toString();
                Snackbar.make(view, string, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        List<Guide> guideList = LitePal.findAll(Guide.class);
        List<String> guideNameList = new ArrayList<>();
        for (Guide guide :
                guideList) {
            guideNameList.add(guide.getPeopleName());
        }
        if (guideNameList.isEmpty()) {
            textNoneGuide.setVisibility(View.VISIBLE);
        } else {
            textNoneGuide.setVisibility(View.INVISIBLE);
        }
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, guideNameList);
        listGuide.setAdapter(mAdapter);

    }

    private void addGuide() {
        for (int i = 0; i < 20; i++) {
            Guide guide = new Guide();
            guide.setPeopleName("小明" + i + "号");
            guide.save();
        }
    }
}
