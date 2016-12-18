package ru.ssau.mobile.ssau_mobile_task3;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import ru.ssau.mobile.ssau_mobile_task3.model.Photo;

/**
 * Created by Pavel on 18.12.2016.
 */

public class FullscreenImageActivity extends AppCompatActivity{
    private static final String TAG = "FullscreenImageActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        Intent intent = getIntent();
        byte[] raw = intent.getExtras().getByteArray("image");
        ImageView imageView = (ImageView) findViewById(R.id.fullscreen_image);
        imageView.setImageDrawable(new BitmapDrawable(getResources(), Photo.getImage(raw)));
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}
