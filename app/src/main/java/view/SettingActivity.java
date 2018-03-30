package view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yssh.memohae.R;
import com.yssh.memohae.SettingManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.current_background_color_iv) ImageView current_color_iv;

    @Override
    public  void onResume(){
        super.onResume();
        init();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);
    }

    private void init(){
        SettingManager settingManager = new SettingManager(getApplicationContext());

        Drawable drawable = getResources().getDrawable(settingManager.getBackgroundColor());

        //Glide Options

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        requestOptions.placeholder(drawable);

        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load(null)
                .into(current_color_iv);

        //current_color_iv.setBackgroundResource(settingManager.getBackgroundColor());
        //Log.d("SettingManager", settingManager.getBackgroundColor()+"");

    }

    @OnClick(R.id.back_btn) void backClicked(){
        finish();
    }

    @OnClick(R.id.background_setting_layout) void backgroundSettingClicked(){
        Intent intent = new Intent(getApplicationContext(), ColorSettingActivity.class);
        startActivity(intent);
    }
}
