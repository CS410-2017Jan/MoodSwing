package com.moodswing.mvp.mvp.presenter;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.GetJournalsUsecase;
import com.moodswing.mvp.domain.NewEntryNoPicUsecase;
import com.moodswing.mvp.domain.NewEntryUsecase;
import com.moodswing.mvp.domain.SetTitleUsecase;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.response.NewEntryResponse;
import com.moodswing.mvp.mvp.model.response.SetTitleResponse;
import com.moodswing.mvp.mvp.model.Title;
import com.moodswing.mvp.mvp.view.NewEntryView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Matthew on 2017-03-04.
 */

public class NewEntryPresenter implements Presenter<NewEntryView> {
    private NewEntryView newEntryView;
    private NewEntryUsecase newEntryUsecase;
    private SetTitleUsecase setTitleUsecase;
    private GetJournalsUsecase getJournalsUsecase;
    private NewEntryNoPicUsecase newEntryNoPicUsecase;
    private Disposable newEntrySubscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public NewEntryPresenter(NewEntryUsecase newEntryUsecase, SetTitleUsecase setTitleUsecase, GetJournalsUsecase getJournalsUsecase, NewEntryNoPicUsecase newEntryNoPicUsecase) {
        this.newEntryUsecase = newEntryUsecase;
        this.setTitleUsecase = setTitleUsecase;
        this.getJournalsUsecase = getJournalsUsecase;
        this.newEntryNoPicUsecase = newEntryNoPicUsecase;
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


    public void uploadCapture(MultipartBody.Part data, RequestBody entryText, RequestBody entryDate) {
        newEntryUsecase.setData(data);
        newEntryUsecase.setText(entryText);
        newEntryUsecase.setDate(entryDate);
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

    public void uploadCapture(String description, String date) {
        newEntryNoPicUsecase.setCapture(new Capture(description, date));
        newEntryNoPicUsecase.setToken(sharedPreferencesManager.getToken());

        newEntrySubscription = newEntryNoPicUsecase.execute()
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
