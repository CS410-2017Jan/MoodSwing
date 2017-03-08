package com.moodswing.rest;

import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.NewEntryResponse;
import com.moodswing.mvp.mvp.model.Post;
import com.moodswing.mvp.mvp.model.ProfilePictureResponse;
import com.moodswing.mvp.mvp.model.SignupResponse;
import com.moodswing.mvp.mvp.model.User;
import java.util.List;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

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

    @Headers("Content-Type: application/json")
    @GET("users/{username}/entries")
    Observable<List<JournalEntries>> getJournalEntries(@Path("username") String username);

    @Multipart
    @POST("users/self/picture")
    Observable<ProfilePictureResponse> postProfilePicture(@Header("x-access-token") String token,
                                                          @Part MultipartBody.Part picture);
}
