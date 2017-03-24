package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.GetUserUsecase;
import com.moodswing.mvp.mvp.presenter.FollowingPresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by daniel on 23/03/17.
 */

@Module2
public class FollowingModule {

    @PerActivity
    @Provides2
    public GetUserUsecase provideGetFollowingUsecase(Repository repository) {
        return new GetUserUsecase(repository);
    }

    @PerActivity
    @Provides2
    public FollowingPresenter provideFollowingPresenter(GetUserUsecase getUserUsecase) {return new FollowingPresenter(getUserUsecase);}
}
