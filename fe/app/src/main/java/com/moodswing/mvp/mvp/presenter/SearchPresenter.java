package com.moodswing.mvp.mvp.presenter;

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
    private SearchUsecase searchUsecase;
    private Disposable searchSubscription;

    public SearchPresenter(SearchUsecase searchUsecase) {
        this.searchUsecase = searchUsecase;
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

    public void search() {
        searchSubscription = searchUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<List<User>>>() {
                    @Override
                    public void accept(Response<List<User>> listResponse) throws Exception {
                        if (listResponse.code() == 200) {
                            searchView.initializeListView(listResponse.body());
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
}
