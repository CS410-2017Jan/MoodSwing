package com.moodswing.mvp.mvp.model;

import java.util.List;

/**
 * Created by daniel on 13/02/17.
 */

public class User {
    private String displayName;
    private String username;
    //
    private String _id;
    private List<String> followers;
    private List<String> following;

    public User(String username) {
        this.username = username;
    }

    public User(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }

    public User(String displayName, String username, String _id, List<String> followers, List<String> following) {
        this.displayName = displayName;
        this.username = username;
        this._id = _id;
        this.followers = followers;
        this.following = following;
    }

    public String get_id() {
        return _id;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }
}
