package com.moodswing.mvp.mvp.model.response;

/**
 * Created by daniel on 17/02/17.
 */

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;

    public LoginResponse(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public boolean isSuccessful() {
        return success;
    }
}
