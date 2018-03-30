package com.yssh.memohae;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        init();
    }

    private void init(){
        SettingManager settingManager = new SettingManager(getApplicationContext());


    }

    @OnClick(R.id.back_btn) void backClicked(){
        finish();
    }

    @OnClick(R.id.background_setting_layout) void backgroundSettingClicked(){
        SettingManager settingManager = new SettingManager(getApplicationContext());
        settingManager.setBackgroundColor(R.color.background_color_green);
    }
}
