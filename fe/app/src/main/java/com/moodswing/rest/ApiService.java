package com.moodswing.rest;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.CaptureResponse;
import com.moodswing.mvp.mvp.model.DeleteCaptureResponse;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.NewEntryResponse;
import com.moodswing.mvp.mvp.model.ProfilePictureResponse;
import com.moodswing.mvp.mvp.model.SetTitleResponse;
import com.moodswing.mvp.mvp.model.SignupResponse;
import com.moodswing.mvp.mvp.model.User;
import java.util.List;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
    Observable<NewEntryResponse> postNewEntry(@Body Capture capture,
                                              @Header("x-access-token") String accessToken);

    @Headers("Content-Type: application/json")
    @PUT("users/self/entries/{entryId}")
    Observable<SetTitleResponse> setTitle(@Header("x-access-token") String accessToken,
                                          @Path("entryId") String entryId,
                                          @Body String title);

    @Headers("Content-Type: application/json")
    @DELETE("users/self/captures/{captureId}")
    Observable<DeleteCaptureResponse> deleteCapture(@Path("captureId") String captureId,
                                                    @Header("x-access-token") String accessToken);

    @Headers("Content-Type: application/json")
    @GET("users/{username}/entries")
    Observable<List<JournalEntries>> getJournalEntries(@Path("username") String username);

    @Multipart
    @POST("users/self/picture")
    Observable<ProfilePictureResponse> postProfilePicture(@Header("x-access-token") String token,
                                                          @Part MultipartBody.Part picture);

    @GET("users/self/picture")
    Observable<ResponseBody> getProfilePicture(@Header("x-access-token") String token);

    /*
    @PUT("users/self")
    Observable<ChangeUsernameResponse>  changeUsername(@Header("x-access-token") String token,
                                                       String )

    @PUT("users/self")
    Observable
    */

//    @Headers("Content-Type: application/json")
//    @POST("users/self/captures")
//    Observable<CaptureResponse> getCaptureData(@Body Capture capture,
//                                              @Header("x-access-token") String accessToken);

}
