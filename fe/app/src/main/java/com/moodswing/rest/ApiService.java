package com.moodswing.rest;

import com.mvp.mvp.model.ResponseWrapper;
import com.mvp.mvp.model.User;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by daniel on 13/02/17.
 */

public interface ApiService {

    @POST("public/users") // TODO is this right?
    Observable<ResponseWrapper<User>> postUser(@Body User user);

    @POST("public/users/login") // TODO is this right?
    Observable<ResponseWrapper<Boolean>> postLogin(@Body User user); // TODO not Boolean... LoginResponse etc...
}
