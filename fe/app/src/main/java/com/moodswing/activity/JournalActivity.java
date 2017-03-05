package com.moodswing.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerJournalComponent;
import com.moodswing.injector.component.JournalComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.JournalModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.model.JournalEntry;
import com.moodswing.mvp.mvp.presenter.JournalPresenter;
import com.moodswing.mvp.mvp.view.JournalView;

import java.util.List;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalActivity extends AppCompatActivity implements JournalView {
    @Inject2
    JournalPresenter _journalPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.btn_logout)
    Button _logoutButton;

    @BindView(R.id.btn_addnew)
    ImageButton _addEntryButton;

    private JournalComponent _journalComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        _journalComponent = DaggerJournalComponent.builder()
                .journalModule(new JournalModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();
        _journalComponent.inject(this);

        initializePresenter();
        initializeLogoutButton();
        initializeAddEntryButton();

        if (!_journalPresenter.isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
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

    @Override
    public void showJournals(List<JournalEntry> journalEntries) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showError() {

    }

    private void initializeLogoutButton() {
        _logoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: this is stubbed
                String currentUser = _sharedPreferencesManager.getCurrentUser();
                if (currentUser != null) {
                    _sharedPreferencesManager.logout(currentUser);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // TODO: ERROR... App should shutdown
                }
            }
        });
    }


    private void initializeAddEntryButton() {
        _addEntryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
            }
        });
    }

    private void initializePresenter() {
        _journalPresenter.attachView(this);
        _journalPresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
    }
}
