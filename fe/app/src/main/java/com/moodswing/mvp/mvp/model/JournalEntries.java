package com.moodswing.mvp.mvp.model;

import java.util.List;

/**
 * Created by Matthew on 2017-03-07.
 */

public class JournalEntries {

    String username;
    List<Capture> captures;
    String title;
    String date;

    public JournalEntries(String username, List<Capture> captures, String title, String data){
        this.username = username;
        this.captures = captures;
        this.title = title;
        this.date = date;
    }

    public List<Capture> getEntry(){
        return captures;
    }
}