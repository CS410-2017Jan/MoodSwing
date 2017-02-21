package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.GetJournalsUsecase;
import com.moodswing.mvp.mvp.presenter.JournalPresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by daniel on 20/02/17.
 */
@Module2
public class JournalModule {

    @PerActivity
    @Provides2
    public GetJournalsUsecase provideGetJournalsUsecase(Repository repository) {
        return new GetJournalsUsecase(repository);
    }


    @PerActivity
    @Provides2
    public JournalPresenter provideJournalPresenter(GetJournalsUsecase getJournalsUsecase) {return new JournalPresenter(getJournalsUsecase);}
}
