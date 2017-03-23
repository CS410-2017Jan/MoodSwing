package com.moodswing.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;
import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.emotion_effects_helper.EmotionView;
import com.moodswing.emotion_effects_helper.ImageHelper;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.CameraComponent;
import com.moodswing.injector.component.DaggerCameraComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.CameraModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.presenter.CameraPresenter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by daniel on 03/03/17.
 */

public class CameraActivity extends AppCompatActivity implements Detector.ImageListener, Detector.FaceListener, CameraDetector.CameraEventListener, EmotionView.EmotionThreadEventListener {
    @Inject2
    CameraPresenter _cameraPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    private CameraComponent _cameraComponent;

    @BindView(R.id.camera_preview)
    SurfaceView cameraPreview; // display camera images

    @BindView(R.id.emotion_view)
    EmotionView emotionView; // display emotion overlay

    @BindView(R.id.please_wait_textview)
    TextView pleaseWaitTextView;

    @BindView(R.id.not_found_textview)
    TextView notFoundTextView;

    @BindView(R.id.camera_layout)
    RelativeLayout cameraLayout; // layout containing all UI elements

    @BindView(R.id.progress_bar_cover)
    RelativeLayout progressBarLayout; // layout containing progress circle for initialization

    @BindView(R.id.permissionsUnavailableLayout)
    LinearLayout permissionsUnavailableLayout; // notify user that not enough permissions have been granted

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.btn_camera_capture)
    ImageButton _cameraCaptureButton;

    @BindView(R.id.btn_camera_switcher)
    ImageButton _cameraSwitchButton;

    @BindView(R.id.emotion_switch)
    SwitchCompat _emotionSwitch;

    public static final int MAX_SUPPORTED_FACES = 1;
    private static final String LOG_TAG = "MoodSwing Camera";
    private static final int CAMERA_PERMISSIONS_REQUEST = 42;  //value is arbitrary (between 0 and 255)
    private static final int EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 73;
    private Frame mostRecentFrame;
    int cameraPreviewWidth = 0;
    int cameraPreviewHeight = 0;
    CameraDetector.CameraType cameraType;
    private boolean cameraPermissionsAvailable = false;
    private boolean storagePermissionsAvailable = false;
    private CameraDetector detector = null;
    private boolean isFrontFacingCameraDetected = true;
    private boolean isBackFacingCameraDetected = true;
    private boolean emotionEffectsEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        _cameraComponent = DaggerCameraComponent.builder()
                .cameraModule(new CameraModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .applicationComponent(applicationComponent)
                .build();
        _cameraComponent.inject(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        preproccessImages();
        initializeUI();
        checkForCameraPermissions();
        determineCameraAvailability();
        initializeCameraDetector();
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    private void determineCameraAvailability() {
        PackageManager manager = getPackageManager();
        isFrontFacingCameraDetected = manager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        isBackFacingCameraDetected = manager.hasSystemFeature(PackageManager.FEATURE_CAMERA);

        if (!isFrontFacingCameraDetected && !isBackFacingCameraDetected) {
            progressBar.setVisibility(View.INVISIBLE);
            pleaseWaitTextView.setVisibility(View.INVISIBLE);
            notFoundTextView.setVisibility(View.VISIBLE);
        }

        //restore the camera type settings
        String cameraTypeName = _sharedPreferencesManager.getCameraType();

        if (cameraTypeName.equals(CameraDetector.CameraType.CAMERA_FRONT.name())) {
            cameraType = CameraDetector.CameraType.CAMERA_FRONT;
        } else {
            cameraType = CameraDetector.CameraType.CAMERA_BACK;
        }
    }

    private void initializeUI() {
        emotionView.setZOrderMediaOverlay(true);
        cameraPreview.setZOrderMediaOverlay(false);
        initializeCameraCaptureButton();
        initializeCameraSwitchButton();
        initializeEmotionSwitch();
        emotionView.setEventListener(this);
    }

    private void initializeEmotionSwitch() {
        _emotionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                emotionEffectsEnabled = isChecked;
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Emotion effects enabled.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Emotion effects disabled.", Toast.LENGTH_LONG).show();
                    emotionView.invalidatePoints();
                }
            }
        });
    }

    private void initializeCameraSwitchButton() {
        _cameraSwitchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setCameraType(cameraType == CameraDetector.CameraType.CAMERA_FRONT ?
                        CameraDetector.CameraType.CAMERA_BACK : CameraDetector.CameraType.CAMERA_FRONT);
            }
        });
    }

    private void setCameraType(CameraDetector.CameraType cameraType) {
        //If a settings change is necessary
        if (this.cameraType != cameraType) {
            switch (cameraType) {
                case CAMERA_BACK:
                    if (isBackFacingCameraDetected) {
                        this.cameraType = CameraDetector.CameraType.CAMERA_BACK;
                    } else {
                        Toast.makeText(this, "No back-facing camera found", Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case CAMERA_FRONT:
                    if (isFrontFacingCameraDetected) {
                        this.cameraType = CameraDetector.CameraType.CAMERA_FRONT;
                    } else {
                        Toast.makeText(this, "No front-facing camera found", Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                default:
                    Log.e(LOG_TAG, "Unknown camera type selected");
            }

            detector.setCameraType(cameraType);
            _sharedPreferencesManager.setCameraType(cameraType.name());
        }
    }

    private void initializeCameraCaptureButton() {
        _cameraCaptureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                takeScreenshot();
            }
        });
    }

    private void takeScreenshot() {
        // Check the permissions to see if we are allowed to save the screenshot
        if (!storagePermissionsAvailable) {
            checkForStoragePermissions();
            return;
        }

        emotionView.requestBitmap();

        /**
         * A screenshot of the drawing view is generated and processing continues via the callback
         * onBitmapGenerated() which calls processScreenshot().
         */
    }

    private void checkForCameraPermissions() {
        cameraPermissionsAvailable =
                ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        if (!cameraPermissionsAvailable) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showPermissionExplanationDialog(CAMERA_PERMISSIONS_REQUEST);
            } else {
                requestCameraPermissions();
            }
        }
    }

    private void requestCameraPermissions() {
        if (!cameraPermissionsAvailable) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSIONS_REQUEST);
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
        } else {
            takeScreenshot();
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
                CameraActivity.this);

        // set title
        alertDialogBuilder.setTitle(getResources().getString(R.string.insufficient_permissions));

        // set dialog message
        if (requestCode == CAMERA_PERMISSIONS_REQUEST) {
            alertDialogBuilder
                    .setMessage(getResources().getString(R.string.permissions_camera_needed_explanation))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.understood), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            requestCameraPermissions();
                        }
                    });
        } else if (requestCode == EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
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

        if (requestCode == CAMERA_PERMISSIONS_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.CAMERA)) {
                    cameraPermissionsAvailable = (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }

            if (!cameraPermissionsAvailable) {
                permissionsUnavailableLayout.setVisibility(View.VISIBLE);
            } else {
                permissionsUnavailableLayout.setVisibility(View.GONE);
            }
        }

        if (requestCode == EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    storagePermissionsAvailable = (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }

            if (storagePermissionsAvailable) {
                // resume taking the screenshot
                takeScreenshot();
            }
        }
    }

    private void initializeCameraDetector() {
        detector = new CameraDetector(this, cameraType, cameraPreview, MAX_SUPPORTED_FACES, Detector.FaceDetectorMode.LARGE_FACES);
        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview, MAX_SUPPORTED_FACES, Detector.FaceDetectorMode.LARGE_FACES);
        detector.setImageListener(this);
        detector.setFaceListener(this);
        detector.setOnCameraEventListener(this);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && isFrontFacingCameraDetected) {
            cameraPreview.post(new Runnable() {
                @Override
                public void run() {
                    mainWindowResumedTasks();
                }
            });
        }
    }

    void mainWindowResumedTasks() {
        //Notify the user that they can't use the app without authorizing these permissions.
        if (!cameraPermissionsAvailable) {
            permissionsUnavailableLayout.setVisibility(View.VISIBLE);
            return;
        }
        startDetector();
//        progressBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopDetector();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForCameraPermissions();
    }

    private void stopDetector() {
        if (detector.isRunning()) {
            try {
                detector.stop();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        detector.setDetectAllEmotions(false);
        detector.setDetectAllExpressions(false);
        detector.setDetectAllAppearances(false);
        detector.setDetectAllEmojis(false);
    }

    void startDetector() {
        if (!isBackFacingCameraDetected && !isFrontFacingCameraDetected) {
            return; //without any cameras detected, we cannot proceed
        }

        detector.setDetectValence(true); //this app will always detect valence
        detector.setDetectAllEmojis(true);
        detector.setDetectAllEmotions(true);
        if (!detector.isRunning()) {
            try {
                detector.start();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    }

    @Override
    public void onImageResults(List<Face> faces, Frame image, float v) {
        if (emotionEffectsEnabled) {
            mostRecentFrame = image;

            //If the faces object is null, we received an unprocessed frame
            if (faces == null) {
                return;
            }

            //If faces.size() is 0, we received a frame in which no face was detected
            if (faces.size() <= 0) {
                emotionView.invalidatePoints();
            } else if (faces.size() == 1) {
                emotionView.updatePoints(faces);
            }
        }
}

    @Override
    public void onFaceDetectionStarted() {

    }

    @Override
    public void onFaceDetectionStopped() {

    }

    @Override
    public void onCameraSizeSelected(int cameraWidth, int cameraHeight, Frame.ROTATE rotation) {
        if (rotation == Frame.ROTATE.BY_90_CCW || rotation == Frame.ROTATE.BY_90_CW) {
            cameraPreviewWidth = cameraHeight;
            cameraPreviewHeight = cameraWidth;
        } else {
            cameraPreviewWidth = cameraWidth;
            cameraPreviewHeight = cameraHeight;
        }

        cameraLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get the screen width and height, and calculate the new app width/height based on the surfaceview aspect ratio.
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int layoutWidth = displaymetrics.widthPixels;
                int layoutHeight = displaymetrics.heightPixels;

                if (cameraPreviewWidth == 0 || cameraPreviewHeight == 0 || layoutWidth == 0 || layoutHeight == 0)
                    return;

                float layoutAspectRatio = (float) layoutWidth / layoutHeight;
                float cameraPreviewAspectRatio = (float) cameraPreviewWidth / cameraPreviewHeight;

                int newWidth;
                int newHeight;

                if (cameraPreviewAspectRatio > layoutAspectRatio) {
                    newWidth = layoutWidth;
                    newHeight = (int) (layoutWidth / cameraPreviewAspectRatio);
                } else {
                    newWidth = (int) (layoutHeight * cameraPreviewAspectRatio);
                    newHeight = layoutHeight;
                }

                ViewGroup.LayoutParams params = cameraLayout.getLayoutParams();
                params.height = newHeight;
                params.width = newWidth;
                cameraLayout.setLayoutParams(params);

                progressBarLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBitmapGenerated(@NonNull final Bitmap bitmap, final String currentEmoji) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processScreenshot(bitmap, currentEmoji);
            }
        });
    }

    private void processScreenshot(final Bitmap emotionViewBitmap, final String currentEmoji) {
        if (mostRecentFrame == null) {
            Toast.makeText(getApplicationContext(), "No frame detected, aborting screenshot", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!storagePermissionsAvailable) {
            checkForStoragePermissions();
            return;
        }

        final Bitmap faceBitmap = ImageHelper.getBitmapFromFrame(mostRecentFrame);

        if (faceBitmap == null) {
            Log.e(LOG_TAG, "Unable to generate bitmap for frame, aborting screenshot");
            return;
        }

        final Bitmap finalScreenshot = Bitmap.createBitmap(faceBitmap.getWidth(), faceBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(finalScreenshot);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);

        canvas.drawBitmap(faceBitmap, 0, 0, paint);

        float scaleFactor = ((float) faceBitmap.getWidth()) / ((float) emotionViewBitmap.getWidth());
        int scaledHeight = Math.round(emotionViewBitmap.getHeight() * scaleFactor);
        canvas.drawBitmap(emotionViewBitmap, null, new Rect(0, 0, faceBitmap.getWidth(), scaledHeight), paint);

        emotionViewBitmap.recycle();
        final Bitmap previewImage = finalScreenshot.copy(finalScreenshot.getConfig(), true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setNegativeButton("Save to device", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveImage(finalScreenshot);
                        faceBitmap.recycle();
                        finalScreenshot.recycle();
                        previewImage.recycle();
                    }
                }).setNeutralButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        previewImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        faceBitmap.recycle();
                        finalScreenshot.recycle();
                        previewImage.recycle();

                        forwardBitmap(byteArray, currentEmoji);
                    }
                }).setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        faceBitmap.recycle();
                        finalScreenshot.recycle();
                        previewImage.recycle();
                    }
                });

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        final View dialogLayout = inflater.inflate(R.layout.dialog_camera_preview, null);
        final ImageView cameraPreviewImageView = (ImageView) dialogLayout.findViewById(R.id.camera_preview_dialog);
        cameraPreviewImageView.setImageBitmap(previewImage);

        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();
    }

    private void forwardBitmap(byte[] byteArray, String currentEmoji) {
        Intent intent = new Intent(this, NewEntryActivity.class);
        intent.putExtra("CAPTURE", byteArray);
        intent.putExtra("CURRENT_EMOJI", currentEmoji);
        intent.putExtra("NEW_ENTRY_INTENT", "CAMERA_ACTIVITY");
        startActivity(intent);
    }

    private void saveImage(Bitmap finalScreenshot) {
        Date now = new Date();
        String timestamp = DateFormat.format("yyyy-MM-dd_hh-mm-ss", now).toString();
        File pictureFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MoodSwing");
        if (!pictureFolder.exists()) {
            if (!pictureFolder.mkdir()) {
                Log.e(LOG_TAG, "Unable to create directory: " + pictureFolder.getAbsolutePath()); return; } }

        String screenshotFileName = timestamp + ".png";
        File screenshotFile = new File(pictureFolder, screenshotFileName);

        try {
            ImageHelper.saveBitmapToFileAsPng(finalScreenshot, screenshotFile);
        } catch (IOException e) {
            String msg = "Unable to save screenshot";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, msg, e);
            return;
        }
        ImageHelper.addPngToGallery(getApplicationContext(), screenshotFile);

        String fileSavedMessage = "Capture saved to: " + screenshotFile.getPath();
        Toast.makeText(getApplicationContext(), fileSavedMessage, Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, fileSavedMessage);
    }

    private void preproccessImages() {
        Context context = getBaseContext();

        for (Face.EMOJI emoji : Face.EMOJI.values()) {
            String emojiResourceName;
            String emojiFileName;
            String emojiName = emoji.name();

            if (emoji.equals(Face.EMOJI.UNKNOWN)) {
                emojiName = "blank";
            }
            emojiResourceName = emojiName.trim().replace(' ', '_').toLowerCase(Locale.US).concat("_emoji");
            emojiFileName = emojiResourceName + ".png";
            ImageHelper.preproccessImageIfNecessary(context, emojiFileName, emojiResourceName);
        }
    }
}
