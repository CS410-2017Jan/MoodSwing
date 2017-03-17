package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.SetTitleResponse;
import com.moodswing.mvp.mvp.model.Title;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by Matthew on 2017-03-15.
 */

public class SetTitleUsecase implements Usecase<SetTitleResponse>{
    private Repository repository;
    private String entryId;
    private String accessToken;
    private Title title;

    public SetTitleUsecase(Repository repository) {this.repository = repository;}

    public void setDateId(String _id) { this.entryId = _id; }

    public void setTitle(Title title) { this.title = title; }

    public void setToken(String token){
        this.accessToken = token;
    }

    @Override
    public Observable<SetTitleResponse> execute() {
        return repository.setTitle(accessToken, entryId, title);
    }
}
