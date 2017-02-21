package com.moodswing.rest;

import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.network.Repository;

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
    public Observable<User> postUser(User user) {
        return apiService.postUser(user);
    }

    @Override
    public Observable<LoginResponse> postLogin(User user) {
        return apiService.postLogin(user);
    }
}
