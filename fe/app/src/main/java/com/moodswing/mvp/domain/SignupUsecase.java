package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by daniel on 18/02/17.
 */

public class SignupUsecase implements Usecase<User> {
    private Repository repository;
    private User user;

    public SignupUsecase(Repository repository) {this.repository = repository;}

    public void setUser(User user) {this.user = user;}

    @Override
    public Observable<User> execute() {
        return repository.postUser(user);
    }
}