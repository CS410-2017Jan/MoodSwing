package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-10.
 */

public class DateBlock {

    String title;
    String date;

    public DateBlock(){};

    public DateBlock(String title, String date){
        this.title = title;
        this.date = date;
    }

    public String getTitle(){
        return title;
    }

    public String getDate(){
        return date;
    }

    public void setTitle(String t){
        title = t;
    }

    public void setDate(String d){
        date = d;
    }
}
