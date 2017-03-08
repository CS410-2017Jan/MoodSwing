package com.moodswing.rest;

import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.NewEntryResponse;
import com.moodswing.mvp.mvp.model.Post;
import com.moodswing.mvp.mvp.model.SignupResponse;
import com.moodswing.mvp.mvp.model.ProfilePicture;
import com.moodswing.mvp.mvp.model.ProfilePictureResponse;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.network.Repository;

import java.io.File;

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
    public Observable<SignupResponse> postUser(User user) {
        return apiService.postUser(user);
    }

    @Override
    public Observable<LoginResponse> postLogin(User user) {
        return apiService.postLogin(user);
    }

    @Override
    public Observable<ProfilePictureResponse> postProfilePicture(String token, ProfilePicture profilePicture) {
        return apiService.postProfilePicture(token, profilePicture.getPicture());
    }

    @Override
    public Observable<NewEntryResponse> postNewEntry(Post post, String accessToken) {
        return apiService.postNewEntry(post, accessToken);
    }
}
