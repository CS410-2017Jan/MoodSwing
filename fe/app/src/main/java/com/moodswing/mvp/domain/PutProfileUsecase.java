package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.ChangeProfileRequest;
import com.moodswing.mvp.mvp.model.response.ChangeProfileResponse;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;
import retrofit2.Response;

/**
 * Created by Kenny on 2017-03-22.
 */

public class PutProfileUsecase implements Usecase<Response<ChangeProfileResponse>>{
    private Repository repository;
    private String accessToken;
    private ChangeProfileRequest changeProfileRequest;

    public PutProfileUsecase(Repository repository) {
        this.repository = repository;
    }

    public void seToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setChangeProfileRequest(ChangeProfileRequest request) {
        this.changeProfileRequest = request;
    }

    @Override
    public Observable<Response<ChangeProfileResponse>> execute() {
        return repository.changeUser(accessToken, changeProfileRequest);
    }
}
