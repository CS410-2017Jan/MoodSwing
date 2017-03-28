package com.moodswing.mvp.mvp.presenter;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.GetUserUsecase;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.view.FollowersView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by daniel on 24/03/17.
 */

public class FollowersPresenter implements Presenter<FollowersView> {
    private FollowersView followersView;
    private GetUserUsecase getUserUsecase;
    private Disposable subscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public FollowersPresenter(GetUserUsecase getUserUsecase) {
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
    public void attachView(FollowersView view) {
        this.followersView = view;
    }

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }

    public void getFollowers(){
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
                            List<String> followers = user.getFollowers();
                            getFollowersInfo(followers);
                        } else {
                            followersView.showError("Unable to retrieve followers.");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        followersView.showError("Error encountered: " + throwable.getMessage());
                    }
                });
    }

    private void getFollowersInfo(List<String> followers) {
        Disposable subscription;
        final List<User> followerUsers = new ArrayList<>();
        final int size = followers.size();

        for (String username : followers) {
            getUserUsecase.setUsername(username);
            subscription = getUserUsecase.execute()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Response<User>>() {
                        @Override
                        public void accept(Response<User> response) throws Exception {
                            User user = response.body();
                            if (response.code() == 200) {
                                followerUsers.add(user);
                                if (followerUsers.size() == size) {
                                    getFollows(followerUsers);
                                }
                            } else {
                                followersView.showError("Unable to retrieve user info.");
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            followersView.showError("Error encountered: " + throwable.getMessage());
                        }
                    });
        }
    }

    public void getFollows(final List<User> followerUsers) {
        getUserUsecase.setUsername(sharedPreferencesManager.getCurrentUser());
        Disposable getFollowsSubscription = getUserUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<User>>() {
                    @Override
                    public void accept(Response<User> response) throws Exception {
                        if (response.code() == 200) {
                            List<String> follows = response.body().getFollowing();
                            followersView.initializeListView(followerUsers, follows);
                        } else {
                            followersView.showError("Server encountered an error");
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        followersView.showError("Server encountered an error " + throwable.getMessage());
                    }
                });
    }
}
