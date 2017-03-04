package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.TemporaryCameraUsecase;
import com.moodswing.mvp.mvp.presenter.CameraPresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by daniel on 04/03/17.
 */

@Module2
public class CameraModule {
    @PerActivity
    @Provides2
    public TemporaryCameraUsecase provideTemporaryCameraUsecase(Repository repository) {
        return new TemporaryCameraUsecase(repository);
    }

    @PerActivity
    @Provides2
    public CameraPresenter provideCameraPresenter(TemporaryCameraUsecase temporaryCameraUsecase) {return new CameraPresenter(temporaryCameraUsecase);}
}
