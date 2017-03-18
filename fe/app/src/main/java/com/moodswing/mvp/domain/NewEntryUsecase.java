package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.response.NewEntryResponse;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by Matthew on 2017-03-04.
 */

public class NewEntryUsecase implements Usecase<NewEntryResponse> {

    private Repository repository;
    private Capture capture;
    private String accessToken;

    public NewEntryUsecase(Repository repository) {this.repository = repository;}

    public void setCapture(Capture capture) {this.capture = capture;}

    public void setToken(String token){
        this.accessToken = token;
    }

    @Override
    public Observable<NewEntryResponse> execute() {
        return repository.postNewEntry(capture, accessToken);
    }
}
