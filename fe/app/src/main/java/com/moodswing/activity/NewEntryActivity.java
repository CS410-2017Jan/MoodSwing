package com.moodswing.activity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;

import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerNewEntryComponent;
import com.moodswing.injector.component.NewEntryComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.NewEntryModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.widget.DateBlock;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.presenter.NewEntryPresenter;
import com.moodswing.mvp.mvp.view.NewEntryView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Matthew on 2017-03-04.
 */

public class NewEntryActivity extends MoodSwingActivity implements NewEntryView, DatePickerDialog.OnDateSetListener{

    private List<DateBlock> dBlocks = new ArrayList<>();

    @Inject2
    NewEntryPresenter _newEntryPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.btn_share)
    Button _shareButton;

    @BindView(R.id.selectdatelayout)
    RelativeLayout _dateLayout;

    @BindView(R.id.title_layout)
    RelativeLayout _titleLayout;

    @BindView(R.id.post_image)
    ImageView _postImage;

    @BindView(R.id.inside_image)
    ImageView _insideImage;

    @BindView(R.id.text_date)
    TextView _dateText;

    @BindView(R.id.entry_desc)
    EditText _descText;

    @BindView(R.id.entry_title)
    EditText _titleText;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    private NewEntryComponent _newEntryComponent;
    private Bitmap capture;
    byte[] byteArray;
    private boolean isNewEntry = false;
    private boolean loadingDate = false;
    private String date = "";
    String title = "";
    String currentEmoji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newentry);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        _newEntryComponent = DaggerNewEntryComponent.builder()
                .newEntryModule(new NewEntryModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();
        _newEntryComponent.inject(this);

        initializePresenter();
        initializeShareButton();
        initializeDateButton();
        //initializePictureListener();
        initializeDate();
        initializeBottomNavigationView();
        checkIntent();
    }

    private void checkIntent() {
        currentEmoji = "UNKNOWN";
        String activityIntent = getIntent().getStringExtra("NEW_ENTRY_INTENT");
        if (activityIntent.equals("FULL_SCREEN_IMAGE_ACTIVITY")) {
            String uri = getIntent().getStringExtra("CAPTURE_URI");
            String type[] = uri.split("/");
            Uri uriPicture = Uri.parse(uri);
            if (uriPicture != null) {
                _postImage.setImageURI(uriPicture);
                _postImage.setBackgroundColor(Color.TRANSPARENT);
                _insideImage.setVisibility(View.INVISIBLE);
            }
            byteArray = convertImageToByte(uriPicture);
        }

        if(activityIntent.equals("CAMERA_ACTIVITY")) {
            byteArray = getIntent().getByteArrayExtra("CAPTURE");
            currentEmoji = getIntent().getStringExtra("CURRENT_EMOJI");
            Log.i("***" + currentEmoji, "************************************");
            if (byteArray != null) {
                capture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                _postImage.setImageBitmap(capture);
                _postImage.setBackgroundColor(Color.TRANSPARENT);
                _insideImage.setVisibility(View.INVISIBLE);
            }
        }
    }

    public byte[] convertImageToByte(Uri uri){
        byte[] data = null;
        try {
            ContentResolver cr = getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onResume() {
        super.onResume();
        _titleText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30)});
        _titleText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        loadingDate = true;
        _newEntryPresenter.getEntries();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public void onSetTitleFailure() {
        Toast.makeText(getBaseContext(), "Title failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNewEntryFailure() {
        Toast.makeText(getBaseContext(), "Capture failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEntryFailure() {
        Toast.makeText(getBaseContext(), "Failure getting entries", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError() {
        Toast.makeText(NewEntryActivity.this, "Error creating entry", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSetTitleSuccess() {
        exitToJounal();
    }

    @Override
    public void onEntrySuccess(List<JournalEntries> journalEntries) {
        String tempDate = setJournalViewDateFormat(date);
        for(JournalEntries je: journalEntries){
            DateBlock db = new DateBlock(je.getTitle(), setJournalViewDateFormat(je.getDate()), je.getId(), je.getUsername());
            dBlocks.add(db);
        }
        if(isNewEntry){
            int i = 0;
            for(DateBlock db: dBlocks){
                if(db.getDate().equals(tempDate)){
                    break;
                }
                i += 1;
            }
            DateBlock blockToUpdate = dBlocks.get(i);
            _newEntryPresenter.setTitle(title, blockToUpdate.getId());
            isNewEntry = false;
        }
        if(loadingDate){
            _descText.setHint("Write a description...");
            _titleText.setHint("Enter a title...");
            for(DateBlock db: dBlocks){
                if(db.getDate().equals(tempDate)){
                    _titleText.setEnabled(false);
                    _titleText.setHint(db.getTitle());
                    _titleText.setText(db.getTitle());
                    _titleLayout.setBackgroundResource(R.drawable.background_newentrynotselectable);
                    break;
                }else{
                    _titleText.setEnabled(true);
                    _titleText.setText("");
                    _titleLayout.setBackgroundResource(R.drawable.background_newentryselectable);
                }
            }
            loadingDate = false;
        }
    }

    @Override
    public void onNewEntrySuccess() {
        isNewEntry = true;
        _newEntryPresenter.getEntries();
    }

    private void initializeShareButton() {
        _shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String description = _descText.getText().toString();
                title = _titleText.getText().toString();
                if(!isEmpty(description) && !isEmpty(date) && !isEmpty(title)){
                    shareEntry(description);
                }
                else{
                    displayError();
                }
            }
        });
    }

    private void initializePictureListener() {
        _postImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openFullScreen();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear += 1;
        date = Integer.toString(dayOfMonth) + "/" + Integer.toString(monthOfYear) + "/" + Integer.toString(year);
        updateDateText(date);
        onResume();
    }

    private void initializeDateButton() {
        _dateLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        NewEntryActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                // dpd.getDatePicker().setMaxDate(System.currentTimeMillis()); //TODO: figure out why this dosnt work
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    private void initializePresenter() {
        _newEntryPresenter.attachView(this);
        _newEntryPresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
    }

    private void shareEntry(String description) {
        if (byteArray != null){
            MediaType mediaType = MediaType.parse("image/jpeg");
            RequestBody entryPic = RequestBody.create(mediaType, byteArray);
            RequestBody entryText = RequestBody.create(MediaType.parse("text/plain"), description);
            RequestBody entryDate = RequestBody.create(MediaType.parse("text/plain"), date);
            RequestBody entryEmotion = RequestBody.create(MediaType.parse("text/plain"), currentEmoji);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image", entryPic);
            _newEntryPresenter.uploadCapture(body, entryText, entryDate, entryEmotion);
        } else {
            _newEntryPresenter.uploadCapture(description, date);
        }

    }

    private boolean isEmpty(String s) {
        if(s != null && !s.isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        String description = _descText.getText().toString();
        if(!isEmpty(description) || capture != null){
            displayWarning("Are you sure you want to discard your post?");
        }
        else{
            exitToJounal();
        }
    }

    private void displayError() {
        AlertDialog alertDialog = new AlertDialog.Builder(NewEntryActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("The title and description cannot have an empty value.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void displayWarning(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewEntryActivity.this);
        builder.setTitle("Warning");
        builder.setMessage(s);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                exitToJounal();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void exitToJounal(){
        Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
        startActivity(intent);
    }

    public void initializeDate(){
        Date tDate = new Date();
        DateFormat firstdf = new SimpleDateFormat("dd/MM/yyyy");
        date = firstdf.format(tDate);
        DateFormat secdf = new SimpleDateFormat("MMM.d, yyyy");
        Date tempDate;
        String rDate = "";
        try {
            tempDate = firstdf.parse(date);
            rDate = secdf.format(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        _dateText.setText(rDate);
    }

    public void updateDateText(String date){
        DateFormat firstdf = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat secdf = new SimpleDateFormat("MMM.d, yyyy");
        Date tempDate;
        String rDate = "";
        try {
            tempDate = firstdf.parse(date);
            rDate = secdf.format(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        _dateText.setText(rDate);
    }

    public String setJournalViewDateFormat(String date){
        DateFormat firstdf = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat secdf = new SimpleDateFormat("MMM.d, yyyy");
        Date tempDate;
        String rDate = "";
        try {
            tempDate = firstdf.parse(date);
            rDate = secdf.format(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rDate;
    }

    public void openFullScreen(){
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("PICTURE", byteArray);
        startActivity(intent);
    }
}
