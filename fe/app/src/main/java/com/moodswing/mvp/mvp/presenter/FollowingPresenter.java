package com.moodswing.mvp.mvp.presenter;

import android.util.Log;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.GetUserUsecase;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.view.FollowingView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by daniel on 23/03/17.
 */

public class FollowingPresenter implements Presenter<FollowingView>{
    private FollowingView view;
    private GetUserUsecase getUserUsecase;
    private Disposable subscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public FollowingPresenter(GetUserUsecase getUserUsecase) {
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
    public void attachView(FollowingView view) {
        this.view = view;
    }

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }

    public void getFollowing(){
        String username = sharedPreferencesManager.getCurrentUser();
        getUserUsecase.setUsername(username);

        subscription = getUserUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<User>>() {
                    @Override
                    public void accept(Response<User> response) throws Exception {
                        User user = response.body();
                        if (response.code() == 200) {
                            List<String> following = user.getFollowing();
                            getFollowingInfo(following);
                        } else {
                            view.showError("Unable to retrieve following.");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        view.showError("Error encountered: " + throwable.getMessage());
                    }
                });
    }

    private void getFollowingInfo(List<String> following) {
        Disposable subscription;
        final List<User> followingUsers = new ArrayList<>();
        final int size = following.size();

        for (String username : following) {
            getUserUsecase.setUsername(username);
            subscription = getUserUsecase.execute()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Response<User>>() {
                        @Override
                        public void accept(Response<User> response) throws Exception {
                            User user = response.body();
                            if (response.code() == 200) {
                                followingUsers.add(user);
                                if (followingUsers.size() == size) {
                                    view.initializeListView(followingUsers);
                                }
                            } else {
                                view.showError("Unable to retrieve user info.");
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            view.showError("Error encountered: " + throwable.getMessage());
                        }
                    });
        }
    }
}
