package com.moodswing.mvp.domain;

import com.moodswing.mvp.network.Repository;
import com.moodswing.widget.DateBlock;

import io.reactivex.Observable;

/**
 * Created by Matthew on 2017-03-19.
 */

public class GetCommentsUsecase implements Usecase<DateBlock> {

    private Repository repository;
    private String entryId;

    public GetCommentsUsecase(Repository repository) {
        this.repository = repository;
    }

    public void setDateId(String _id) {
        this.entryId = _id;
    }

    @Override
    public Observable<DateBlock> execute() {
        return repository.getComments(entryId);
    }
}
