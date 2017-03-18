package com.moodswing.mvp.mvp.presenter;

import com.moodswing.mvp.data.SharedPreferencesManager;
//import com.moodswing.mvp.domain.CaptureUsecase;
import com.moodswing.mvp.domain.SearchUsecase;
import com.moodswing.mvp.mvp.view.CaptureView;
import com.moodswing.mvp.mvp.view.SearchView;
import com.moodswing.mvp.mvp.model.User;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by Matthew on 2017-03-12.
 */

public class CapturePresenter implements Presenter<CaptureView> {
    private CaptureView captureView;
//    private CaptureUsecase captureUsecase;
    private SearchUsecase searchUsecase;
    private Disposable captureSubscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public CapturePresenter(SearchUsecase searchUsecase) {
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
    public void attachView(CaptureView view) {
        this.captureView = view;
    }


    public void getUsers() {
        captureSubscription = searchUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<List<User>>>() {
                    @Override
                    public void accept(Response<List<User>> listResponse) throws Exception {
                        if (listResponse.code() == 200) {
                            captureView.onGetUserInfoSuccess(listResponse.body());
                        } else {
                            captureView.onGetUserInfoFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        captureView.showError();
                    }
                });
    }


    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }
}
