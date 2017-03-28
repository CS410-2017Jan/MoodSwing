package com.moodswing.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerNotificationsComponent;
import com.moodswing.injector.component.NotificationsComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.NotificationsModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.CaptureDivider;
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.presenter.NotificationsPresenter;
import com.moodswing.mvp.mvp.view.NotificationsView;
import com.moodswing.widget.CaptureTouchListener;
import com.moodswing.widget.DateBlock;
import com.moodswing.widget.DateDivider;
import com.moodswing.widget.NotificationsAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

/**
 * Created by Kenny on 2017-03-25.
 */

public class NotificationsActivity extends MoodSwingActivity implements NotificationsView {
    private List<DateBlock> dBlocks = new ArrayList<>();
    private List<Capture> captures = new ArrayList<>();
    private List<JournalEntries> journals = new ArrayList<>();
    private NotificationsAdapter nAdapter;
    public static Intent captureIntent;

    @Inject2
    NotificationsPresenter _notificationsPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.notifications_recycler_view)
    android.support.v7.widget.RecyclerView _nRecyclerView;

    private NotificationsComponent _notificationsComponent;
    private boolean isResuming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_main);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();
        _notificationsComponent = DaggerNotificationsComponent.builder()
                .notificationsModule(new NotificationsModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();
        _notificationsComponent.inject(this);

        initializePresenter();
        initializeBottomNavigationView();

        nAdapter = new NotificationsAdapter(captures);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        _nRecyclerView.setLayoutManager(layoutManager);
        _nRecyclerView.setItemAnimator(new DefaultItemAnimator());
        _nRecyclerView.addItemDecoration(new DateDivider(this, R.drawable.divider));
        _nRecyclerView.setAdapter(nAdapter);

        _nRecyclerView.addOnItemTouchListener(new CaptureTouchListener(this, _nRecyclerView, new CaptureTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {
                view.setBackgroundColor(Color.GRAY);
                openCapture(captures.get(position));
            }
            
        }));

    }

    private void openCapture(Capture capture){
        String capTitle = capture.getNotificationTitle();
        String capDate = capture.getDate();
        String capText = capture.getText();
        String capId = capture.getId();

        String dateId = null;
        String capUsername = capture.getNotificationUsername();
        
        captureIntent = new Intent(this, CaptureActivity.class);
        captureIntent.putExtra("EXTRA_TITLE", capTitle);
        captureIntent.putExtra("EXTRA_DATE", capDate);
        captureIntent.putExtra("EXTRA_TEXT", capText);
        captureIntent.putExtra("EXTRA_DATEID", dateId);
        captureIntent.putExtra("EXTRA_CAPID", capId);

        startActivity(captureIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (_notificationsPresenter.isUserLoggedIn()) {
            setTitle(_sharedPreferencesManager.getCurrentUser() + "'s " + "Notifications");
            captures.clear();
            isResuming = true;
            _notificationsPresenter.getUser();
            _notificationsPresenter.getNotifications();
        }
    }
    private void initializePresenter() {
        _notificationsPresenter.attachView(this);
        _notificationsPresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
    }

    @Override
    public void showEntries(List<JournalEntries> journalEntries){
        for(JournalEntries je: journalEntries){
            journals.add(je);
            List<Capture> capture = je.getEntry();
            String date = je.getDate();
            String title = je.getTitle();
            String username = je.getUsername();

            for(Capture e: capture){
                e.setNotifyTitle(title);
                e.setNotificationUsername(username);
                e.setCaptureDate(date);
                captures.add(e);
                _notificationsPresenter.getUserDisplayName(username, e.getId());
//                _notificationsPresenter.getEntryPic(e.getId());
            }
        }
    }

    @Override
    public void showEntryPic(ResponseBody picture, String captureId){
        Boolean hasImage = true;
        Bitmap bitmap = null;
        if (picture.contentLength() == 0) {
            hasImage = false;
        } else {
            bitmap = BitmapFactory.decodeStream(picture.byteStream());
        }
        updatePicture(bitmap, captureId, hasImage);
    }

    private void updatePicture(Bitmap bitmap, String captureId, Boolean hasImage) {
        for(Capture c: captures){
            if (c.getId().equals(captureId)){
                c.setImage(bitmap);
                c.setHasImage(hasImage);
            }
        }
    }

    @Override
    public void onGetDisplayName(String displayName, final String username, String captureID) {
        for (final Capture c : captures) {
            if (c.getId().equals(captureID)) {
                c.setDisplayName(displayName);
            }
        }
        nAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetDisplayNameFailure() {

    }

    @Override
    public void onGetUserInfoSuccess(User user) {

    }

    @Override
    public void onGetUserInfoFailure(){
        String message = "User Failure";
        showToast(message);
    }

    private void showToast(String s) {
        Toast.makeText(NotificationsActivity.this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError() {
        String message = "Error fetching entries";
        showToast(message);
    }

    @Override
    public void onEntryFailure() {
        String message = "Capture fetch Failure";
        showToast(message);
    }
}
