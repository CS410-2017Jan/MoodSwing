package com.moodswing.mvp.mvp.presenter;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.DeleteCaptureUsecase;
import com.moodswing.mvp.domain.GetJournalsUsecase;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.DeleteCaptureResponse;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.view.JournalView;

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
    private DeleteCaptureUsecase deleteCaptureUsecase;
    private Disposable getJounalsSubscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public JournalPresenter(GetJournalsUsecase getJournalsUsecase, DeleteCaptureUsecase deleteCaptureUsecase) {
        this.getJournalsUsecase = getJournalsUsecase;
        this.deleteCaptureUsecase = deleteCaptureUsecase;
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
                .subscribe(new Consumer<List<JournalEntries>>() {
                    @Override
                    public void accept(List<JournalEntries> journalEntries) throws Exception {
                        if (true) {
                            journalView.showEntries(journalEntries);
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


    public void deleteCapture(Capture capture) {
        deleteCaptureUsecase.setDeletionId(capture.getId());
        deleteCaptureUsecase.setToken(sharedPreferencesManager.getToken());

        getJounalsSubscription = deleteCaptureUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DeleteCaptureResponse>() {
                    @Override
                    public void accept(DeleteCaptureResponse deleteCaptureResponse) throws Exception {
                        if (deleteCaptureResponse.isSuccessful()) {
                            journalView.onDeletionSuccess();
                        } else {
                            journalView.onDeletionFailure();
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
