package com.moodswing.mvp.mvp.presenter;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.GetNotificationsUsecase;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.view.NotificationsView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kenny on 2017-03-25.
 */

public class NotificationsPresenter implements Presenter<NotificationsView> {
    private NotificationsView notificationsView;
    private GetNotificationsUsecase getNotificationsUsecase;
    private SharedPreferencesManager sharedPreferencesManager;
    private Disposable notificationsSubscription;


    public NotificationsPresenter(GetNotificationsUsecase getNotificationsUsecase) {
        this.getNotificationsUsecase = getNotificationsUsecase;
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
    public void attachView(NotificationsView view) {
        this.notificationsView = view;
    }

    public void getNotifications() {
        getNotificationsUsecase.setToken(sharedPreferencesManager.getToken());

        notificationsSubscription = getNotificationsUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<JournalEntries>>() {
                    @Override
                    public void accept(List<JournalEntries> journalEntries) throws Exception {

                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable)throws Exception {

                    }
                });
    }

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }
}
