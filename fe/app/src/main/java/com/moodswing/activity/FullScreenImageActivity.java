package com.moodswing.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerNewEntryComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.NewEntryModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.view.FullScreenImageView;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Matthew on 2017-03-17.
 */

public class FullScreenImageActivity extends AppCompatActivity implements FullScreenImageView {


    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.imageFullScreen)
    ImageView _image;

    @BindView(R.id.imageFullScreenIcon)
    ImageView _imageIcon;

    @BindView(R.id.keepbutton)
    Button _keepButton;

    @BindView(R.id.changebutton)
    Button _changeButton;

    byte[] byteArray;
    private Bitmap pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_fullscreenimage);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        byteArray = getIntent().getByteArrayExtra("PICTURE");
        if (byteArray != null) {
            _image.setBackgroundColor(Color.parseColor("#000000"));
            _imageIcon.setVisibility(View.INVISIBLE);
            pic = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            _image.setImageBitmap(pic);
        }else{
            _image.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

}
