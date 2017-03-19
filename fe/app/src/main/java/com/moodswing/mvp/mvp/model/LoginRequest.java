package com.moodswing.mvp.mvp.model;

/**
 * Created by daniel on 18/03/17.
 */

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
