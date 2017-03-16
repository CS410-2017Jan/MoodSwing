package com.moodswing.rest;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.CaptureResponse;
import com.moodswing.mvp.mvp.model.DeleteCaptureResponse;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.NewEntryResponse;
import com.moodswing.mvp.mvp.model.SetTitleResponse;
import com.moodswing.mvp.mvp.model.SignupResponse;
import com.moodswing.mvp.mvp.model.ProfilePictureResponse;
import com.moodswing.mvp.mvp.model.Title;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.network.Repository;


import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
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
    public Observable<SignupResponse> postUser(User user) {
        return apiService.postUser(user);
    }

    @Override
    public Observable<LoginResponse> postLogin(User user) {
        return apiService.postLogin(user);
    }

    @Override
    public Observable<SetTitleResponse> setTitle(String accessToken, String entryId, Title title){
        return apiService.setTitle(accessToken, entryId, title);
    };

    @Override
    public Observable<NewEntryResponse> postNewEntry(Capture capture, String accessToken) {
        return apiService.postNewEntry(capture, accessToken);
    }

    @Override
    public Observable<List<JournalEntries>> getJournalEntries(String username){
        return apiService.getJournalEntries(username);
    }

    @Override
    public Observable<ProfilePictureResponse> postProfilePicture(String token, MultipartBody.Part picture) {
        return apiService.postProfilePicture(token, picture);
    }

    @Override
    public Observable<ResponseBody> getProfilePicture(String token) {
        return apiService.getProfilePicture(token);
    }

    @Override
    public Observable<Response<List<User>>> getUsers() {
        return apiService.getUsers();
    }

    @Override
    public Observable<DeleteCaptureResponse> deleteCapture(String _id, String token) {
        return apiService.deleteCapture(_id, token);
    }

//    @Override
//    public Observable<CaptureResponse> getCaptureData(Capture capture, String accessToken) {
//        return apiService.getCaptureData(capture, accessToken);
//    }
}
