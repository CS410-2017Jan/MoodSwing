package com.moodswing.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Matthew on 2017-03-17.
 */

public class FullScreenImageActivity extends AppCompatActivity implements FullScreenImageView {
    private static final int SELECT_PICTURE = 1;
    private static final int EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 99;

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
    private boolean storagePermissionsAvailable;
    private Uri selectedProfileUri;

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

        initializeButtons();
    }

    private void initializeButtons() {
        _changeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                changePicture();
            }
        });

        _keepButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                savePicture();
            }
        });
    }

    private void savePicture() {
        String uri = selectedProfileUri.toString();
        Intent intent = new Intent(this, NewEntryActivity.class);
        intent.putExtra("CAPTURE_URI", uri);
        intent.putExtra("NEW_ENTRY_INTENT", "FULL_SCREEN_IMAGE_ACTIVITY");
        startActivity(intent);
    }

    private void changePicture() {
        // First check that we have storage permissions
        if (!storagePermissionsAvailable) {
            checkForStoragePermissions();
        }

        if (storagePermissionsAvailable)  {
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
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedProfileUri = data.getData();
                _image.setImageURI(selectedProfileUri);
                _imageIcon.setVisibility(View.INVISIBLE);
                _image.setBackgroundColor(Color.parseColor("#000000"));
            }
        }
    }

    private void checkForStoragePermissions() {
        storagePermissionsAvailable =
                ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!storagePermissionsAvailable) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermissionExplanationDialog(EXTERNAL_STORAGE_PERMISSIONS_REQUEST);
            } else {
                // No explanation needed, we can request the permission.
                requestStoragePermissions();
            }
        }
    }

    private void requestStoragePermissions() {
        if (!storagePermissionsAvailable) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSIONS_REQUEST);

            // EXTERNAL_STORAGE_PERMISSIONS_REQUEST is an app-defined int constant that must be between 0 and 255.
            // The callback method gets the result of the request.
        }
    }

    private void showPermissionExplanationDialog(int requestCode) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                FullScreenImageActivity.this);

        // set title
        alertDialogBuilder.setTitle(getResources().getString(R.string.insufficient_permissions));

        if (requestCode == EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            alertDialogBuilder
                    .setMessage(getResources().getString(R.string.permissions_storage_needed_explanation))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.understood), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            requestStoragePermissions();
                        }
                    });
        }

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    storagePermissionsAvailable = (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }

            if (storagePermissionsAvailable) {
                // resume changing profile picture
                changePicture();
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}
