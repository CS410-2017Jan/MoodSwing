package com.moodswing.mvp.mvp.presenter;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.GetJournalsUsecase;
import com.moodswing.mvp.mvp.model.GetEntriesResponse;
import com.moodswing.mvp.mvp.model.Post;
import com.moodswing.mvp.mvp.view.JournalView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by daniel on 11/02/17.
 */

public class JournalPresenter implements Presenter<JournalView> {
    private JournalView journalView;
    private GetJournalsUsecase getJournalsUsecase;
    private Disposable getJounalsSubscription;
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

    public void getEntries() {
        getJournalsUsecase.setUsername(sharedPreferencesManager.getCurrentUser());

        getJounalsSubscription = getJournalsUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GetEntriesResponse>() {
                    @Override
                    public void accept(GetEntriesResponse getEntriesResponse) throws Exception {
                        if (getEntriesResponse.isSuccessful()) {
                            journalView.showEntries();
                        } else {
                            journalView.onEntryFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        journalView.showError();
                    }
                });
    }

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }

    public boolean isUserLoggedIn() {
        return sharedPreferencesManager.isUserLoggedIn();
    }
}
