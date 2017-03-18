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
    private Disposable captureSubscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public CapturePresenter() {

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


    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }
}
