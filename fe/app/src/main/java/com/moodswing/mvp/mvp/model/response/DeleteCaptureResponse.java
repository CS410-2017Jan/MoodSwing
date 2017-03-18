package com.moodswing.mvp.mvp.model.response;

/**
 * Created by Matthew on 2017-03-14.
 */

public class DeleteCaptureResponse {
    private boolean success;
    private String message;

    public DeleteCaptureResponse(boolean success, String message) {
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
