package com.moodswing.mvp.domain;

import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by daniel on 04/03/17.
 */

public class TemporaryCameraUsecase implements Usecase<String> {
    private Repository repository;

    public TemporaryCameraUsecase(Repository repository) {this.repository = repository;}

    @Override
    public Observable<String> execute() {
        return null;
    } // TODO: temp

}
