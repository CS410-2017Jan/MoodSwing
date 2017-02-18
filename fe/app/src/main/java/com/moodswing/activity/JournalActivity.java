package com.moodswing.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.moodswing.R;
import com.moodswing.mvp.mvp.model.JournalEntry;
import com.moodswing.mvp.mvp.view.JournalView;

import java.util.List;

public class JournalActivity extends AppCompatActivity implements JournalView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if (!isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: check shared preferences
        //if (!isLoggedIn()) {
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
        //}
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

}
