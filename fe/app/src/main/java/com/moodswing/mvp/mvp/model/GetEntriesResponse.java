package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-07.
 */

public class GetEntriesResponse {

    private boolean success;
    private String message;

    public GetEntriesResponse(boolean success, String message) {
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

