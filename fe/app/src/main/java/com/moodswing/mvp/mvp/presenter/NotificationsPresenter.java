package com.moodswing.mvp.mvp.presenter;

import android.util.Log;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.GetEntryPicUsecase;
import com.moodswing.mvp.domain.GetNotificationsUsecase;
import com.moodswing.mvp.domain.GetUserUsecase;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.view.NotificationsView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Kenny on 2017-03-25.
 */

public class NotificationsPresenter implements Presenter<NotificationsView> {
    private NotificationsView notificationsView;
    private GetNotificationsUsecase getNotificationsUsecase;
    private GetEntryPicUsecase getEntryPicUsecase;
    private GetUserUsecase getUserUsecase;
    private SharedPreferencesManager sharedPreferencesManager;
    private Disposable notificationsSubscription;


    public NotificationsPresenter(GetNotificationsUsecase getNotificationsUsecase, GetEntryPicUsecase getEntryPicUsecase, GetUserUsecase getUserUsecase) {
        this.getNotificationsUsecase = getNotificationsUsecase;
        this.getEntryPicUsecase = getEntryPicUsecase;
        this.getUserUsecase = getUserUsecase;
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
                        if (true) {
                            notificationsView.showEntries(journalEntries);
                        } else {
                            notificationsView.onEntryFailure();
                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable)throws Exception {

                    }
                });
    }

    public void getUser() {
        getUserUsecase.setUsername(sharedPreferencesManager.getCurrentUser());
        notificationsSubscription = getUserUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<User>>() {
                    @Override
                    public void accept(Response<User> response) throws Exception {
                        if (response.code() == 200) {
                            User user = response.body();
                            notificationsView.onGetUserInfoSuccess(user);
                        } else {
                            notificationsView.onGetUserInfoFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        notificationsView.showError();
                    }
                });
    }
    public void getEntryPic(final String captureId) {
        getEntryPicUsecase.setCaptureId(captureId);
        getEntryPicUsecase.setToken(sharedPreferencesManager.getToken());

        notificationsSubscription = getEntryPicUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody picture) throws Exception {
                        notificationsView.showEntryPic(picture, captureId);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("", "****GET " + captureId + " ERROR****");
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
