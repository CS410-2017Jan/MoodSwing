package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.response.ProfilePictureResponse;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;
import okhttp3.MultipartBody;

/**
 * Created by Kenny on 2017-03-04.
 */

public class EditProfilePictureUsecase implements Usecase<ProfilePictureResponse> {
    private Repository repository;
    private String token;
    private MultipartBody.Part profilePicture;

    public EditProfilePictureUsecase(Repository repository) {this.repository = repository;}

    public void setToken(String token) {
        this.token = token;
    }

    public void setProfilePicture(MultipartBody.Part profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public Observable<ProfilePictureResponse> execute() {
        return repository.postProfilePicture(token, profilePicture);
    }
}
