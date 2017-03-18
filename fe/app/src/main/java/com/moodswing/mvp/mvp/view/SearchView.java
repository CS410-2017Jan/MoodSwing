package com.moodswing.mvp.mvp.view;

import com.moodswing.mvp.mvp.model.journalobjects.User;

import java.util.List;

/**
 * Created by daniel on 16/03/17.
 */

public interface SearchView extends View{
    void showError(String error);
    void initializeListView(List<User> users);
}
