package view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.yssh.memohae.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.RealmConfig;
import database.model.MemoVO;
import io.realm.Realm;

public class WriteMemoActivity extends AppCompatActivity {

    private Realm mRealm;

    @BindView(R.id.memo_edit_box)
    EditText memo_et;

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_memo);

        ButterKnife.bind(this);

        init();
    }

    /**
     * init
     */
    private void init(){
        RealmConfig realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.MemoRealmVersion(getApplicationContext()));
    }

    /**
     * Realm DB 에 Memo 저장
     * @param memoTextStr
     */
    private void insertDB(String memoTextStr){
        mRealm.beginTransaction();
        MemoVO memoVO = new MemoVO();

        Number maxId = mRealm.where(MemoVO.class).max("no");
        int nextId = (maxId == null) ? 1:maxId.intValue() + 1;

        memoVO.setNo(nextId);
        memoVO.setMemoText(memoTextStr);

        mRealm.copyToRealmOrUpdate(memoVO);
        mRealm.commitTransaction();

        finish();
    }

    @OnClick(R.id.write_btn) void writeMemoClicked(){
        String textStr = memo_et.getText().toString();
        textStr = textStr.trim();

        if(textStr.equals("")){
            Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else{
            insertDB(textStr);
        }
    }

    @OnClick(R.id.back_btn) void backClicked(){
        finish();
    }
}
