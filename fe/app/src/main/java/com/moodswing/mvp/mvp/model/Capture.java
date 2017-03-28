package com.moodswing.mvp.mvp.model;

import android.graphics.Bitmap;

/**
 * Created by Matthew on 2017-03-07.
 */

public class Capture {

    private String displayName;
    private String text;
    private String captureDate;
    private String _id;
    private String notificationTitle;
    private String notificationUsername;
    private Bitmap image;
    private Boolean hasImage = false;

    public Capture(){}

    public Capture(String text, String captureDate){
        this.text = text;
        this.captureDate = captureDate;
    }

    public Capture(String text, String captureDate, String _id){
        this.text = text;
        this.captureDate = captureDate;
        this._id = _id;
    }

    public String getText(){
        return text;
    }

    public String getDate(){
        return captureDate;
    }

    public String getId(){
        return _id;
    }

    public Bitmap getImage(){
        return image;
    }

    public Boolean getHasImage(){
        return hasImage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getNotificationUsername() {
        return notificationUsername;
    }

    public void setText(String t){
        text = t;
    }

    public void setCaptureDate(String d){
        captureDate = d;
    }

    public void setId(String id){
        _id = id;
    }

    public void setImage(Bitmap image){
        this.image = image;
    }

    public void setHasImage(Boolean hasImage){
        this.hasImage = hasImage;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setNotifyTitle(String title) {
        this.notificationTitle = title;
    }

    public void setNotificationUsername(String username) {
        this.notificationUsername = username;
    }


}
