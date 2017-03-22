package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.Text;
import com.moodswing.mvp.mvp.model.response.EditEntryResponse;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by Matthew on 2017-03-21.
 */

public class EditEntryUsecase implements Usecase<EditEntryResponse> {

    private Repository repository;
    private Text text;
    private String id;
    private String accessToken;

    public EditEntryUsecase(Repository repository) {this.repository = repository;}

    public void setText(Text text){
        this.text = text;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setToken(String token){
        this.accessToken = token;
    }

    @Override
    public Observable<EditEntryResponse> execute() {
        return repository.editEntryText(accessToken, id, text);
    }
}
