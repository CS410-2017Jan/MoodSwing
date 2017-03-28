package com.moodswing.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.presenter.NotificationsPresenter;
import com.moodswing.mvp.mvp.view.NotificationsView;
import com.moodswing.widget.DateBlock;
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
    private NotificationsAdapter nAdapter;

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
        _nRecyclerView.setAdapter(nAdapter);

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
            List<Capture> capture = je.getEntry();

            String date = je.getDate();
            String title = je.getTitle();
            String username = je.getUsername();

            for(Capture e: capture){
                e.setNotifyTitle(title);
                e.setCaptureDate(date);
                captures.add(e);
                _notificationsPresenter.getUserDisplayName(username, e.getId());

//                _notificationsPresenter.getEntryPic(e.getId());
            }
            nAdapter.notifyDataSetChanged();
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
    public void onGetDisplayName(String displayName, String captureID) {
        for (Capture c: captures) {
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
