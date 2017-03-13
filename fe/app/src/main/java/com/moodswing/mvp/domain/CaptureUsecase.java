package com.moodswing.mvp.domain;

import android.database.Observable;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.CaptureResponse;
import com.moodswing.mvp.network.Repository;

/**
 * Created by Matthew on 2017-03-12.
 */

//public class CaptureUsecase implements Usecase<CaptureResponse>{
//
//    private Repository repository;
//    private Capture capture;
//    private String accessToken;
//
//    public CaptureUsecase(Repository repository) {this.repository = repository;}
//
//    public void setCapture(Capture capture) {this.capture = capture;}
//
//    public void setToken(String token){ this.accessToken = token; }
//
//    @Override
//    public Observable<CaptureResponse> execute() {
//        return repository.getCaptureData(capture, accessToken);
//    }
//
//}
