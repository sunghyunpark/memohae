package view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yssh.memohae.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.RealmConfig;
import database.model.MemoVO;
import io.realm.Realm;

public class WriteMemoActivity extends AppCompatActivity {

    private Realm mRealm;
    private static final int GET_PICTURE_URI = 0;
    private static final int REQUEST_PERMISSIONS = 10;
    private String photoPath = null;

    @BindView(R.id.memo_edit_box) EditText memo_et;
    @BindView(R.id.memo_photo_iv) ImageView memo_photo_iv;

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
     * Secrete Mode 는 default > false
     * @param memoTextStr
     */
    private void insertDB(String memoTextStr){
        mRealm.beginTransaction();
        MemoVO memoVO = new MemoVO();

        Number maxId = mRealm.where(MemoVO.class).max("no");
        int nextId = (maxId == null) ? 0:maxId.intValue() + 1;

        memoVO.setNo(nextId);
        memoVO.setOrder(nextId);
        memoVO.setMemoText(memoTextStr);
        memoVO.setSecreteMode(false);
        memoVO.setMemoPhotoPath(photoPath);

        mRealm.copyToRealmOrUpdate(memoVO);
        mRealm.commitTransaction();

        finish();
    }

    /**
     * Uri 절대 경로
     * @param uri
     * @return
     */
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
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

    @OnClick(R.id.select_photo_btn) void selectPhotoClicked(){
        if (ContextCompat.checkSelfPermission(WriteMemoActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(WriteMemoActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (WriteMemoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(WriteMemoActivity.this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            } else {
                ActivityCompat.requestPermissions(WriteMemoActivity.this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            //사진가져오기
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GET_PICTURE_URI);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PICTURE_URI) {
            if (resultCode == RESULT_OK) {
                try {
                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //imageView.setImageBitmap(bitmap);
                    //Glide Options
                    photoPath = "file://"+getPath(data.getData());
                    memo_photo_iv.setVisibility(View.VISIBLE);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.centerCrop();
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

                    Glide.with(getApplicationContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(photoPath)
                            .into(memo_photo_iv);

                } catch (Exception e) {
                    Log.e("test", e.getMessage());
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:

                //권한이 있는 경우
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //사진가져오기
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GET_PICTURE_URI);
                }
                //권한이 없는 경우
                else {
                    finish();
                    Toast.makeText(this, "퍼미션을 허용해야 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
