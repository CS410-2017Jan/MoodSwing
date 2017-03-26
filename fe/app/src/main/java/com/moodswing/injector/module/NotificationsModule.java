package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.GetNotificationsUsecase;
import com.moodswing.mvp.mvp.presenter.NotificationsPresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by Kenny on 2017-03-25.
 */

@Module2
public class NotificationsModule {

    @PerActivity
    @Provides2
    public GetNotificationsUsecase provideGetNotificationsUsecase(Repository repository) {
        return new GetNotificationsUsecase(repository);
    }

    @Provides2
    @PerActivity
    public NotificationsPresenter provideNotificationsPresenter(GetNotificationsUsecase getNotificationsUsecase)
    {return new NotificationsPresenter(getNotificationsUsecase);}
}
