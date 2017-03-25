package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.CaptureUsecase;
import com.moodswing.mvp.domain.GetCommentsUsecase;
import com.moodswing.mvp.domain.GetEntryPicHighResUsecase;
import com.moodswing.mvp.domain.GetEntryPicUsecase;
import com.moodswing.mvp.domain.SearchUsecase;
import com.moodswing.mvp.mvp.presenter.CapturePresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by Matthew on 2017-03-12.
 */

@Module2
public class CaptureModule {

    @PerActivity
    @Provides2
    public CaptureUsecase provideCaptureUsecase(Repository repository) {
        return new CaptureUsecase(repository);
    }

    @PerActivity
    @Provides2
    public GetCommentsUsecase provideGetCommentsUsecase(Repository repository) {
        return new GetCommentsUsecase(repository);
    }

    @PerActivity
    @Provides2
    public GetEntryPicHighResUsecase provideGetEntryPicHighResUsecase(Repository repository) {
        return new GetEntryPicHighResUsecase(repository);
    }

    @PerActivity
    @Provides2
    public CapturePresenter provideCapturePresenter(CaptureUsecase captureUsecase, GetCommentsUsecase getCommentsUsecase, GetEntryPicHighResUsecase getEntryPicHighResUsecase) {return new CapturePresenter(captureUsecase, getCommentsUsecase, getEntryPicHighResUsecase);}

}
