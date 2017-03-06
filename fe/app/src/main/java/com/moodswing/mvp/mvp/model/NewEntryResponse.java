package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-05.
 */

public class NewEntryResponse {

    private boolean success;
    private String message;
    private String token;

    public NewEntryResponse(boolean success, String token, String message) {
        this.success = success;
        this.token = token;
        this.message = message;
    }

    public boolean isSuccessful() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
