package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.response.SimpleResponse;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Matthew on 2017-03-04.
 */

public class NewEntryUsecase implements Usecase<SimpleResponse> {

    private Repository repository;
    private Capture capture;
    private String accessToken;
    private MultipartBody.Part data;
    private RequestBody entryText;
    private RequestBody entryDate;
    private RequestBody entryEmotion;

    public NewEntryUsecase(Repository repository) {this.repository = repository;}

    public void setCapture(Capture capture) {this.capture = capture;}

    public void setToken(String token){
        this.accessToken = token;
    }

    public void setText(RequestBody entryText){
        this.entryText = entryText;
    }

    public void setData(MultipartBody.Part data){
        this.data = data;
    }

    public void setDate(RequestBody entryDate){
        this.entryDate = entryDate;
    }

    public void setEmotion(RequestBody entryEmotion){
        this.entryEmotion = entryEmotion;
    }

    @Override
    public Observable<SimpleResponse> execute() {
        return repository.postNewEntry(accessToken, data, entryText, entryDate, entryEmotion);
    }
}
