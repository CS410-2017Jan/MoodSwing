package com.moodswing.mvp.network;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.response.DeleteCaptureResponse;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.response.LoginResponse;
import com.moodswing.mvp.mvp.model.response.NewEntryResponse;
import com.moodswing.mvp.mvp.model.response.SetTitleResponse;
import com.moodswing.mvp.mvp.model.response.SignupResponse;
import com.moodswing.mvp.mvp.model.response.ProfilePictureResponse;
import com.moodswing.mvp.mvp.model.Title;
import com.moodswing.mvp.mvp.model.User;
import java.util.List;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Part;

/**
 * Interface for API repository
 * Usecases should use this interface for fetching and uploading data to repository.
 * Created by daniel on 11/02/17.
 */

public interface Repository {
    Observable<NewEntryResponse> postNewEntry(Capture capture, String accessToken);
    Observable<SetTitleResponse> setTitle(String accessToken, String entryId, Title title);
    Observable<DeleteCaptureResponse> deleteCapture(String _id, String accessToken);
    Observable<List<JournalEntries>> getJournalEntries(String username);
    Observable<SignupResponse> postUser(User user);
    Observable<LoginResponse> postLogin(User user);
    Observable<ProfilePictureResponse> postProfilePicture(String token, @Part MultipartBody.Part picture);
    Observable<ResponseBody> getProfilePicture(String token);
//    Observable<CaptureResponse> getCaptureData(Capture capture, String accessToken);
    Observable<Response<List<User>>> getUsers();
}
