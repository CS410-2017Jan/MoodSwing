package com.moodswing.mvp.mvp.presenter;

import android.util.Log;
import android.widget.Toast;

import com.moodswing.activity.SearchActivity;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.GetUserUsecase;
import com.moodswing.mvp.domain.SearchUsecase;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.view.SearchView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by daniel on 16/03/17.
 */

public class SearchPresenter implements Presenter<SearchView> {
    private SearchView searchView;
    private SharedPreferencesManager sharedPreferencesManager;
    private SearchUsecase searchUsecase;
    private GetUserUsecase getUserUsecase;
    private Disposable searchSubscription;

    public SearchPresenter(SearchUsecase searchUsecase, GetUserUsecase getUserUsecase) {
        this.searchUsecase = searchUsecase;
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
    public void attachView(SearchView view) {
        searchView = view;
    }

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }

    public void search() {
        searchSubscription = searchUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<List<User>>>() {
                    @Override
                    public void accept(Response<List<User>> listResponse) throws Exception {
                        String currentUser = sharedPreferencesManager.getCurrentUser();
                        List<User> users = listResponse.body();
                        if (listResponse.code() == 200) {
                            getFollows(users);
                        } else {
                            searchView.showError("Server responded with: " + listResponse.errorBody());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        searchView.showError(throwable.getMessage());
                    }
                });
    }

    public void getFollows(final List<User> users) {
        getUserUsecase.setUsername(sharedPreferencesManager.getCurrentUser());
       Disposable getFollowsSubscription = getUserUsecase.execute()
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Consumer<Response<User>>() {
                   @Override
                   public void accept(Response<User> response) throws Exception {
                       if (response.code() == 200) {
                           List<String> follows = response.body().getFollowing();
                           searchView.initializeListView(users, follows);
                       } else {
                           searchView.showError("Server encountered an error");
                       }

                   }
               }, new Consumer<Throwable>() {
                   @Override
                   public void accept(Throwable throwable) throws Exception {
                       searchView.showError("Server encountered an error " + throwable.getMessage());
                   }
               });
    }
}
