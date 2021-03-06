package com.moodswing.mvp.domain;

import io.reactivex.Observable;

import com.moodswing.mvp.mvp.model.response.SimpleResponse;
import com.moodswing.mvp.network.Repository;

/**
 * Created by Matthew on 2017-03-14.
 */

public class DeleteCaptureUsecase implements Usecase<SimpleResponse> {
    private Repository repository;
    private String _id;
    private String accessToken;

    public DeleteCaptureUsecase(Repository repository) {this.repository = repository;}

    public void setDeletionId(String _id) { this._id = _id; }

    public void setToken(String token){
        this.accessToken = token;
    }

    @Override
    public Observable<SimpleResponse> execute() {
        return repository.deleteCapture(_id, accessToken);
    }
}
