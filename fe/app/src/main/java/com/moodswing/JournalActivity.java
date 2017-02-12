package com.moodswing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mvp.mvp.model.JournalEntry;
import com.mvp.mvp.view.JournalView;

import java.util.List;

public class JournalActivity extends AppCompatActivity implements JournalView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
