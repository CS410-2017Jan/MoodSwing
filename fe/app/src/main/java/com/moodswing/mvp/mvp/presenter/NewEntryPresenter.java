package com.moodswing.mvp.mvp.presenter;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.GetJournalsUsecase;
import com.moodswing.mvp.domain.NewEntryUsecase;
import com.moodswing.mvp.domain.SetTitleUsecase;
import com.moodswing.mvp.mvp.model.journalobjects.Capture;
import com.moodswing.mvp.mvp.model.journalobjects.JournalEntries;
import com.moodswing.mvp.mvp.model.NewEntryResponse;
import com.moodswing.mvp.mvp.model.SetTitleResponse;
import com.moodswing.mvp.mvp.model.journalobjects.Title;
import com.moodswing.mvp.mvp.view.NewEntryView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Matthew on 2017-03-04.
 */

public class NewEntryPresenter implements Presenter<NewEntryView> {
    private NewEntryView newEntryView;
    private NewEntryUsecase newEntryUsecase;
    private SetTitleUsecase setTitleUsecase;
    private GetJournalsUsecase getJournalsUsecase;
    private Disposable newEntrySubscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public NewEntryPresenter(NewEntryUsecase newEntryUsecase, SetTitleUsecase setTitleUsecase, GetJournalsUsecase getJournalsUsecase) {
        this.newEntryUsecase = newEntryUsecase;
        this.setTitleUsecase = setTitleUsecase;
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
    public void attachView(NewEntryView view) {
        this.newEntryView = view;
    }


    public void uploadCapture(String description, String date) {
        newEntryUsecase.setCapture(new Capture(description, date));
        newEntryUsecase.setToken(sharedPreferencesManager.getToken());

        newEntrySubscription = newEntryUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NewEntryResponse>() {
                    @Override
                    public void accept(NewEntryResponse newEntryResponse) throws Exception {
                        if (newEntryResponse.isSuccessful()) {
                            newEntryView.onNewEntrySuccess();
                        } else {
                            newEntryView.onNewEntryFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        newEntryView.showError();
                    }
                });
    }

    public void setTitle(String title, String id) {
        setTitleUsecase.setDateId(id);
        setTitleUsecase.setTitle(new Title(title));
        setTitleUsecase.setToken(sharedPreferencesManager.getToken());

        newEntrySubscription = setTitleUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SetTitleResponse>() {
                    @Override
                    public void accept(SetTitleResponse setTitleResponse) throws Exception {
                        if (setTitleResponse.isSuccessful()) {
                            newEntryView.onSetTitleSuccess();
                        } else {
                            newEntryView.onSetTitleFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        newEntryView.showError();
                    }
                });
    }

    public void getEntries() {
        getJournalsUsecase.setUsername(sharedPreferencesManager.getCurrentUser());

        newEntrySubscription = getJournalsUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<JournalEntries>>() {
                    @Override
                    public void accept(List<JournalEntries> journalEntries) throws Exception {
                        if (true) {
                            newEntryView.onEntrySuccess(journalEntries);
                        } else {
                            newEntryView.onEntryFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        newEntryView.showError();
                    }
                });
    }


    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }
}
