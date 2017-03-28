package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.response.FollowResponse;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;
import retrofit2.Response;

/**
 * Created by daniel on 26/03/17.
 */

public class UnfollowUsecase implements Usecase<Response<FollowResponse>> {
    private Repository repository;
    private String accessToken;
    private String username;

    public UnfollowUsecase(Repository repository) {this.repository = repository;}

    @Override
    public Observable<Response<FollowResponse>> execute() {
        return repository.unfollow(accessToken, username);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
