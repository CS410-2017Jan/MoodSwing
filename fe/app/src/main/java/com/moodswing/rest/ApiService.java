package com.moodswing.rest;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.NewEntryResponse;
import com.moodswing.mvp.mvp.model.Post;
import com.moodswing.mvp.mvp.model.ProfilePicture;
import com.moodswing.mvp.mvp.model.ProfilePictureResponse;
import com.moodswing.mvp.mvp.model.SignupResponse;
import com.moodswing.mvp.mvp.model.User;

import java.io.File;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by daniel on 13/02/17.
 */

public interface ApiService {



    @Headers("Content-Type: application/json")
    @POST("users")
    Observable<SignupResponse> postUser(@Body User user);

    @Headers("Content-Type: application/json")
    @POST("users/login")
    Observable<LoginResponse> postLogin(@Body User user);

    @Headers("Content-Type: application/json")
    @POST("users/self/captures")
    Observable<NewEntryResponse> postNewEntry(@Body Post post,
                                              @Header("x-access-token") String accessToken);


    @Multipart
    @POST("users/self/picture")
    Observable<ProfilePictureResponse> postProfilePicture(@Header("x-access-token") String token,
                                                          @Part MultipartBody.Part picture);
}
