package com.moodswing.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerJournalComponent;
import com.moodswing.injector.component.JournalComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.JournalModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.CapturesAdapter;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.presenter.JournalPresenter;
import com.moodswing.mvp.mvp.view.JournalView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalActivity extends AppCompatActivity implements JournalView {


    private List<Capture> captures = new ArrayList<>();
    private CapturesAdapter cAdapter;

    @Inject2
    JournalPresenter _journalPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.btn_logout)
    ImageButton _logoutButton;

    @BindView(R.id.btn_addnew)
    ImageButton _addEntryButton;

    @BindView(R.id.btn_edit_profile)
    ImageButton _editprofileButton;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.btn_camera)
    Button _cameraButton;

    @BindView(R.id.recycler_view)
    android.support.v7.widget.RecyclerView _recyclerView;


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
        initializeEditProfileButton();
        initializeBottomNavigationView();

        if (!_journalPresenter.isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        cAdapter = new CapturesAdapter(captures);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        _recyclerView.setLayoutManager(layoutManager);
        _recyclerView.setItemAnimator(new DefaultItemAnimator());
        _recyclerView.setAdapter(cAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (_journalPresenter.isUserLoggedIn()) {
            _journalPresenter.getEntries();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void showEntries(List<JournalEntries> journalEntries){
        for(JournalEntries je: journalEntries){
            List<Capture> capture = je.getEntry();
            for(Capture e: capture){
                captures.add(e);
            }
        }
        cAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEntryFailure(){
        String message = "Capture fetch Failure";
        showToast(message);
    }

    @Override
    public void showError() {
        String message = "Error fetching entries";
        showToast(message);
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
                Intent intent = new Intent(getApplicationContext(), NewEntryActivity.class);
                startActivity(intent);
            }
        });
    }


    private void initializeEditProfileButton() {
        _editprofileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //TODO: set some restrictions on editing profile
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
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

    private void initializePresenter() {
        _journalPresenter.attachView(this);
        _journalPresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
    }

    private void showToast(String s){
        Toast.makeText(JournalActivity.this, s, Toast.LENGTH_LONG).show();
    }
}

