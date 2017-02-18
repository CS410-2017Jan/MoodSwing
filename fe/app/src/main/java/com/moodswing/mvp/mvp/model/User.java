package com.moodswing.mvp.mvp.model;

/**
 * Created by daniel on 13/02/17.
 */

public class User {
    private String displayName;
    private String username;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
