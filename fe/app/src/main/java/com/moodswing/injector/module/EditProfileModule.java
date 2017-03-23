package com.moodswing.injector.module;

import com.moodswing.injector.scope.PerActivity;
import com.moodswing.mvp.domain.EditProfilePictureUsecase;
import com.moodswing.mvp.domain.GetProfilePictureUsecase;
import com.moodswing.mvp.domain.PutProfileUsecase;
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
    public EditProfilePictureUsecase provideEditProfilePictureUsecase(Repository repository) {
        return new EditProfilePictureUsecase(repository);
    }

    @PerActivity
    @Provides2
    public GetProfilePictureUsecase provideGetProfilePictureUsecase(Repository repository) {
        return new GetProfilePictureUsecase(repository);
    }

    @PerActivity
    @Provides2
    public PutProfileUsecase putProfileUsecase(Repository repository) {
        return new PutProfileUsecase(repository);
    }

    @Provides2
    @PerActivity
    public EditProfilePresenter provideEditProfilePresenter(EditProfilePictureUsecase editProfilePictureUsecase,
                                                            GetProfilePictureUsecase getProfilePictureUsecase,
                                                            PutProfileUsecase putProfileUsecase)
    {return new EditProfilePresenter(editProfilePictureUsecase, getProfilePictureUsecase, putProfileUsecase);}
}
