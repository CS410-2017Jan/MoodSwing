package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.EditEntryUsecase;
import com.moodswing.mvp.mvp.presenter.EditEntryPresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by Matthew on 2017-03-21.
 */

@Module2
public class EditEntryModule {

    @PerActivity
    @Provides2
    public EditEntryUsecase provideEditEntryUsecase(Repository repository) {
        return new EditEntryUsecase(repository);
    }

    @PerActivity
    @Provides2
    public EditEntryPresenter provideEditEntryPresenter(EditEntryUsecase editEntryUsecase) {
        return new EditEntryPresenter(editEntryUsecase);
    }
}
