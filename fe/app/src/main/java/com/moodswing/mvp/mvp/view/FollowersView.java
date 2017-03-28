package com.moodswing.mvp.mvp.view;

import com.moodswing.mvp.mvp.model.User;

import java.util.List;

/**
 * Created by daniel on 24/03/17.
 */

public interface FollowersView extends View {
    void showError(String error);
    void initializeListView(List<User> followers, List<String> follows);
}
