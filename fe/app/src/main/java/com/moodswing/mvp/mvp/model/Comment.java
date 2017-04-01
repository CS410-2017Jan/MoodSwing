package com.moodswing.mvp.mvp.model;

import android.graphics.Bitmap;

/**
 * Created by Matthew on 2017-03-18.
 */

public class Comment {
    private String commenter;
    private String text;
    private String _id;
    private String displayName;
    private Bitmap image;
    private Boolean hasImage = false;

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

    public String getId() {
        return _id;
    }

    public String getCommenter() {
        return commenter;
    }

    public Boolean getHasImage(){
        return hasImage;
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

    public void setHasImage(Boolean hasImage){
        this.hasImage = hasImage;
    }

    public void setImage(Bitmap image){
        this.image = image;
    }

    public Bitmap getImage(){
        return image;
    }
}
