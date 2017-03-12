package com.moodswing.mvp.domain;

import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by Kenny on 2017-03-11.
 */

public class GetProfilePictureUsecase implements Usecase<ResponseBody> {
    private Repository repository;
    private String token;

    public GetProfilePictureUsecase(Repository repository) {this.repository = repository;}

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public Observable<ResponseBody> execute() {
        return repository.getProfilePicture(token);
    }
}
