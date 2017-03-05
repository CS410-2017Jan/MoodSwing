package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.EditProfilePictureUsecase;
import com.moodswing.mvp.mvp.presenter.EditProfilePresenter;
import com.moodswing.mvp.network.Repository;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by Kenny on 2017-02-27.
 */

@Module2
public class EditProfileModule {

    @PerActivity
    @Provides2
    public EditProfilePictureUsecase provideGetLoginUsecase(Repository repository) {
        return new EditProfilePictureUsecase(repository);
    }


    @Provides2
    @PerActivity
    public EditProfilePresenter provideEditProfilePresenter(EditProfilePictureUsecase editProfilePictureUsecase)
    {return new EditProfilePresenter(editProfilePictureUsecase);}
}
