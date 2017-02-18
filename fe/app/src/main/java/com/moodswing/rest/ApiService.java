package com.moodswing.rest;

import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.User;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by daniel on 13/02/17.
 */

public interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("public/users")
    Observable<User> postUser(@Body User user);

    @Headers("Content-Type: application/json")
    @POST("public/users/login")
    Observable<LoginResponse> postLogin(@Body User user);
}
