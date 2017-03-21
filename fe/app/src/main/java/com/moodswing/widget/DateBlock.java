package com.moodswing.widget;

import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.Comment;

import java.util.List;

/**
 * Created by Matthew on 2017-03-10.
 */

public class DateBlock {

    String title;
    String date;
    String _id;
    String username;
    List<Comment> comments;

    public DateBlock(){};

    public DateBlock(String title, String date, String _id, String username){
        this.title = title;
        this.date = date;
        this._id = _id;
        this.username = username;
    }

    public DateBlock(String title, String date, String _id, String username, List<Comment> comments){
        this.title = title;
        this.date = date;
        this._id = _id;
        this.username = username;
        this.comments = comments;
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

    public String getId(){
        return _id;
    }

    public void setId(String id){
        _id = id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String u){
        username = u;
    }

    public List<Comment> getComments(){
        return comments;
    }
}
