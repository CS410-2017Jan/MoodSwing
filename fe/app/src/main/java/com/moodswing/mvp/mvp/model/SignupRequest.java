package com.moodswing.mvp.mvp.model;

/**
 * Created by daniel on 18/03/17.
 */

public class SignupRequest {
    private String displayname;
    private String username;
    private String password;

    public SignupRequest(String displayName, String username, String password) {
        this.username = username;
        this.password = password;
        this.displayname = displayName;
    }
}
