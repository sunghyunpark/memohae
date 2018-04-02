package view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yssh.memohae.R;
import com.yssh.memohae.SettingManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ColorSettingActivity extends AppCompatActivity {

    private int[] colorArray = {
            R.color.background_color_blue,
            R.color.background_color_default,
            R.color.background_color_cyon,
            R.color.background_color_teal,
            R.color.background_color_green,
            R.color.background_color_light_green,
            R.color.background_color_lime,
            R.color.background_color_amber,
            R.color.background_color_orange,
            R.color.background_color_deep_orange,
            R.color.background_color_purple,
            R.color.background_color_deep_purple,
            R.color.background_color_brown,
            R.color.background_color_blue_gray,
            R.color.background_color_indigo
            };
    private SettingManager settingManager;

    @BindView(R.id.color_recyclerView) RecyclerView colorRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_setting);

        ButterKnife.bind(this);

        init();

    }

    /**
     * init
     */
    private void init(){
        settingManager = new SettingManager(getApplicationContext());

        RecyclerAdapter adapter = new RecyclerAdapter(colorArray);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        colorRecyclerView.setLayoutManager(linearLayoutManager);
        colorRecyclerView.setAdapter(adapter);

    }

    /**
     * color recyclerView Adapter
     */
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_ITEM = 0;
        private int[] colorArray;

        private RecyclerAdapter(int[] colorArray) {
            this.colorArray = colorArray;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_color_item, parent, false);
                return new ColorHolder(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private int getItem(int position) {
            return colorArray[position];
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof ColorHolder) {
                final int currentItem = getItem(position);
                final ColorHolder VHitem = (ColorHolder)holder;

                Drawable drawable = getResources().getDrawable(currentItem);

                //Glide Options

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.centerCrop();
                requestOptions.placeholder(drawable);

                Glide.with(getApplicationContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(null)
                        .into(VHitem.color_iv);

                VHitem.item_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        settingManager.setBackgroundColor(currentItem);
                        Toast.makeText(getApplicationContext(), "배경 색상이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }

        class ColorHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.color_iv) ImageView color_iv;
            @BindView(R.id.item_layout) ViewGroup item_layout;

            private ColorHolder(View itemView){
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
            return colorArray.length;
        }
    }

    @OnClick(R.id.back_btn) void backClicked(){
        finish();
    }
}
