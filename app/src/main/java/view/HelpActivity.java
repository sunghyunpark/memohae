package view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.yssh.memohae.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpActivity extends AppCompatActivity {

    @BindString(R.string.help_txt) String helpStr;
    @BindView(R.id.help_txt) TextView help_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ButterKnife.bind(this);

        help_tv.setText(helpStr);

    }

    @OnClick(R.id.back_btn) void backClicked(){
        finish();
    }
}
