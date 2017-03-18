package com.moodswing.mvp.mvp.model;

/**
 * Created by daniel on 13/02/17.
 */

public class User {
    private String displayName;
    private String username;
    private String password;
    private String _id;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String _id, String username, String displayName) {
        this._id = _id;
        this.username = username;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }
}
