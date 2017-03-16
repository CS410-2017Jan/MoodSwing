package com.moodswing.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.view.CaptureView;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Matthew on 2017-03-12.
 */

public class CaptureActivity extends MoodSwingActivity implements CaptureView {

//    private CaptureView captureView;

//    @Inject2
//    CapturePresenter _capturePresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.cap_name)
    TextView _capName;

    @BindView(R.id.cap_date)
    TextView _capDate;

    @BindView(R.id.cap_title)
    TextView _capTitle;

    @BindView(R.id.cap_text)
    TextView _capText;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

//    private CaptureComponent _captureComponent;

    // Facebook
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    String title;
    String date;
    String text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        initializeBottomNavigationView();

//        initializePresenter();

        // Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        title = getIntent().getStringExtra("EXTRA_TITLE");
        date = getIntent().getStringExtra("EXTRA_DATE");
        text = getIntent().getStringExtra("EXTRA_TEXT");
        _capName.setText("Name");
        _capDate.setText(date);
        _capTitle.setText(title);
        _capText.setText(text);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onCaptureSuccess(){
        String message = "Capture fetch Failure";
        Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCaptureFailure(){
        String message = "Capture fetch Failure";
        Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError() {
        String message = "Error fetching entries";
        Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_LONG).show();
    }

//    private void initializePresenter() {
//        _capturePresenter.attachView(this);
//        _capturePresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
//    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.action_share:
                View menuItemView = findViewById(R.id.action_share);
                PopupMenu popupMenu = new PopupMenu(this, menuItemView);
                popupMenu.inflate(R.menu.share_popup_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.share_facebook:
                                if (ShareDialog.canShow(SharePhotoContent.class)) {
                                    // TODO change to Bitmap in Capture
                                    Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                                    SharePhoto photo = new SharePhoto.Builder()
                                            .setBitmap(image)
                                            .build();

                                    SharePhotoContent sharePhotoContent= new SharePhotoContent.Builder()
                                            .addPhoto(photo)
                                            .setShareHashtag(new ShareHashtag.Builder()
                                                    .setHashtag("#MoodSwing")
                                                    .build())
                                            .build();
                                    shareDialog.show(sharePhotoContent);
                                }
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
