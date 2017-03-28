package com.moodswing.mvp.mvp.model;

import java.util.List;

/**
 * Created by Matthew on 2017-03-07.
 */

public class JournalEntries {

    String username;
    List<Capture> captures;
    String title;
    String entryDate;
    String _id;
    List<Comment> comments;
    String dateCreated;

    public JournalEntries(String username, List<Capture> captures, String title, String date, String _id, List<Comment> comments){
        this.username = username;
        this.captures = captures;
        this.title = title;
        this.entryDate = date;
        this._id = _id;
        this.comments = comments;
    }

    public List<Capture> getEntry(){
        return captures;
    }

    public String getDate(){
        return entryDate;
    }

    public String getTitle(){
        return title;
    }

    public String getUsername(){
        return username;
    }

    public String getId(){
        return _id;
    }

    public void setId(String id){
        _id = id;
    }

    public List<Comment> getComments(){ return comments; }

    //Notification dependencies
    public String getDateCreated() {
        return dateCreated;
    }

    public void setDate_id(String dateCreated) {
        this.dateCreated = dateCreated;
    }

}