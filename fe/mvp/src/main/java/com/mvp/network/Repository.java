package com.mvp.network;

import com.mvp.mvp.model.ResponseWrapper;
import com.mvp.mvp.model.User;

import io.reactivex.Observable;

/**
 * Interface for API repository
 * Usecases should use this interface for fetching and uploading data to repository.
 * Created by daniel on 11/02/17.
 */

public interface Repository {
    // getJournalEntries etc...
    Observable<ResponseWrapper<User>> postUser(User user);
    Observable<ResponseWrapper<Boolean>> postLogin(User user); // TODO: Change Boolean...
}
