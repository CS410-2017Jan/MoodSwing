package com.moodswing.mvp.mvp.model.response;

/**
 * Created by Matthew on 2017-03-05.
 */

public class NewEntryResponse {
    private boolean success;
    private String message;

    public NewEntryResponse(boolean success, String message) {
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
