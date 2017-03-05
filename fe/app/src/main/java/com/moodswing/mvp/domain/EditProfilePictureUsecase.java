package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.ProfilePicture;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by Kenny on 2017-03-04.
 */

public class EditProfilePictureUsecase implements Usecase<ProfilePicture> {
    private Repository repository;

    public EditProfilePictureUsecase(Repository repository) {this.repository = repository;}


    @Override
    public Observable<ProfilePicture> execute() {
        return null;
    }
}
