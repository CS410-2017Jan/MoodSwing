package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.mvp.domain.LoginUsecase;
import com.mvp.mvp.presenter.LoginPresenter;
import com.mvp.network.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by daniel on 12/02/17.
 */

@Module
public class LoginModule {

    @PerActivity
    @Provides
    public LoginUsecase provideGetLoginUsecase(Repository repository) {
        return new LoginUsecase(repository);
    }

    @PerActivity
    @Provides
    public LoginPresenter provideLoginPresenter(LoginUsecase loginUsecase) {return new LoginPresenter(loginUsecase);}
}
