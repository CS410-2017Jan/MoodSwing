package com.moodswing.mvp.mvp.presenter;

import android.app.ProgressDialog;
import android.content.Context;

import com.moodswing.R;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.NewEntryUsecase;
import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.NewEntryResponse;
import com.moodswing.mvp.mvp.model.Post;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.view.NewEntryView;

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
    private Disposable newEntrySubscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public NewEntryPresenter(NewEntryUsecase newEntryUsecase) {
        this.newEntryUsecase = newEntryUsecase;
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


    public void uploadPost(String description, String date, String token) {
        newEntryUsecase.setPost(new Post(description, date));
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


    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }
}
