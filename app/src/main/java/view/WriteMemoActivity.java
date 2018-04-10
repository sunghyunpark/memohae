package view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private String localBitmapPath = "";
    private Bitmap bm, resized;


    @BindView(R.id.memo_edit_box) EditText memo_et;
    @BindView(R.id.memo_photo_iv) ImageView memo_photo_iv;

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRealm.close();
        if(bm != null){
            bm.recycle();
        }
        if(resized != null){
            resized.recycle();
        }
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
        memoVO.setSecreteModeTitle("");
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

    private void goToSelectPhoto(){
        File folder_path = new File(Environment.getExternalStorageDirectory()+"/MEMOHAE/");
        if(!folder_path.exists()){
            folder_path.mkdir();
        }
        //사진가져오기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GET_PICTURE_URI);
    }


    @OnClick(R.id.write_btn) void writeMemoClicked(){
        String textStr = memo_et.getText().toString();
        textStr = textStr.trim();

        if(textStr.equals("")){
            Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else{
            insertDB(textStr);

            //로컬에 저장
            OutputStream outStream = null;
            File file = new File(localBitmapPath);

            try{
                outStream = new FileOutputStream(file);
                resized.compress(Bitmap.CompressFormat.PNG,100,outStream);
                outStream.flush();
                outStream.close();
            }catch(FileNotFoundException e){

            }catch(IOException e){

            }
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
            goToSelectPhoto();
        }
    }

    /**
     * 갤러리에서 사진을 불러오면 일단 글라이드에서는 uri로 보여줌(속도때문에)
     * 그리고 저장을 누르면 파일을 로컬 폴더에 저장
     * 비트맵은 리사이징을 거친다(속도문제)
     * 파일명은 미리 생성해둔다
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PICTURE_URI) {
            if (resultCode == RESULT_OK) {
                try {

                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //imageView.setImageBitmap(bitmap);
                    //Glide Options
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    localBitmapPath = Environment.getExternalStorageDirectory()+"/MEMOHAE/"+ timeStamp + "_memohae.png";

                    //bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    bm = BitmapFactory.decodeFile(getPath(data.getData()), options);
                    resized = Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), true);
                    photoPath = "file://"+localBitmapPath;
                    memo_photo_iv.setVisibility(View.VISIBLE);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.centerCrop();
                    requestOptions.circleCrop();    //circle
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);

                    Glide.with(getApplicationContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(data.getData())
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
                    goToSelectPhoto();
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
