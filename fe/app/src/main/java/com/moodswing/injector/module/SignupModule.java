package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.SignupUsecase;
import com.moodswing.mvp.mvp.presenter.SignupPresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by daniel on 18/02/17.
 */

@Module2
public class SignupModule {

    @PerActivity
    @Provides2
    public SignupUsecase provideGetSignupUsecase(Repository repository) {
        return new SignupUsecase(repository);
    }

    @PerActivity
    @Provides2
    public SignupPresenter provideSignupPresenter(SignupUsecase signupUsecase) {return new SignupPresenter(signupUsecase);}
}
