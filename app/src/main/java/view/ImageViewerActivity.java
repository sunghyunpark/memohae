package view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yssh.memohae.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageViewerActivity extends AppCompatActivity {

    @BindView(R.id.image_view_pic) CropView cropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String memoPhotoPath = intent.getExtras().getString("memoPhoto");

        Glide.with(getApplicationContext()).asBitmap().load(memoPhotoPath)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        cropView.setImageBitmap(resource);
                        //할일

                    }
                });
    }
}
