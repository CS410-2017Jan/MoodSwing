package com.mvp.domain;

import com.mvp.mvp.model.User;
import com.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by daniel on 13/02/17.
 */

public class LoginUsecase implements Usecase<Boolean> {

    private Repository repository;
    private User user;

    public LoginUsecase(Repository repository) {this.repository = repository;}

    public void setUser(User user) {this.user = user;}

    @Override
    public Observable<Boolean> execute() {
        // TODO
        //return repository.postLogin(user).map();
        return null;
    }
}
