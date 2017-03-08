package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-05.
 */

public class Post {

    String text;
    String captureDate;

    public Post(String desc, String entryDate) {
        this.text = desc;
        this.captureDate = entryDate;
    }


    public String getDescription(){ return text; }

    public String getDate(){
        return captureDate;
    }

    public void setDescription(String newdesc){ text = newdesc; }

    public void setDate(String d){
        captureDate = d;
    }
}
