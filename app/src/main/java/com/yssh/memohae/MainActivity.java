package com.yssh.memohae;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import model.MemoModel;

public class MainActivity extends AppCompatActivity {

    private ArrayList<MemoModel> memoItems;
    private RecyclerAdapter adapter;

    @BindView(R.id.memo_recyclerView) RecyclerView memoRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        init();

        setData();
    }

    /**
     * init
     */
    private void init(){
        memoItems = new ArrayList<MemoModel>();
        adapter = new RecyclerAdapter(memoItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        memoRecyclerView.setLayoutManager(linearLayoutManager);
        memoRecyclerView.setAdapter(adapter);
    }

    private void setData(){
        MemoModel memoModel;
        for(int i=0;i<10;i++){
            memoModel = new MemoModel();
            memoModel.setNo(i);
            memoModel.setMemoText(i+"");
            memoItems.add(memoModel);
        }

    }

    /**
     * Memo RecyclerView Adapter
     */
    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_ITEM = 0;
        List<MemoModel> listItems;

        private RecyclerAdapter(List<MemoModel> listItems) {
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

        private MemoModel getItem(int position) {
            return listItems.get(position);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof MemoViewHolder) {
                final MemoModel currentItem = getItem(position);
                final MemoViewHolder VHitem = (MemoViewHolder)holder;

                VHitem.memo_text_tv.setText(currentItem.getMemoText());

            }
        }

        private class MemoViewHolder extends RecyclerView.ViewHolder{
            ViewGroup memo_item_vg;
            TextView memo_text_tv;

            private MemoViewHolder(View itemView){
                super(itemView);

                memo_item_vg = (ViewGroup)itemView.findViewById(R.id.memo_item_layout);
                memo_text_tv = (TextView)itemView.findViewById(R.id.memo_text_tv);
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
}
