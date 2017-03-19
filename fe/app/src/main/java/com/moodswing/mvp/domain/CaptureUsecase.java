package com.moodswing.mvp.domain;

import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.model.response.PostCommentResponse;
import com.moodswing.mvp.network.Repository;

import io.reactivex.Observable;

/**
 * Created by Matthew on 2017-03-12.
 */

public class CaptureUsecase implements Usecase<PostCommentResponse>{

    private Repository repository;
    private String entryId;
    private String accessToken;
    private Comment comment;

    public CaptureUsecase(Repository repository) {this.repository = repository;}

    public void setDateId(String _id) { this.entryId = _id; }

    public void setComment(Comment comment) { this.comment = comment; }

    public void setToken(String token){
        this.accessToken = token;
    }

    @Override
    public Observable<PostCommentResponse> execute() {
        return repository.postComment(accessToken, entryId, comment);
    }

}
