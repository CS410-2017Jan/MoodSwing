package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.network.Repository;


import io.reactivex.Observable;
import retrofit2.Response;

/**
 * Created by daniel on 23/03/17.
 */

public class GetUserUsecase implements Usecase<Response<User>> {
    private Repository repository;
    private String username;

    public GetUserUsecase(Repository repository) {this.repository = repository;}

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Observable<Response<User>> execute() {
        return repository.getUser(username);
    }
}
