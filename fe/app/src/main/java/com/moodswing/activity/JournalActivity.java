package com.moodswing.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.moodswing.mvp.mvp.model.DateDivider;
import com.moodswing.mvp.mvp.model.CaptureTouchListener;
import com.moodswing.mvp.mvp.model.DateAdapter;
//import com.moodswing.mvp.mvp.model.DateSeparator;
import com.moodswing.mvp.mvp.model.DateBlock;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.presenter.JournalPresenter;
import com.moodswing.mvp.mvp.view.JournalView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalActivity extends AppCompatActivity implements JournalView {

    private List<DateBlock> dBlocks = new ArrayList<>();
    private List<Capture> captures = new ArrayList<>();
    private DateAdapter dAdapter;

    @Inject2
    JournalPresenter _journalPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.date_recycler_view)
    android.support.v7.widget.RecyclerView _dRecyclerView;

    @BindView(R.id.newentry_fab)
    FloatingActionButton newEntryFab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

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

        initializeBottomNavigationView();
        initializeNewEntryFab();
//        initializeSettingsButton();
        setSupportActionBar(toolbar);

        if (!_journalPresenter.isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        dAdapter = new DateAdapter(dBlocks, captures, this, getApplicationContext(), _journalPresenter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        _dRecyclerView.setLayoutManager(layoutManager);
        _dRecyclerView.setItemAnimator(new DefaultItemAnimator());
        _dRecyclerView.addItemDecoration(new DateDivider(this, R.drawable.divider));
        _dRecyclerView.setAdapter(dAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (_journalPresenter.isUserLoggedIn()) {
            toolbar.setTitleTextColor(Color.WHITE);
            setTitle(_sharedPreferencesManager.getCurrentUser() + "'s " + "MoodSwings");
            captures.clear();
            dBlocks.clear();
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

            String sDate = setJournalViewDateFormat(je.getDate());
            DateBlock db = new DateBlock("Title", sDate);
            dBlocks.add(db);

            for(Capture e: capture){
                e.setCaptureDate(sDate);
                captures.add(e);
            }
        }
        dAdapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        finishAffinity();
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

    private void showToast(String s) {
        Toast.makeText(JournalActivity.this, s, Toast.LENGTH_LONG).show();
    }

    private void initializeNewEntryFab() {
        newEntryFab.setOnClickListener(new FloatingActionButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewEntryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                String currentUser = _sharedPreferencesManager.getCurrentUser();
                if (currentUser != null) {
                    _sharedPreferencesManager.logout(currentUser);
                    Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent2);
                } else {
                    // TODO: ERROR... App should shutdown
                }
                break;
            default:
                return true;
        }
        return true;
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
}

