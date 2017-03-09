package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-07.
 */

public class Capture {

    String text;
    String captureDate;

    public Capture(){};

    public Capture(String text, String capturedate){
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public String getDate(){
        return captureDate;
    }

    public void setText(String t){
        text = t;
    }

    public void setCaptureDate(String d){
        captureDate = d;
    }
}
