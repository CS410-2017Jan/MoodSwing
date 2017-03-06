package com.moodswing.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.EditProfileComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.EditProfileModule;
import com.moodswing.mvp.mvp.presenter.EditProfilePresenter;
import com.moodswing.injector.component.DaggerEditProfileComponent;
import com.moodswing.mvp.mvp.view.EditProfileView;

import java.util.ArrayList;

import javax.inject.Inject2;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kenny on 2017-02-27.
 */

public class EditProfileActivity extends AppCompatActivity implements EditProfileView {
    //------- variables used for gallery image selection -------------
    // Action code used for gallery selection showing the result of our action
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;

    @Inject2
    EditProfilePresenter _editProfilePresenter;

    @BindView(R.id.btn_editprofilepicture)
    Button _editProfilePictureButton;


    private EditProfileComponent _editProfileComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        _editProfileComponent = DaggerEditProfileComponent.builder()
                .editProfileModule(new EditProfileModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();
        _editProfileComponent.inject(this);

        initializePresenter();
        initializeButtons();

    }

    private void initializeButtons() {
        _editProfilePictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //TODO: set some restrictions on editing profile
                changeProfilePicture();
            }
        });
    }

    private void changeProfilePicture() {
        // Create intent to Open Image applications like Gallery, Google Photos
        // select a file
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //allows multiple strings tobe
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                ImageView profileImageView;
                profileImageView= (ImageView) findViewById(R.id.profilepicture);
                profileImageView.setImageURI(selectedImageUri);

                //Pass to Presenter
                _editProfilePresenter.changePicture(selectedImageUri);
            }
        }

        if (Intent.ACTION_SEND_MULTIPLE.equals(data.getAction()) && data.hasExtra(Intent.EXTRA_STREAM)) {
            // retrieve a collection of selected images
            ArrayList<Parcelable> list = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            // iterate over these images
            if( list != null ) {
                for (Parcelable parcel : list) {
                    Uri uri = (Uri) parcel;
                    // TODO handle the images one by one here
                }
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }


    private void initializePresenter() {
        _editProfilePresenter.attachView(this);
    }

}
