package com.yssh.memohae;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.RealmConfig;
import database.model.MemoVO;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private ArrayList<MemoVO> memoItems;
    private RecyclerAdapter adapter;
    private Realm mRealm;

    @BindView(R.id.main_activity_layout) ViewGroup main_activity_vg;
    @BindView(R.id.memo_recyclerView) RecyclerView memoRecyclerView;

    @Override
    public void onResume(){
        super.onResume();

        setBackground();

        setData();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        init();

    }

    /**
     * init
     */
    private void init(){
        memoItems = new ArrayList<MemoVO>();
        adapter = new RecyclerAdapter(memoItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        memoRecyclerView.setLayoutManager(linearLayoutManager);
        memoRecyclerView.setAdapter(adapter);
    }

    private void setBackground(){
        SettingManager settingManager = new SettingManager(getApplicationContext());
        main_activity_vg.setBackgroundResource(settingManager.getBackgroundColor());
    }

    /**
     * Realm DB 로 부터 Memo 데이터를 받아와 List 에 저장
     */
    private void setData(){
        if(memoItems != null){
            memoItems.clear();
        }

        RealmConfig realmConfig = new RealmConfig();
        mRealm = Realm.getInstance(realmConfig.MemoRealmVersion(getApplicationContext()));

        RealmResults<MemoVO> memoVORealmResults = mRealm.where(MemoVO.class).findAll();
        int listSize = memoVORealmResults.size();

        for(int i=listSize-1;i>=0;i--){
            memoItems.add(memoVORealmResults.get(i));
            Log.d("MemoData","============================================");
            Log.d("MemoData", "Memo NO : "+memoVORealmResults.get(i).getNo());
            Log.d("MemoData", "Memo Text : "+memoVORealmResults.get(i).getMemoText());
            Log.d("MemoData","============================================");
        }
        adapter.notifyDataSetChanged();

    }

    /**
     * Memo RecyclerView Adapter
     */
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_ITEM = 0;
        List<MemoVO> listItems;

        private RecyclerAdapter(List<MemoVO> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_memo_item, parent, false);
                return new MemoViewHolder(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private MemoVO getItem(int position) {
            return listItems.get(position);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof MemoViewHolder) {
                final MemoVO currentItem = getItem(position);
                final MemoViewHolder VHitem = (MemoViewHolder)holder;

                VHitem.memo_text_tv.setText(currentItem.getMemoText());

            }
        }

        class MemoViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.memo_item_layout) ViewGroup memo_item_vg;
            @BindView(R.id.memo_text_tv) TextView memo_text_tv;

            private MemoViewHolder(View itemView){
                super(itemView);

                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_ITEM;
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }

    /**
     * Realm DB 의 모든 Data 삭제
     */
    private void deleteAllMemoDB(){
        RealmResults<MemoVO> memoVORealmResults = mRealm.where(MemoVO.class).findAll();
        mRealm.beginTransaction();
        memoVORealmResults.deleteAllFromRealm();
        mRealm.commitTransaction();

        setData();
    }

    /**
     * All Delete Memo Dialog 노출
     */
    private void showAllDeleteDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("전체 삭제");
        alert.setMessage("전체 삭제하시겠습니까?");
        alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteAllMemoDB();
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

    @OnClick(R.id.delete_memo_btn) void deleteMemoClicked(){
        if(memoItems.size() > 0){
            showAllDeleteDialog();
        }else{
            Toast.makeText(getApplicationContext(), "삭제할 메모가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.write_memo_btn) void writeMemoClicked(){
        Intent intent = new Intent(getApplicationContext(), WriteMemoActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.setting_btn) void settingClicked(){
        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(intent);
    }

}
