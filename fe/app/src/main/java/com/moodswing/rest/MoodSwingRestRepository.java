package com.moodswing.rest;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.ChangeProfileRequest;
import com.moodswing.mvp.mvp.model.LoginRequest;
import com.moodswing.mvp.mvp.model.SignupRequest;
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.model.Text;
import com.moodswing.mvp.mvp.model.response.ChangeProfileResponse;
import com.moodswing.mvp.mvp.model.response.DeleteCaptureResponse;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.response.EditEntryResponse;
import com.moodswing.mvp.mvp.model.response.LoginResponse;
import com.moodswing.mvp.mvp.model.response.NewEntryResponse;
import com.moodswing.mvp.mvp.model.response.PostCommentResponse;
import com.moodswing.mvp.mvp.model.response.SetTitleResponse;
import com.moodswing.mvp.mvp.model.response.SignupResponse;
import com.moodswing.mvp.mvp.model.response.ProfilePictureResponse;
import com.moodswing.mvp.mvp.model.Title;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.network.Repository;
import com.moodswing.widget.DateBlock;


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
    public Observable<SignupResponse> postSignupRequest(SignupRequest signupRequest) {
        return apiService.postSignupRequest(signupRequest);
    }

    @Override
    public Observable<LoginResponse> postLogin(LoginRequest loginRequest) {
        return apiService.postLogin(loginRequest);
    }

    @Override
    public Observable<SetTitleResponse> setTitle(String accessToken, String entryId, Title title){
        return apiService.setTitle(accessToken, entryId, title);
    }

    @Override
    public Observable<EditEntryResponse> editEntryText(String accessToken, String id, Text text){
        return apiService.editEntryText(accessToken, id, text);
    }

    @Override
    public Observable<PostCommentResponse> postComment(String accessToken, String entryId, Comment comment){
        return apiService.postComment(accessToken, entryId, comment);
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
    public Observable<DateBlock> getComments(String entryId){
        return apiService.getComments(entryId);
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

    @Override
    public Observable<Response<ChangeProfileResponse>> changeUser(String accessToken, ChangeProfileRequest changeProfileRequest){
        return apiService.changeUser(accessToken, changeProfileRequest);
    }
//    @Override
//    public Observable<PostCommentResponse> getCaptureData(Capture capture, String accessToken) {
//        return apiService.getCaptureData(capture, accessToken);
//    }
}
