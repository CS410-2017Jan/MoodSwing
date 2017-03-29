package com.moodswing.mvp.mvp.model.response;

/**
 * Created by Matthew on 2017-03-28.
 */

public class SimpleResponse {
    private boolean success;
    private String message;

    public SimpleResponse(boolean success, String message) {
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
