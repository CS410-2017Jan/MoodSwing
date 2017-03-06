package com.moodswing.mvp.network;

import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.NewEntryResponse;
import com.moodswing.mvp.mvp.model.Post;
import com.moodswing.mvp.mvp.model.SignupResponse;
import com.moodswing.mvp.mvp.model.ProfilePicture;
import com.moodswing.mvp.mvp.model.ProfilePictureResponse;
import com.moodswing.mvp.mvp.model.User;

import io.reactivex.Observable;

/**
 * Interface for API repository
 * Usecases should use this interface for fetching and uploading data to repository.
 * Created by daniel on 11/02/17.
 */

public interface Repository {
    // getJournalEntries etc...
    Observable<NewEntryResponse> postNewEntry(Post post);
    Observable<SignupResponse> postUser(User user);
    Observable<LoginResponse> postLogin(User user);
    Observable<ProfilePictureResponse> postProfilePicture(ProfilePicture profilePicture);
}
