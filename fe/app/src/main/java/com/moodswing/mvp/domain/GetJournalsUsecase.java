package com.moodswing.mvp.domain;


import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.network.Repository;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by daniel on 20/02/17.
 */

public class GetJournalsUsecase implements Usecase<List<JournalEntries>> {

    private Repository repository;
    private String username;

    public GetJournalsUsecase(Repository repository) {this.repository = repository;}

    public void setUsername(String username) {this.username = username;}

    @Override
    public Observable<List<JournalEntries>> execute() {
        return repository.getJournalEntries(username);
    }
}
