package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.NewEntryResponse;
import com.moodswing.mvp.mvp.model.Post;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by Matthew on 2017-03-04.
 */

public class NewEntryUsecase implements Usecase<NewEntryResponse> {

    private Repository repository;
    private Post post;
    private String accessToken;

    public NewEntryUsecase(Repository repository) {this.repository = repository;}

    public void setPost(Post post) {this.post = post;}

    public void setToken(String token){
        this.accessToken = token;
    }

    @Override
    public Observable<NewEntryResponse> execute() {
        return repository.postNewEntry(post, accessToken);
    }
}
