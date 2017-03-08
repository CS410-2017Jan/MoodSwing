package com.moodswing.mvp.domain;


import com.moodswing.mvp.mvp.model.GetEntriesResponse;
import com.moodswing.mvp.mvp.model.Post;
import com.moodswing.mvp.network.Repository;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by daniel on 20/02/17.
 */

public class GetJournalsUsecase implements Usecase<GetEntriesResponse> {

    private Repository repository;
    private String username;

    public GetJournalsUsecase(Repository repository) {this.repository = repository;}

    public void setUsername(String username) {this.username = username;}

    @Override
    public Observable<GetEntriesResponse> execute() {
        return repository.getJournalEntries(username);
    }
}
