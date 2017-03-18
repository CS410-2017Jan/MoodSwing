package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.network.Repository;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

/**
 * Created by daniel on 16/03/17.
 */

public class SearchUsecase implements Usecase<Response<List<User>>> {
    private Repository repository;

    public SearchUsecase(Repository repository) {this.repository = repository;}

    @Override
    public Observable<Response<List<User>>> execute() {
        return repository.getUsers();
    }
}
