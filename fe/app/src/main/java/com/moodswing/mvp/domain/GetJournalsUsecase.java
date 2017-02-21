package com.moodswing.mvp.domain;


import com.moodswing.mvp.mvp.model.JournalEntry;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by daniel on 20/02/17.
 */

public class GetJournalsUsecase implements Usecase<JournalEntry> {
    private Repository repository;

    public GetJournalsUsecase(Repository repository) {this.repository = repository;}

    @Override
    public Observable<JournalEntry> execute() {
        // TODO: stubbed
        return null;
    }
}
