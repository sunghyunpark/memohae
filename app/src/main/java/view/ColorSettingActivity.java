package view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
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
            R.color.background_color_indigo,
            R.drawable.bg_img0,
            R.drawable.bg_img1,
            R.drawable.bg_img2,
            R.drawable.bg_img3,
            R.drawable.bg_img4,
            R.drawable.bg_img5
            };
    private SettingManager settingManager;
    private int DISPLAY_WIDTH;
    private int arraySize = colorArray.length;

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

        Display display = ((WindowManager)getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
        DISPLAY_WIDTH = display.getWidth();

        RecyclerAdapter adapter = new RecyclerAdapter(colorArray);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        GridLayoutManager lLayout = new GridLayoutManager(getApplicationContext(),3);
        colorRecyclerView.setLayoutManager(lLayout);
        colorRecyclerView.setAdapter(adapter);


    }


    /**
     * color recyclerView Adapter
     */
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_ITEM = 0;
        private int [] listItems;

        private RecyclerAdapter(int[] listItems) {
            this.listItems = listItems;
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
            return listItems[position];
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof ColorHolder) {
                final ColorHolder VHitem = (ColorHolder)holder;

                //Glide Options
                setThumbnail(position, VHitem);

                VHitem.item_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        settingManager.setBackgroundColor(colorArray[position]);
                        Toast.makeText(getApplicationContext(), "배경 색상이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                VHitem.item_layout.setLayoutParams(thumbNailSize(getApplicationContext()));

                /*
                현재 선택되어있는 color 에 체크표시
                 */
                if(isChecked(position)){
                    VHitem.checked_iv.setVisibility(View.VISIBLE);
                }else{
                    VHitem.checked_iv.setVisibility(View.GONE);
                }
            }
        }

        private int getResourceId(int position){
            return getResources().getIdentifier("bg_img" + (position - arraySize), "drawable", getPackageName());
        }

        /**
         * color 와 drawable 이 서로 다른데 같은 배열에 있어서 갯수로 분기처리함
         * 추후 더 좋은 방법으로 수정해야할듯함
         * @param position
         * @param Vhitem
         */
        private void setThumbnail(int position, ColorHolder Vhitem){
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();

            if(position >= arraySize){
                Glide.with(getApplicationContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(getResourceId(position))
                        .into(Vhitem.color_iv);
            }else{

                Drawable drawable = getResources().getDrawable(getItem(position));
                requestOptions.placeholder(drawable);

                Glide.with(getApplicationContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(null)
                        .into(Vhitem.color_iv);
            }

        }
        class ColorHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.color_iv) ImageView color_iv;
            @BindView(R.id.item_layout) ViewGroup item_layout;
            @BindView(R.id.checked_img) ImageView checked_iv;

            private ColorHolder(View itemView){
                super(itemView);

                ButterKnife.bind(this, itemView);
            }
        }

        private boolean isChecked(int position){
            int currentColorId = settingManager.getBackgroundColor();

            return currentColorId == colorArray[position];
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_ITEM;
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.length;
        }
    }

    private FrameLayout.LayoutParams thumbNailSize(Context context){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DISPLAY_WIDTH/3,
                DISPLAY_WIDTH/3);
        return params;
    }

    @OnClick(R.id.back_btn) void backClicked(){
        finish();
    }
}
