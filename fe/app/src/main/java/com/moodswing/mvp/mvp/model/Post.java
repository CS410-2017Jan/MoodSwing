package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-05.
 */

public class Post {

    String text;
    String entryDate;

    public Post(String desc, String entryDate) {
        this.text = desc;
        this.entryDate = entryDate;
    }


    public String getDescription(){ return text; }

    public String getDate(){
        return entryDate;
    }

    public void setDescription(String newdesc){ text = newdesc; }

    public void setDate(String d){
        entryDate = d;
    }
}
