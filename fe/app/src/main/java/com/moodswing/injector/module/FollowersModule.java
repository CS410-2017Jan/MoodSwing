package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.GetUserUsecase;
import com.moodswing.mvp.mvp.presenter.FollowersPresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by daniel on 24/03/17.
 */

@Module2
public class FollowersModule {
    @PerActivity
    @Provides2
    public GetUserUsecase provideGetFollowersUsecase(Repository repository) {
        return new GetUserUsecase(repository);
    }

    @PerActivity
    @Provides2
    public FollowersPresenter provideFollowersPresenter(GetUserUsecase getUserUsecase) {return new FollowersPresenter(getUserUsecase);}
}
