package com.moodswing.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.ImageButton;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerNewEntryComponent;
import com.moodswing.injector.component.NewEntryComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.NewEntryModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.presenter.NewEntryPresenter;
import com.moodswing.mvp.mvp.view.NewEntryView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Matthew on 2017-03-04.
 */

public class NewEntryActivity extends AppCompatActivity implements NewEntryView, DatePickerDialog.OnDateSetListener{

    private NewEntryView newEntryView;

    @Inject2
    NewEntryPresenter _newEntryPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.btn_share)
    Button _shareButton;

    @BindView(R.id.btn_date)
    ImageButton _dateButton;

    @BindView(R.id.entry_desc)
    EditText _descText;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    private NewEntryComponent _newEntryComponent;
    private Bitmap capture;
    private String date = "";

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
        initializeBottomNavigationView();

        checkIntent();
    }

    private void checkIntent() {
        // TODO: Verify that this works
        byte[] byteArray = getIntent().getByteArrayExtra("CAPTURE");

        if (byteArray != null) {
            capture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    public void onNewEntryFailure() {
        Toast.makeText(getBaseContext(), "Capture failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError() {
        Toast.makeText(NewEntryActivity.this, "Error creating entry", Toast.LENGTH_LONG).show();
    }

    public void onNewEntrySuccess() {
        exitToJounal();
    }

    private void initializeShareButton() {
        _shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String description = _descText.getText().toString();
                if(!isEmpty(description) && !isEmpty(date)){
                    shareEntry(description);
                }
                else{
                    displayError();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        // TODO: use date
        date = Integer.toString(dayOfMonth) + "/" + Integer.toString(monthOfYear) + "/" + Integer.toString(year);
    }

    private void initializeDateButton() {
        _dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        NewEntryActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    private void initializePresenter() {
        _newEntryPresenter.attachView(this);
        _newEntryPresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
    }

    private void shareEntry(String description) {
        _newEntryPresenter.uploadCapture(description, date);
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
        if(!isEmpty(description)){
            displayWarning("Are you sure you want to discard your post?");
        }
        else{
            exitToJounal();
        }
    }

    private void displayError() {
        AlertDialog alertDialog = new AlertDialog.Builder(NewEntryActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("The description and date cannot have an empty value.");
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

    private void initializeBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        // TODO: Re-direct to search
                        break;
                    case R.id.action_camera:
                        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_follows:
                        // TODO: Re-direct to follows
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
}
