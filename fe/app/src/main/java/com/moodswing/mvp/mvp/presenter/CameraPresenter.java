package com.moodswing.mvp.mvp.presenter;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.TemporaryCameraUsecase;
import com.moodswing.mvp.mvp.view.CameraView;

/**
 * Created by daniel on 04/03/17.
 */

public class CameraPresenter implements Presenter<CameraView> {
    private CameraView cameraView;
    private TemporaryCameraUsecase temporaryCameraUsecase;
    private SharedPreferencesManager sharedPreferencesManager;

    public CameraPresenter(TemporaryCameraUsecase temporaryCameraUsecase) {
        this.temporaryCameraUsecase = temporaryCameraUsecase;
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
    public void attachView(CameraView view) {
        this.cameraView = view;
    }

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }
}
