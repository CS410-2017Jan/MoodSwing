package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by daniel on 13/02/17.
 */

public class LoginUsecase implements Usecase<LoginResponse> {

    private Repository repository;
    private User user;

    public LoginUsecase(Repository repository) {this.repository = repository;}

    public void setUser(User user) {this.user = user;}

    @Override
    public Observable<LoginResponse> execute() {
        return repository.postLogin(user);
    }
}
