package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.LoginRequest;
import com.moodswing.mvp.mvp.model.response.LoginResponse;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by daniel on 13/02/17.
 */

public class LoginUsecase implements Usecase<LoginResponse> {

    private Repository repository;
    private LoginRequest loginRequest;

    public LoginUsecase(Repository repository) {this.repository = repository;}

    public void setLoginRequest(LoginRequest loginRequest) {this.loginRequest = loginRequest;}

    @Override
    public Observable<LoginResponse> execute() {
        return repository.postLogin(loginRequest);
    }
}
