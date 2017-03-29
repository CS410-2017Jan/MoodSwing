package com.moodswing.mvp.network;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.ChangeProfileRequest;
import com.moodswing.mvp.mvp.model.LoginRequest;
import com.moodswing.mvp.mvp.model.SignupRequest;
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.model.Text;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.response.LoginResponse;
import com.moodswing.mvp.mvp.model.response.SignupResponse;
import com.moodswing.mvp.mvp.model.Title;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.model.response.SimpleResponse;
import com.moodswing.widget.DateBlock;

import java.util.List;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Part;

/**
 * Interface for API repository
 * Usecases should use this interface for fetching and uploading data to repository.
 * Created by daniel on 11/02/17.
 */

public interface Repository {
    Observable<SimpleResponse> postNewEntry(String accessToken, @Part MultipartBody.Part data, RequestBody entryText, RequestBody entryDate, RequestBody entryEmotion);
    Observable<SimpleResponse> postNewEntryNoPic(Capture capture, String accessToken);
    Observable<SimpleResponse> setTitle(String accessToken, String entryId, Title title);
    Observable<SimpleResponse> editEntryText(String accessToken, String id, Text text);
    Observable<SimpleResponse> deleteCapture(String _id, String accessToken);
    Observable<List<JournalEntries>> getJournalEntries(String username);
    Observable<DateBlock> getComments(String entryId);
    Observable<SignupResponse> postSignupRequest(SignupRequest signupRequest);
    Observable<LoginResponse> postLogin(LoginRequest loginRequest);
    Observable<SimpleResponse> postProfilePicture(String token, @Part MultipartBody.Part picture);
    Observable<ResponseBody> getProfilePicture(String username);
    Observable<ResponseBody> getEntryPic(String token, String captureId);
    Observable<ResponseBody> getEntryPicHighRes(String token, String captureId);
    Observable<Response<List<User>>> getUsers();
    Observable<SimpleResponse> postComment(String accessToken, String entryId, Comment comment);
    Observable<Response<SimpleResponse>> changeUser(String accessToken, ChangeProfileRequest changeProfileRequest);
    Observable<List<JournalEntries>> getNotifications(String accessToken);
    Observable<Response<User>> getUser(String username);
    Observable<Response<SimpleResponse>> follow(String accessToken, String username);
    Observable<Response<SimpleResponse>> unfollow(String accessToken, String username);
}
