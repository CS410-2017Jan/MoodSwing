package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-07.
 */

public class Capture {

    String text;

    public Capture(){};

    public Capture(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public void seText(String t){
        text = t;
    }
}
