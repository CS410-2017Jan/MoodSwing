package com.moodswing.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
//import com.moodswing.injector.component.CaptureComponent;
import com.moodswing.mvp.data.SharedPreferencesManager;
//import com.moodswing.mvp.mvp.presenter.CapturePresenter;
import com.moodswing.mvp.mvp.view.CaptureView;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Matthew on 2017-03-12.
 */

public class CaptureActivity extends AppCompatActivity implements CaptureView{

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

//    private CaptureComponent _captureComponent;

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

//        initializePresenter();
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
}
