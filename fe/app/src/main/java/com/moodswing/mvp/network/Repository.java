package com.moodswing.mvp.network;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.CaptureResponse;
import com.moodswing.mvp.mvp.model.DeleteCaptureResponse;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.NewEntryResponse;
import com.moodswing.mvp.mvp.model.SignupResponse;
import com.moodswing.mvp.mvp.model.ProfilePictureResponse;
import com.moodswing.mvp.mvp.model.User;
import java.util.List;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Part;

/**
 * Interface for API repository
 * Usecases should use this interface for fetching and uploading data to repository.
 * Created by daniel on 11/02/17.
 */

public interface Repository {
    Observable<NewEntryResponse> postNewEntry(Capture capture, String accessToken);
    Observable<DeleteCaptureResponse> deleteCapture(String _id, String accessToken);
    Observable<List<JournalEntries>> getJournalEntries(String username);
    Observable<SignupResponse> postUser(User user);
    Observable<LoginResponse> postLogin(User user);
    Observable<ProfilePictureResponse> postProfilePicture(String token, @Part MultipartBody.Part picture);
    Observable<ResponseBody> getProfilePicture(String token);
//    Observable<CaptureResponse> getCaptureData(Capture capture, String accessToken);
}
