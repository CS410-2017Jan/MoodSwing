package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.NewEntryUsecase;
import com.moodswing.mvp.mvp.presenter.NewEntryPresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by Matthew on 2017-03-04.
 */

@Module2
public class NewEntryModule {

    @PerActivity
    @Provides2
    public NewEntryUsecase provideNewEntryUsecase(Repository repository) {
        return new NewEntryUsecase(repository);
    }


    @PerActivity
    @Provides2
    public NewEntryPresenter provideNewEntryPresenter(NewEntryUsecase newEntryUsecase) {return new NewEntryPresenter(newEntryUsecase);}
}