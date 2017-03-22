package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-21.
 */

public class Text {
    String text;

    public Text(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }
}
