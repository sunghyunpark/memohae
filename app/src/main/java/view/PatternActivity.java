package view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.yssh.memohae.R;
import com.yssh.memohae.SettingManager;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Pattern Activity
 * https://github.com/aritraroy/PatternLockView 참고
 */
public class PatternActivity extends AppCompatActivity {

    public static final int NOT_REGISTER_PATTERN_MODE = 1;    //아직 패턴 등록안된 경우
    public static final int SECRETE_MEMO_MODE = 2;    //시크릿 메모 진입하는 경우

    private int patternMode = 0;
    private int memoNo;
    private String memoText;
    private boolean secreteMode;
    private String memoPhoto;
    private SettingManager settingManager;

    @BindView(R.id.pattern_text) TextView pattern_text_tv;
    @BindView(R.id.patter_lock_view) PatternLockView mPatternLockView;
    @BindString(R.string.pattern_try_txt) String pattern_try_str;
    @BindString(R.string.pattern_error_txt) String pattern_error_str;
    @BindString(R.string.pattern_try_again_txt) String pattern_try_again_str;
    @BindString(R.string.pattern_try_current_txt) String pattern_try_current_str;
    @BindString(R.string.pattern_error_txt_2) String pattern_error_2_str;

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        String firstPatternString = "";
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(mPatternLockView, pattern));
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToSha1(mPatternLockView, pattern));

            if(PatternLockUtils.patternToString(mPatternLockView, pattern).length() < 4){
                // 패턴이 4개의 점 미만인 경우
                pattern_text_tv.setText(pattern_error_str);
                mPatternLockView.clearPattern();
            }else{
                if(patternMode == NOT_REGISTER_PATTERN_MODE){
                    //처음 패턴 등록하는 경우
                    if(firstPatternString.equals("")){
                        //처음 그린 패턴 저장
                        firstPatternString = PatternLockUtils.patternToSha1(mPatternLockView, pattern);
                        Toast.makeText(getApplicationContext(),"저장하였습니다.", Toast.LENGTH_SHORT).show();

                        pattern_text_tv.setText(pattern_try_again_str);    //다시 한번 그려주세요.
                        mPatternLockView.clearPattern();

                    }else{
                        // 두번째 확인인 경우
                        if(firstPatternString.equals(PatternLockUtils.patternToSha1(mPatternLockView, pattern))){
                            //두 번 모두 같은 경우
                            settingManager.setPatternKey(PatternLockUtils.patternToSha1(mPatternLockView, pattern));
                            Toast.makeText(getApplicationContext(), "패턴이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                            mPatternLockView.clearPattern();
                            finish();
                        }else{
                            //마지막 그린 패턴이랑 다른 경우
                            pattern_text_tv.setText("패턴이 다릅니다. 다시 시도해주세요.");
                            mPatternLockView.clearPattern();
                        }
                    }

                }else if(patternMode == SECRETE_MEMO_MODE){
                    if(PatternLockUtils.patternToSha1(mPatternLockView, pattern).equals(settingManager.getPatternKey())){
                        Intent intent = new Intent(getApplicationContext(), EditMemoActivity.class);
                        intent.putExtra("memoNo", memoNo);
                        intent.putExtra("memoText", memoText);
                        intent.putExtra("secreteMode", secreteMode);
                        intent.putExtra("memoPhoto", memoPhoto);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        mPatternLockView.clearPattern();
                    }

                }
            }
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pattern);

        Intent intent = getIntent();
        patternMode = intent.getIntExtra("patternMode", 0);
        if(patternMode == SECRETE_MEMO_MODE){
            memoNo = intent.getIntExtra("memoNo",0);
            memoText = intent.getExtras().getString("memoText");
            secreteMode = intent.getBooleanExtra("secreteMode", false);
            memoPhoto = intent.getExtras().getString("memoPhoto");
        }

        ButterKnife.bind(this);

        init();
    }

    private void init(){
        settingManager = new SettingManager(getApplicationContext());

        patternInit();
        pattern_text_tv.setText(pattern_try_str);

    }

    private void patternInit(){
        //mPatternLockView.setDotCount(3);
        mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
        mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
        mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
        mPatternLockView.setAspectRatioEnabled(true);
        mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        mPatternLockView.setDotAnimationDuration(150);
        mPatternLockView.setPathEndAnimationDuration(100);
        mPatternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.white));
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(true);
        mPatternLockView.setInputEnabled(true);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);
    }

}
