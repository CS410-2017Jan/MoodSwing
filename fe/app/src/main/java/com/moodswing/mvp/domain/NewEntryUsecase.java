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

    public NewEntryUsecase(Repository repository) {this.repository = repository;}

    @Override
    public Observable<NewEntryResponse> execute() {
        return repository.postNewEntry(post);
    }
}
