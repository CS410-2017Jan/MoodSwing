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

    public JournalEntries(String username, List<Capture> captures, String title, String date){
        this.username = username;
        this.captures = captures;
        this.title = title;
        this.entryDate = date;
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
}