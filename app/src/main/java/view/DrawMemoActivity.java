package view;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yssh.memohae.R;
import com.yssh.memohae.SettingManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 그리기 화면
 */
public class DrawMemoActivity extends AppCompatActivity {

    @BindView(R.id.drawView) DrawView drawView;
    @BindView(R.id.background_img_iv) ImageView background_iv;

    private SettingManager settingManager;
    //상태값을 상수로 정의
    static final int RED_STATE=0;
    static final int BLUE_STATE=1;
    static final int YELLOW_STATE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_memo);

        ButterKnife.bind(this);

        init();

        setBackground();

        setDrawView();
    }

    private void init(){
        settingManager = new SettingManager(getApplicationContext());
    }

    private void setBackground(){
        //main_activity_vg.setBackgroundResource(settingManager.getBackgroundColor());
        Drawable drawable = getResources().getDrawable(settingManager.getBackgroundColor());
        //Glide Options
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        requestOptions.placeholder(drawable);
        //requestOptions.override(DISPLAY_WIDTH, DISPLAY_WIDTH);

        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load(null)
                .into(background_iv);
    }

    private void setDrawView(){
        drawView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.back_btn) void backClicked(){
        finish();
    }
}
