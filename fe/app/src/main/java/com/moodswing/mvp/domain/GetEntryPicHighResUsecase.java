package com.moodswing.mvp.domain;

import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by Matthew on 2017-03-24.
 */

public class GetEntryPicHighResUsecase implements Usecase<ResponseBody> {

    private Repository repository;
    private String token;
    private String captureId;

    public GetEntryPicHighResUsecase(Repository repository) {this.repository = repository;}

    public void setToken(String token) {
        this.token = token;
    }

    public void setCaptureId(String captureId) {
        this.captureId = captureId;
    }

    @Override
    public Observable<ResponseBody> execute() {
        return repository.getEntryPicHighRes(token, captureId);
    }
}
