package com.moodswing.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerJournalComponentOther;
import com.moodswing.injector.component.JournalComponentOther;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.JournalModuleOther;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.presenter.JournalPresenterOther;
import com.moodswing.mvp.mvp.view.JournalViewOther;
import com.moodswing.widget.DateAdapterOther;
import com.moodswing.widget.DateDivider;
import com.moodswing.widget.DateBlock;
import com.moodswing.mvp.mvp.model.JournalEntries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

public class JournalActivityOther extends Journal implements JournalViewOther {

    @Inject2
    JournalPresenterOther _journalPresenterOther;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.user_displaynameother)
    TextView _userDisplayName;

    @BindView(R.id.entire_containerother)
    RelativeLayout _entireContainer;

    @BindView(R.id.relativeLayout7other)
    RelativeLayout _emojiGradient;

    @BindView(R.id.imageViewother)
    ImageView _profilePic;

    @BindView(R.id.emoji1other)
    ImageView _emoji1;

    @BindView(R.id.emoji2other)
    ImageView _emoji2;

    @BindView(R.id.emoji1Countother)
    TextView _e1Count;

    @BindView(R.id.emoji2Countother)
    TextView _e2Count;

    @BindView(R.id.date_recycler_viewother)
    android.support.v7.widget.RecyclerView _dRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private JournalComponentOther _journalComponentOther;
    private DateAdapterOther dAdapter;
    private boolean isResuming = false;
    private String displayName;
    private String username;
    private List<String> follows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainother);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        _journalComponentOther = DaggerJournalComponentOther.builder()
                .journalModuleOther(new JournalModuleOther())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();
        _journalComponentOther.inject(this);

        initializePresenter();
        initializeBottomNavigationView();
        setSupportActionBar(toolbar);

        dAdapter = new DateAdapterOther(dBlocks, captures, this, _journalPresenterOther);
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
        _dRecyclerView.setFocusable(false);
        captures.clear();
        dBlocks.clear();
        isResuming = true;
        displayName = getIntent().getStringExtra("USER_DISPLAYNAME");
        username = getIntent().getStringExtra("USER_USERNAME");
        follows = new ArrayList<>();
        String[] followsArr = getIntent().getStringArrayExtra("USER_FOLLOWING");
        if (followsArr != null && followsArr.length > 0) {
            follows.addAll(Arrays.asList(followsArr));
        }
        toolbar.setTitleTextColor(Color.WHITE);
        setTitle(username + "'s " + "MoodSwings");
        _journalPresenterOther.getUser(username);
        _journalPresenterOther.getProfilePic(username);
        _journalPresenterOther.getEntries(username);
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
                _journalPresenterOther.getEntryPic(e.getId());
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
        Intent captureIntent = DateAdapterOther.getCaptureIntent();
        String username1 = DateAdapterOther.getCapUsername();
        String e1Count = "";
        String e1 = "";
        String e2Count = "";
        String e2 = "";
        if (isResuming){
            username1 = username;
        }
        if (user.getUsername().equals(username1)){
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
    public void showError(String message) {
        showToast(message);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
        startActivity(intent);
    }

    private void initializePresenter() {
        _journalPresenterOther.attachView(this);
        _journalPresenterOther.attachSharedPreferencesManager(_sharedPreferencesManager);
    }

    private void showToast(String s) {
        Toast.makeText(JournalActivityOther.this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_follow, menu);
        if (follows != null && follows.contains(username)) {
            menu.findItem(R.id.action_follow).setIcon(R.drawable.ic_star_followed);
        } else {
            menu.findItem(R.id.action_follow).setIcon(R.drawable.ic_star_unfollowed);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_follow:
                if (follows != null && follows.contains(username)) {
                    menuItem.setIcon(R.drawable.ic_star_unfollowed);
                    _journalPresenterOther.unfollow(username);
                    follows.remove(username);
                } else {
                    menuItem.setIcon(R.drawable.ic_star_followed);
                    _journalPresenterOther.follow(username);
                    follows.add(username);
                }
                break;
            default:
                return true;
        }
        return true;
    }
}