package com.moodswing.mvp.mvp.model.response;

/**
 * Created by Matthew on 2017-03-12.
 */

public class PostCommentResponse {
    private boolean success;
    private String message;

    public PostCommentResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccessful() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
