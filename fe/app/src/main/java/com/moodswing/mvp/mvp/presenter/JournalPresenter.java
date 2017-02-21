package com.moodswing.mvp.mvp.presenter;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.GetJournalsUsecase;
import com.moodswing.mvp.mvp.view.JournalView;

/**
 * Created by daniel on 11/02/17.
 */

public class JournalPresenter implements Presenter<JournalView> {
    private JournalView journalView;
    private GetJournalsUsecase getJournalsUsecase;
    private SharedPreferencesManager sharedPreferencesManager;

    public JournalPresenter(GetJournalsUsecase getJournalsUsecase) {
        this.getJournalsUsecase = getJournalsUsecase;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void attachView(JournalView view) {
        this.journalView = view;
    }

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }

    public boolean isUserLoggedIn() {
        return sharedPreferencesManager.isUserLoggedIn();
    }
}
