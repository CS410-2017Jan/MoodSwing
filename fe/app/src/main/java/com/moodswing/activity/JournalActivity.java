package com.moodswing.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import java.util.List;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

public class JournalActivity extends Journal implements JournalView {

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

    @BindView(R.id.imageView)
    ImageView _profilePic;

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
    private DateAdapter dAdapter;
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
        initializeProfilePic();

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

    private void initializeProfilePic() {
        _profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        _dRecyclerView.setFocusable(false);
        if (_journalPresenter.isUserLoggedIn()) {
            toolbar.setTitleTextColor(Color.WHITE);
            setTitle(_sharedPreferencesManager.getCurrentUser() + "'s " + "MoodSwings");
            captures.clear();
            dBlocks.clear();
            isResuming = true;
            _journalPresenter.getUser();
            _journalPresenter.getProfilePic();
            _journalPresenter.getEntries();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        _profilePic.setImageBitmap(null);
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
                _journalPresenter.getEntryPic(e.getId());
            }
        }
    }

    @Override
    public void onDeletionSuccess(){
        String message = "Capture Deleted";
        showToast(message);
        onResume();
    }

    @Override
    public void getPicture(ResponseBody picture){
        if (picture.contentLength() == 0) {
            _profilePic.setBackgroundResource(R.drawable.empty_profile_pic);
        } else {
            Bitmap bitmap = BitmapFactory.decodeStream(picture.byteStream());
            _profilePic.setBackgroundResource(android.R.color.transparent);
            _profilePic.setImageBitmap(bitmap);
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
                c.setWaitingForImageResponse(false);
            }
        }
        dAdapter.notifyDataSetChanged();
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
    public void onGetUserInfoSuccess(User user){
        Intent captureIntent = DateAdapter.getCaptureIntent();
        String username = DateAdapter.getCapUsername();
        String e1Count = "";
        String e1 = "";
        String e2Count = "";
        String e2 = "";
        if (isResuming){
            username = _sharedPreferencesManager.getCurrentUser();
        }
        if (user.getUsername().equals(username)){
            displayName = user.getDisplayName();
            List<List<String>> sortedEmotions = user.getSortedEmotions();
            int i = 0;
            for (List<String> ec: sortedEmotions){
                if (i == 0){
                    e1 = ec.get(0);
                    e1Count = ec.get(1);
                }
                if (i == 1){
                    e2 = ec.get(0);
                    e2Count = ec.get(1);
                }
                i++;
            }
        }
        if (isResuming){
            _userDisplayName.setText(displayName);
            _e1Count.setText(e1Count);
            _e2Count.setText(e2Count);
            setEmojiDetails(e1, e2);
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
                }
                break;
            case R.id.action_search:
                Intent intent3 = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent3);
            default:
                return true;
        }
        return true;
    }
}

