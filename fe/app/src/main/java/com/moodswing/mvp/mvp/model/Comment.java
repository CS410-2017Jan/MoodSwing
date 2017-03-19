package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-18.
 */

public class Comment {
    private String commenter;
    private String text;
    private String _id;
    private String displayName;

    public Comment() {
    }

    public Comment(String text) {
        this.text = text;
    }

    public Comment(String name, String text) {
        this.text = text;
        this.displayName = name;
    }

    public Comment(String commenter, String text, String _id, String displayName) {
        this.commenter = commenter;
        this.text = text;
        this._id = _id;
        this.displayName = displayName;
    }

    public String getText() {
        return text;
    }

    public void setText(String t) {
        this.text = t;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setName(String n) {
        this.displayName = n;
    }
}
