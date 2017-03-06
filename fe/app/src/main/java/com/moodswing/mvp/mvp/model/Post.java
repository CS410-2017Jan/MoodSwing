package com.moodswing.mvp.mvp.model;

/**
 * Created by Matthew on 2017-03-05.
 */

public class Post {

    String title;
    String description;

    public Post(String title, String desc) {
        this.title = title;
        this.description = desc;
    }



    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public void setTitle(String newtitle){
        title = newtitle;
    }

    public void setDescription(String newdesc){
        description = newdesc;
    }
}
