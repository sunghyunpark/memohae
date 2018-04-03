package view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yssh.memohae.R;
import com.yssh.memohae.SettingManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.current_background_color_iv) ImageView current_color_iv;
    @BindView(R.id.current_app_version_tv) TextView current_app_version_tv;

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
        currentColorThumbnail();

        currentAppVersion();

    }

    /**
     * 현재 배경색상
     */
    private void currentColorThumbnail(){
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
    }

    /**
     * 현재 앱 버전
     */
    private void currentAppVersion(){
        String version;
        try {
            PackageInfo i = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = i.versionName;
            current_app_version_tv.setText("v"+version);
        } catch(PackageManager.NameNotFoundException e) { }
    }

    @OnClick(R.id.back_btn) void backClicked(){
        finish();
    }

    @OnClick(R.id.background_setting_layout) void backgroundSettingClicked(){
        Intent intent = new Intent(getApplicationContext(), ColorSettingActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.go_to_playstore_layout) void goToPlayStoreClicked(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        startActivity(intent);
    }

    @OnClick(R.id.help_layout) void goToHelpClicked(){
        Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
        startActivity(intent);
    }
}
