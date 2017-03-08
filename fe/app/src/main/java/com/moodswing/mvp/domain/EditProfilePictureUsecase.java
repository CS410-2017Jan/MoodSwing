package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.ProfilePicture;
import com.moodswing.mvp.mvp.model.ProfilePictureResponse;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by Kenny on 2017-03-04.
 */

public class EditProfilePictureUsecase implements Usecase<ProfilePictureResponse> {
    private Repository repository;
    private String token;
    private ProfilePicture picture;

    public EditProfilePictureUsecase(Repository repository) {this.repository = repository;}

    public void setToken(String token) {
        this.token = token;
    }

    public void setPicture(ProfilePicture picture) {this.picture = picture;}

    @Override
    public Observable<ProfilePictureResponse> execute() {
        return repository.postProfilePicture(token, picture);
    }
}
