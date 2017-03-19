package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.SignupRequest;
import com.moodswing.mvp.mvp.model.response.SignupResponse;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by daniel on 18/02/17.
 */

public class SignupUsecase implements Usecase<SignupResponse> {
    private Repository repository;
    private SignupRequest signupRequest;

    public SignupUsecase(Repository repository) {this.repository = repository;}

    public void setSignupRequest(SignupRequest signupRequest) {this.signupRequest = signupRequest;}

    @Override
    public Observable<SignupResponse> execute() {
        return repository.postSignupRequest(signupRequest);
    }
}
