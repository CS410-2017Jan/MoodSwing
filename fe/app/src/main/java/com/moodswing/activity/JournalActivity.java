package com.moodswing.activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.widget.DateDivider;
import com.moodswing.widget.DateAdapter;
import com.moodswing.widget.DateBlock;
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

public class JournalActivity extends MoodSwingActivity implements JournalView {

    private List<DateBlock> dBlocks = new ArrayList<>();
    private List<Capture> captures = new ArrayList<>();
    private DateAdapter dAdapter;

    @Inject2
    JournalPresenter _journalPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.user_displayname)
    TextView _userDisplayName;

    @BindView(R.id.entire_container)
    RelativeLayout _entireContainer;

    @BindView(R.id.relativeLayout7)
    RelativeLayout _emojiGradient;

    @BindView(R.id.emoji1)
    ImageView _emoji1;

    @BindView(R.id.emoji2)
    ImageView _emoji2;

    @BindView(R.id.emoji1Count)
    TextView _e1Count;

    @BindView(R.id.emoji2Count)
    TextView _e2Count;

    @BindView(R.id.date_recycler_view)
    android.support.v7.widget.RecyclerView _dRecyclerView;

    @BindView(R.id.newentry_fab)
    FloatingActionButton newEntryFab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private JournalComponent _journalComponent;
    private boolean isResuming = false;
    private String displayName;

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
        setSupportActionBar(toolbar);

        if (!_journalPresenter.isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        dAdapter = new DateAdapter(dBlocks, captures, this, _journalPresenter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
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
            isResuming = true;
            _journalPresenter.getUsers();
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
            String dbid = je.getId();
            String t = je.getTitle();
            String u = je.getUsername();
            List<Comment> comments = je.getComments();

            DateBlock db = new DateBlock(t, sDate, dbid, u, comments);
            dBlocks.add(db);

            for(Capture e: capture){
                e.setCaptureDate(sDate);
                captures.add(e);
            }
        }
        dAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeletionSuccess(){
        String message = "Capture Deleted";
        showToast(message);
        onResume();
    }

    @Override
    public void onSetTitleSuccess(){
        String message = "Title changed successfully";
        showToast(message);
        onResume();
    }

    @Override
    public void onSetTitleFailure(){
        String message = "Title change failure";
        showToast(message);
        onResume();
    }

    @Override
    public void onEntryFailure(){
        String message = "Capture fetch Failure";
        showToast(message);
    }

    @Override
    public void onDeletionFailure(){
        String message = "Capture Deletion Failure";
        showToast(message);
    }

    @Override
    public void onGetUserInfoSuccess(List<User> users){
        Intent captureIntent = DateAdapter.getCaptureIntent();
        String username = DateAdapter.getCapUsername();
        int e1Count = 0;
        int e2Count = 0;
        if (isResuming){
            username = _sharedPreferencesManager.getCurrentUser();
        }
        for (User user: users){
            if (user.getUsername().equals(username)){
                displayName = user.getDisplayName();
                e1Count = 36;
                e2Count = 11;
                break;
            }
        }
        if (isResuming){
            _userDisplayName.setText(displayName);
            _e1Count.setText(Integer.toString(e1Count));
            _e2Count.setText(Integer.toString(e2Count));
            setEmojiDetails("RELAXED", "DISAPPOINTED");
            isResuming = false;
        }else{
            captureIntent.putExtra("EXTRA_DISPLAYNAME", displayName);
            startActivity(captureIntent);
        }
    }

    private void setEmojiDetails(String e1, String e2) {
        _emoji1.setBackground(setEmoji(e1));
        _emoji2.setBackground(setEmoji(e2));
        int e1Color = setColor(e1);
        int e2Color = setColor(e2);
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {e1Color, e2Color});
        gd.setCornerRadius(0f);
        _emojiGradient.setBackgroundDrawable(gd);
    }

    private Drawable setEmoji(String emotion) {
        switch (emotion) {
            case "RELAXED":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.relaxed_emoji, null);
            case "SMILEY":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.smiley_emoji, null);
            case "LAUGHING":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.laughing_emoji, null);
            case "WINK":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.wink_emoji, null);
            case "SMIRK":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.smirk_emoji, null);
            case "KISSING":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.kissing_emoji, null);
            case "STUCK_OUT_TONGUE":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.stuck_out_tongue_emoji, null);
            case "STUCK_OUT_TONGUE_WINKING_EYE":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.stuck_out_tongue_winking_eye_emoji, null);
            case "DISAPPOINTED":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.disappointed_emoji, null);
            case "RAGE":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.rage_emoji, null);
            case "SCREAM":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.scream_emoji, null);
            case "FLUSHED":
                return ResourcesCompat.getDrawable(getResources(), R.drawable.flushed_emoji, null);
            default: // UNKNOWN
                return ResourcesCompat.getDrawable(getResources(), R.drawable.blank_emoji, null);
        }
    }

    private int setColor(String emotion) {
        switch (emotion) {
            case "RELAXED":
                return 0xFFFFF696;
            case "SMILEY":
                return 0xFFFCEF62;
            case "LAUGHING":
                return 0xFFFFAB5A;
            case "WINK":
                return 0xFF8AC980;
            case "SMIRK":
                return 0xFFB26731;
            case "KISSING":
                return 0xFFFF9696;
            case "STUCK_OUT_TONGUE":
                return 0xFFEA9550;
            case "STUCK_OUT_TONGUE_WINKING_EYE":
                return 0xFFEA9550;
            case "DISAPPOINTED":
                return 0xFFBFFCFF;
            case "RAGE":
                return 0xFFFF912D;
            case "SCREAM":
                return 0xFFD39EFF;
            case "FLUSHED":
                return 0xFFFF3F3F;
            default: // UNKNOWN
                return 0xFFFFFFFF;
        }
    }

    @Override
    public void onGetUserInfoFailure(){
        String message = "User Failure";
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
                intent.putExtra("NEW_ENTRY_INTENT", "JOURNAL_ACTIVITY");
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

