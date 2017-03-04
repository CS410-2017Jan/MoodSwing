package com.moodswing.mvp.mvp.model;

/**
 * Created by daniel on 13/02/17.
 */

public class User {
    private String displayName;
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String displayName, String username, String password) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
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

}
