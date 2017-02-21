package com.moodswing.mvp.mvp.model;

/**
 * Created by daniel on 13/02/17.
 */

public class User {
    private String displayName;
    private String username;
    private String password;
    private String token;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() { return token; }
}
