package com.moodswing.widget;

import android.content.Context;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.model.User;

import java.util.List;
import java.util.Locale;

/**
 * Created by daniel on 24/03/17.
 */

public class SearchViewAdapter extends SearchAdapter {
    private SharedPreferencesManager sharedPreferencesManager;

    public SearchViewAdapter(List<User> users, Context context) {
        super(users, context);
    }

    public SearchViewAdapter(List<User> users, Context context, SharedPreferencesManager sharedPreferencesManager) {
        super(users, context);
        this.sharedPreferencesManager = sharedPreferencesManager;
    }

    @Override
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        parkingList.clear();

        String currentUser = sharedPreferencesManager.getCurrentUser();

        for (User user : usersList)  {
            if (charText.length() != 0 && user.getUsername().
                    toLowerCase(Locale.getDefault()).contains(charText) &&
                    !user.getUsername().equals(currentUser)) {
                parkingList.add(user);
            } else if (charText.length() != 0 && user.getDisplayName().
                    toLowerCase(Locale.getDefault()).contains(charText) &&
                    !user.getUsername().equals(currentUser)) {
                parkingList.add(user);
            }
        }
        notifyDataSetChanged();
    }

}
