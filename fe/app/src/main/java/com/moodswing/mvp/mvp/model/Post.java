package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-05.
 */

public class Post {

    String title;
//    String description;
    String date;
    String token;

    public Post(String title, String date, String token) {
        this.title = title;
//        this.description = desc;
        this.date = date;
        this.token = token;
    }

    public String getTitle(){
        return title;
    }

//    public String getDescription(){
//        return description;
//    }

    public String getDate(){
        return date;
    }

    public String getToken(){
        return token;
    }

    public void setTitle(String newtitle){
        title = newtitle;
    }

//    public void setDescription(String newdesc){
//        description = newdesc;
//    }

    public void setDate(String d){
        date = d;
    }
}
