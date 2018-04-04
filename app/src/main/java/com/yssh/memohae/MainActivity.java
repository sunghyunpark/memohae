package com.yssh.memohae;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.yssh.memohae.helper.ItemTouchHelperAdapter;
import com.yssh.memohae.helper.OnStartDragListener;
import com.yssh.memohae.helper.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.RealmConfig;
import database.model.MemoVO;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import view.EditMemoActivity;
import view.PatternActivity;
import view.SettingActivity;
import view.WriteMemoActivity;

public class MainActivity extends AppCompatActivity implements OnStartDragListener {

    private ArrayList<MemoVO> memoItems;
    private RecyclerAdapter adapter;
    private Realm mRealm;
    private SettingManager settingManager;
    private ItemTouchHelper mItemTouchHelper;

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
        settingManager = new SettingManager(getApplicationContext());
        memoItems = new ArrayList<MemoVO>();
        adapter = new RecyclerAdapter(memoItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        memoRecyclerView.setLayoutManager(linearLayoutManager);
        memoRecyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(memoRecyclerView);
    }

    private void setBackground(){
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

        RealmResults<MemoVO> memoVORealmResults = mRealm.where(MemoVO.class).findAll().sort("order", Sort.DESCENDING);
        int listSize = memoVORealmResults.size();

        for(int i=0;i<listSize;i++){
            memoItems.add(memoVORealmResults.get(i));
            Log.d("MemoData","============================================");
            Log.d("MemoData", "Memo NO : "+memoVORealmResults.get(i).getNo());
            Log.d("MemoData", "Memo Order : "+memoVORealmResults.get(i).getOrder());
            Log.d("MemoData", "Memo Text : "+memoVORealmResults.get(i).getMemoText());
            Log.d("MemoData", "Memo SecreteMode : " +memoVORealmResults.get(i).isSecreteMode()+"");
            Log.d("MemoData", "Memo PhotoUri : "+memoVORealmResults.get(i).getMemoPhotoPath()+"");
            Log.d("MemoData","============================================");
        }
        adapter.notifyDataSetChanged();

    }

    /**
     * Memo RecyclerView Adapter
     */
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
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

                VHitem.memo_item_vg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(currentItem.isSecreteMode() && (!TextUtils.isEmpty(settingManager.getPatternKey()))){
                            Intent intent = new Intent(getApplicationContext(), PatternActivity.class);
                            intent.putExtra("memoNo", currentItem.getNo());
                            intent.putExtra("memoText", currentItem.getMemoText());
                            intent.putExtra("secreteMode", currentItem.isSecreteMode());
                            intent.putExtra("memoPhoto", currentItem.getMemoPhotoPath());
                            intent.putExtra("patternMode", PatternActivity.SECRETE_MEMO_MODE);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(getApplicationContext(), EditMemoActivity.class);
                            intent.putExtra("memoNo", currentItem.getNo());
                            intent.putExtra("memoText", currentItem.getMemoText());
                            intent.putExtra("secreteMode", currentItem.isSecreteMode());
                            intent.putExtra("memoPhoto", currentItem.getMemoPhotoPath());
                            startActivity(intent);
                        }
                    }
                });

                if(isSecreteMode(position)){
                    VHitem.memo_text_tv.setVisibility(View.GONE);
                    VHitem.secrete_mode_vg.setVisibility(View.VISIBLE);
                }else{
                    VHitem.memo_text_tv.setVisibility(View.VISIBLE);
                    VHitem.secrete_mode_vg.setVisibility(View.GONE);
                }

            }
        }

        class MemoViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.memo_item_layout) ViewGroup memo_item_vg;
            @BindView(R.id.memo_text_tv) TextView memo_text_tv;
            @BindView(R.id.secrete_mode_layout) ViewGroup secrete_mode_vg;

            private MemoViewHolder(View itemView){
                super(itemView);

                ButterKnife.bind(this, itemView);
            }
        }

        private boolean isSecreteMode(int position){
            return getItem(position).isSecreteMode();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(listItems, fromPosition, toPosition);
            swapMemoOrder(getItem(fromPosition).getOrder(), getItem(toPosition).getOrder());
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(int position) {
            deleteMemoDB(getItem(position).getNo());
            listItems.remove(position);
            notifyItemRemoved(position);
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
     * Realm DB 에서 해당 position item 의 no 를 삭제
     * @param position
     */
    private void deleteMemoDB(int position){
        MemoVO memoVO = mRealm.where(MemoVO.class).equalTo("no",(position)).findFirst();
        mRealm.beginTransaction();
        memoVO.deleteFromRealm();
        mRealm.commitTransaction();

    }

    /**
     * no 은 Primary key 라서 order 를 추가해서 사용함
     * 단순 순서를 정하는 용도로만 사용
     * @param fromOrder
     * @param ToOrder
     */
    private void swapMemoOrder(int fromOrder, int ToOrder){
        MemoVO fromMemoVO = mRealm.where(MemoVO.class).equalTo("order",(fromOrder)).findFirst();
        MemoVO toMemoVO = mRealm.where(MemoVO.class).equalTo("order",(ToOrder)).findFirst();
        mRealm.beginTransaction();
        fromMemoVO.setOrder(ToOrder);
        toMemoVO.setOrder(fromOrder);
        mRealm.commitTransaction();
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

    /**
     * drag and drop
     * @param viewHolder
     */
    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
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
