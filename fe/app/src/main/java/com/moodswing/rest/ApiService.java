package com.moodswing.rest;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.LoginRequest;
import com.moodswing.mvp.mvp.model.SignupRequest;
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.model.Text;
import com.moodswing.mvp.mvp.model.response.DeleteCaptureResponse;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.response.EditEntryResponse;
import com.moodswing.mvp.mvp.model.response.LoginResponse;
import com.moodswing.mvp.mvp.model.response.NewEntryResponse;
import com.moodswing.mvp.mvp.model.response.PostCommentResponse;
import com.moodswing.mvp.mvp.model.response.ProfilePictureResponse;
import com.moodswing.mvp.mvp.model.response.SetTitleResponse;
import com.moodswing.mvp.mvp.model.response.SignupResponse;
import com.moodswing.mvp.mvp.model.Title;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.widget.DateBlock;

import java.util.List;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
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
    Observable<SignupResponse> postSignupRequest(@Body SignupRequest signupRequest);

    @Headers("Content-Type: application/json")
    @POST("users/login")
    Observable<LoginResponse> postLogin(@Body LoginRequest loginRequest);

    @Headers("Content-Type: application/json")
    @POST("users/self/captures")
    Observable<NewEntryResponse> postNewEntry(@Body Capture capture,
                                              @Header("x-access-token") String accessToken);

    @Headers("Content-Type: application/json")
    @PUT("users/self/entries/{entryId}")
    Observable<SetTitleResponse> setTitle(@Header("x-access-token") String accessToken,
                                          @Path("entryId") String entryId,
                                          @Body Title title);

    @Headers("Content-Type: application/json")
    @PUT("users/self/captures/{captureId}")
    Observable<EditEntryResponse> editEntryText(@Header("x-access-token") String accessToken,
                                                @Path("captureId") String captureId,
                                                @Body Text text);

    @Headers("Content-Type: application/json")
    @POST("entries/{entryId}/comments")
    Observable<PostCommentResponse> postComment(@Header("x-access-token") String accessToken,
                                                @Path("entryId") String entryId,
                                                @Body Comment comment);

    @Headers("Content-Type: application/json")
    @DELETE("users/self/captures/{captureId}")
    Observable<DeleteCaptureResponse> deleteCapture(@Path("captureId") String captureId,
                                                    @Header("x-access-token") String accessToken);

    @Headers("Content-Type: application/json")
    @GET("users/{username}/entries")
    Observable<List<JournalEntries>> getJournalEntries(@Path("username") String username);

    @Headers("Content-Type: application/json")
    @GET("entries/{entryId}")
    Observable<DateBlock> getComments(@Path("entryId") String entryId);

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
//    Observable<PostCommentResponse> getCaptureData(@Body Capture capture,
//                                              @Header("x-access-token") String accessToken);

    @Headers("Content-Type: application/json")
    @GET("users")
    Observable<Response<List<User>>> getUsers();

}
