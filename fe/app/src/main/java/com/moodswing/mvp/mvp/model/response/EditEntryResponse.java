package com.moodswing.mvp.mvp.model.response;

/**
 * Created by Matthew on 2017-03-21.
 */

public class EditEntryResponse {
    private boolean success;
    private String message;

    public EditEntryResponse(boolean success, String message) {
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
