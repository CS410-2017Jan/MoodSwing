package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.SignupUsecase;
import com.moodswing.mvp.mvp.presenter.SignupPresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by daniel on 18/02/17.
 */

@Module
public class SignupModule {

    @PerActivity
    @Provides
    public SignupUsecase provideGetSignupUsecase(Repository repository) {
        return new SignupUsecase(repository);
    }

    @PerActivity
    @Provides
    public SignupPresenter provideSignupPresenter(SignupUsecase signupUsecase) {return new SignupPresenter(signupUsecase);}
}
