package com.moodswing.rest;

import com.mvp.mvp.model.ResponseWrapper;
import com.mvp.mvp.model.User;
import com.mvp.network.Repository;

import io.reactivex.Observable;
import retrofit2.Retrofit;

/**
 * Created by daniel on 12/02/17.
 */

public class MoodSwingRestRepository implements Repository {
    private ApiService apiService;

    public MoodSwingRestRepository(Retrofit retrofit) {
        apiService = retrofit.create(ApiService.class);
    }

    @Override
    public Observable<ResponseWrapper<User>> postUser(User user) {
        return apiService.postUser(user);
    }

    @Override
    public Observable<ResponseWrapper<Boolean>> postLogin(User user) {
        return apiService.postLogin(user);
    }
}
