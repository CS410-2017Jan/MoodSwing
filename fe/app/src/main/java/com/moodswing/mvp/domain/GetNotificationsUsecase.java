package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.network.Repository;
import java.util.List;
import io.reactivex.Observable;

/**
 * Created by Kenny on 2017-03-25.
 */

public class GetNotificationsUsecase implements Usecase<List<JournalEntries>>{
    private Repository repository;
    private String token;

    public GetNotificationsUsecase(Repository repository) {
        this.repository = repository;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public Observable<List<JournalEntries>> execute() {
        return repository.getNotifications(this.token);
    }
}
