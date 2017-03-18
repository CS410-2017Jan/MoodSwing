package com.moodswing.mvp.mvp.model.response;

/**
 * Created by daniel on 04/03/17.
 */

public class SignupResponse {
    private boolean success;
    private String message;
    private String token;

    public SignupResponse(boolean success, String token, String message) {
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
