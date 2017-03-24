package com.moodswing.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.EditProfileComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.EditProfileModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.presenter.EditProfilePresenter;
import com.moodswing.injector.component.DaggerEditProfileComponent;
import com.moodswing.mvp.mvp.view.EditProfileView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject2;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Kenny on 2017-02-27.
 */

public class EditProfileActivity extends MoodSwingActivity implements EditProfileView {
    //------- variables used for gallery image selection -------------
    // Action code used for gallery selection showing the result of our action
    private static final int SELECT_PICTURE = 1;
    private static final int EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 99;

    @Inject2
    EditProfilePresenter _editProfilePresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.btn_profilepicture)
    ImageButton _editProfilePictureButton;

    @BindView(R.id.btn_saveprofile)
    Button _saveProfileButton;

    @BindView(R.id.change_displayname)
    EditText _displayNameText;

    @BindView(R.id.change_old_password)
    EditText _oldPasswordText;

    @BindView(R.id.change_new_password)
    EditText _newPasswordText;

    private EditProfileComponent _editProfileComponent;
    private boolean storagePermissionsAvailable;
    private Uri selectedProfileUri;
    private TextWatcher newPasswordWatcher;
    private TextWatcher displayWatcher;
    private boolean newPasswordChanged;
    private boolean displayNameChanged;

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

        newPasswordChanged = false;
        displayNameChanged = false;
        selectedProfileUri = null;
        initializePresenter();
        initializeProfileInformation();
        initializeButtonsAndText();
        initializeBottomNavigationView();
    }

    private void initializeButtonsAndText() {
        _editProfilePictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                changeProfilePicture();
            }
        });

        _saveProfileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });


        //Check for Changes
        displayWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displayNameChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {  }
        };

        newPasswordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newPasswordChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {  }
        };

        _displayNameText.addTextChangedListener(displayWatcher);
        _newPasswordText.addTextChangedListener(newPasswordWatcher);
    }



    private void initializeProfileInformation() {
        _editProfilePresenter.getPicture();
    }

    private void changeProfilePicture() {
        // First check that we have storage permissions
        if (!storagePermissionsAvailable) {
            checkForStoragePermissions();
        }

        if (storagePermissionsAvailable) {
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

    @Override
    public void getPicture(ResponseBody picture) {
        try {
            String[] type = picture.contentType().toString().split("/");
            File profilePictureFile = new File(getExternalFilesDir(null) + File.separator + "OurProfilePicture." + type[1]);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = picture.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = picture.byteStream();
                outputStream = new FileOutputStream(profilePictureFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }
                //SET PICTURE TO IMAGEVIEW
                _editProfilePictureButton.setImageURI(Uri.fromFile(profilePictureFile));
                outputStream.flush();
            } catch (IOException e) {

            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {

        }
    }

    @Override
    public void noPictureMessage() {
        Toast.makeText(getBaseContext(), "Add a Profile Picture!", Toast.LENGTH_LONG).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedProfileUri = data.getData();
                _editProfilePictureButton.setImageURI(selectedProfileUri);
            }
        }
    }

    private void saveProfile() {
        String newPassword;
        String displayName;
        String oldPassword = _oldPasswordText.getText().toString();

        if (newPasswordChanged) {
            if (_newPasswordText.getText().toString().length() < 4) {
                Toast.makeText(getBaseContext(), "Your new password must be longer than 4 characters!", Toast.LENGTH_LONG).show();
                return;
            }

            newPassword = _newPasswordText.getText().toString();
        } else {
            newPassword = null;
        }

        if (displayNameChanged) {
            displayName = _displayNameText.getText().toString();
        } else {
            displayName = null;
        }

        _editProfilePresenter.putProfile(oldPassword, newPassword, displayName);
    }

    @Override
    public void displaySavedProfile() {
        if (selectedProfileUri != null) {
            savePicture();
        }
        Toast.makeText(getBaseContext(), "Profile Updated", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, JournalActivity.class);
        startActivity(intent);
    }

    @Override
    public void displaySaveError(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    private void savePicture() {
        File picture = new File(getPath(selectedProfileUri));
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(selectedProfileUri)),
                        picture
                );
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("profilePicture", picture.getName(), requestFile);

        _editProfilePresenter.postPicture(body);
    }
    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
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
            cursor = null;
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    private void initializePresenter() {
        _editProfilePresenter.attachView(this);
        _editProfilePresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
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
                EditProfileActivity.this);

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
                changeProfilePicture();
            }
        }
    }
}
