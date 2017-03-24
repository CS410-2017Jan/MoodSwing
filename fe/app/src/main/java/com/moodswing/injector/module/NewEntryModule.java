package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.GetJournalsUsecase;
import com.moodswing.mvp.domain.NewEntryNoPicUsecase;
import com.moodswing.mvp.domain.NewEntryUsecase;
import com.moodswing.mvp.domain.SetTitleUsecase;
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
    public SetTitleUsecase provideSetTitleUsecase(Repository repository) {
        return new SetTitleUsecase(repository);
    }

    @PerActivity
    @Provides2
    public GetJournalsUsecase provideGetJournalsUsecase(Repository repository) {
        return new GetJournalsUsecase(repository);
    }

    @PerActivity
    @Provides2
    public NewEntryNoPicUsecase provideNewEntryNoPicUsecase(Repository repository) {
        return new NewEntryNoPicUsecase(repository);
    }


    @PerActivity
    @Provides2
    public NewEntryPresenter provideNewEntryPresenter(NewEntryUsecase newEntryUsecase, SetTitleUsecase setTitleUsecase, GetJournalsUsecase getJournalsUsecase, NewEntryNoPicUsecase newEntryNoPicUsecase) {
        return new NewEntryPresenter(newEntryUsecase, setTitleUsecase, getJournalsUsecase, newEntryNoPicUsecase);
    }
}
