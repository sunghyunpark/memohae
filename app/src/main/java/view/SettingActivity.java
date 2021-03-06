package view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yssh.memohae.R;
import com.yssh.memohae.SettingManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.RealmConfig;
import database.model.MemoVO;
import io.realm.Realm;
import io.realm.RealmResults;

public class SettingActivity extends AppCompatActivity {

    private SettingManager settingManager;
    private Realm mRealm;

    @BindView(R.id.current_background_color_iv) ImageView current_color_iv;
    @BindView(R.id.current_app_version_tv) TextView current_app_version_tv;
    @BindView(R.id.pattern_state_txt) TextView pattern_state_tv;
    @BindView(R.id.text_size_1_tv) TextView text_size_1_tv;
    @BindView(R.id.text_size_2_tv) TextView text_size_2_tv;
    @BindView(R.id.text_size_3_tv) TextView text_size_3_tv;


    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mRealm != null)
        mRealm.close();
    }

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
        settingManager = new SettingManager(getApplicationContext());

        currentColorThumbnail();

        currentAppVersion();

        hasPattern();

        setTextSize();

    }

    /**
     * 현재 배경색상
     */
    private void currentColorThumbnail(){
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

    /**
     * 패턴 확인
     */
    private void hasPattern(){
        if(TextUtils.isEmpty(settingManager.getPatternKey())){
            pattern_state_tv.setText("설정 안됨");
        }else{
            pattern_state_tv.setText("설정 됨");
        }
    }

    /**
     * 패턴 초기화를 하게되면 모든 메모들의 시크릿 모드를 OFF 로 변경
     */
    private void updateDBSecreteModeToOFF(){
        RealmConfig realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.MemoRealmVersion(getApplicationContext()));

        RealmResults<MemoVO> memoVORealmResults = mRealm.where(MemoVO.class).findAll();
        int listSize = memoVORealmResults.size();
        mRealm.beginTransaction();

        for(int i=0;i<listSize;i++){
            memoVORealmResults.get(i).setSecreteMode(false);
        }
        mRealm.commitTransaction();
    }

    /**
     * settingManager 에 저장되어있는 사이즈를 초기화
     */
    private void setTextSize(){
        int textSize = settingManager.getTextSize();

        if(textSize == 15){
            text_size_1_tv.setTextColor(getResources().getColor(R.color.colorAccent));
        }else if(textSize == 20){
            text_size_2_tv.setTextColor(getResources().getColor(R.color.colorAccent));
        }else if(textSize == 25){
            text_size_3_tv.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    /**
     * 텍스트 사이즈를 선택 시 ui 변경
     * @param num
     */
    private void textSizeChange(int num){
        text_size_1_tv.setTextColor(Color.BLACK);
        text_size_2_tv.setTextColor(Color.BLACK);
        text_size_3_tv.setTextColor(Color.BLACK);

        switch (num){
            case 15:
                text_size_1_tv.setTextColor(getResources().getColor(R.color.colorAccent));
                break;

            case 20:
                text_size_2_tv.setTextColor(getResources().getColor(R.color.colorAccent));
                break;

            case 25:
                text_size_3_tv.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
        }

        settingManager.setTextSize(num);
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
    
    @OnClick(R.id.patter_setting_layout) void patternSettingClicked(){
        if(TextUtils.isEmpty(settingManager.getPatternKey())){
            Intent intent = new Intent(getApplicationContext(), PatternActivity.class);
            intent.putExtra("patternMode", PatternActivity.NOT_REGISTER_PATTERN_MODE);
            startActivity(intent);
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("초기화");
            alert.setMessage("패턴을 초기화 하시겠습니까?");
            alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    settingManager.setPatternKey(null);
                    pattern_state_tv.setText("설정 안됨");
                    updateDBSecreteModeToOFF();
                }
            });
            alert.setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.

                        }
                    });
            alert.show();
        }
    }

    @OnClick(R.id.recommend_layout) void recommendClicked(){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        //String subject = "문자의 제목";
        String text = "https://play.google.com/store/apps/details?id="+getPackageName();
        //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        // Title of intent
        Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
        startActivity(chooser);
    }

    @OnClick(R.id.text_size_1_tv) void textSize15Clicked(){
        textSizeChange(15);
        Toast.makeText(getApplicationContext(), "글자 크기가 변경되었습니다. \n앱을 재실행해 주세요.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.text_size_2_tv) void textSize20Clicked(){
        textSizeChange(20);
        Toast.makeText(getApplicationContext(), "글자 크기가 변경되었습니다. \n앱을 재실행해 주세요.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.text_size_3_tv) void textSize25Clicked(){
        textSizeChange(25);
        Toast.makeText(getApplicationContext(), "글자 크기가 변경되었습니다. \n앱을 재실행해 주세요.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.open_source_layout) void openSourceClicked(){
        Intent intent = new Intent(getApplicationContext(), OpenSourceActivity.class);
        startActivity(intent);
    }
}
