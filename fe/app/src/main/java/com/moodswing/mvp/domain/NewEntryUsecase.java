package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.NewEntry;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by Matthew on 2017-03-04.
 */

public class NewEntryUsecase implements Usecase<NewEntry> {

    private Repository repository;

    public NewEntryUsecase(Repository repository) {this.repository = repository;}

    @Override
    public Observable<NewEntry> execute() {
        // TODO: stubbed
        return null;
    }
}
