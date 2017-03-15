package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-07.
 */

public class Capture {

    String text;
    String captureDate;
    String _id;

    public Capture(){};

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

    public void setText(String t){
        text = t;
    }

    public void setCaptureDate(String d){
        captureDate = d;
    }

    public void setId(String id){
        _id = id;
    }
}
