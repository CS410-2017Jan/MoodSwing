package com.moodswing.activity;

import android.os.Bundle;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerNotificationsComponent;
import com.moodswing.injector.component.NotificationsComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.NotificationsModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.presenter.NotificationsPresenter;
import com.moodswing.mvp.mvp.view.NotificationsView;

import javax.inject.Inject2;

import butterknife.ButterKnife;

/**
 * Created by Kenny on 2017-03-25.
 */

public class NotificationsActivity extends MoodSwingActivity implements NotificationsView {

    @Inject2
    NotificationsPresenter _notificationsPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    private NotificationsComponent _notificationsComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
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
    }

    private void initializePresenter() {
        _notificationsPresenter.attachView(this);
        _notificationsPresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
    }


}
