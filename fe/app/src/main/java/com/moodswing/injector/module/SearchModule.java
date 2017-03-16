package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.SearchUsecase;
import com.moodswing.mvp.mvp.presenter.SearchPresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by daniel on 16/03/17.
 */

@Module2
public class SearchModule {

    @PerActivity
    @Provides2
    public SearchUsecase provideGetSearchUsecase(Repository repository) {
        return new SearchUsecase(repository);
    }

    @PerActivity
    @Provides2
    public SearchPresenter provideSearchPresenter(SearchUsecase searchUsecase) {return new SearchPresenter(searchUsecase);}
}
