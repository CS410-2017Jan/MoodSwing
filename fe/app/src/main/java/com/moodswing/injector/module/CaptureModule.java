package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
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
    public SearchUsecase provideSearchUsecase(Repository repository) {
        return new SearchUsecase(repository);
    }


    @PerActivity
    @Provides2
    public CapturePresenter provideCapturePresenter(SearchUsecase searchUsecase) {return new CapturePresenter(searchUsecase);}

}
