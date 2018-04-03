package view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.yssh.memohae.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.RealmConfig;
import database.model.MemoVO;
import io.realm.Realm;

public class EditMemoActivity extends AppCompatActivity {

    private int memoNo;
    private String memoText;
    private Realm mRealm;

    @BindView(R.id.memo_edit_box) EditText memo_edit_et;

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);

        Intent intent = getIntent();
        memoNo = intent.getIntExtra("memoNo",0);
        memoText = intent.getExtras().getString("memoText");

        ButterKnife.bind(this);

        init();
    }

    private void init(){
        RealmConfig realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.MemoRealmVersion(getApplicationContext()));

        memo_edit_et.setText(memoText);
    }

    /**
     * 수정된 메모를 Realm DB 에 저장
     * @param memoNo
     * @param memoText
     */
    private void updateDB(int memoNo, String memoText){
        mRealm.beginTransaction();

        MemoVO memoVO = mRealm.where(MemoVO.class).equalTo("no",memoNo).findFirst();

        memoVO.setMemoText(memoText);

        mRealm.commitTransaction();

        finish();
    }

    @OnClick(R.id.save_btn) void memoSaveClicked(){
        String updateMemoText = memo_edit_et.getText().toString();
        updateMemoText = updateMemoText.trim();
        updateDB(memoNo, updateMemoText);
    }

    @OnClick(R.id.back_btn) void backClicked(){
        finish();
    }
    
    @OnClick(R.id.copy_btn) void copyClicked(){
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("LABEL", memoText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "클립보드에 복사했습니다.", Toast.LENGTH_SHORT).show();
    }
}
