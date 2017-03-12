package com.moodswing.rest;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.NewEntryResponse;
import com.moodswing.mvp.mvp.model.SignupResponse;
import com.moodswing.mvp.mvp.model.ProfilePictureResponse;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.network.Repository;


import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
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
}
