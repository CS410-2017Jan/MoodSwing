package com.moodswing.mvp.mvp.view;

import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.User;

import java.util.List;

/**
 * Created by daniel on 11/02/17.
 */

public interface JournalView extends View {

    void showEntries(List<JournalEntries> je);
    void onDeletionSuccess();
    void onEntryFailure();
    void onDeletionFailure();
    void onSetTitleSuccess();
    void onSetTitleFailure();
    void onGetUserInfoSuccess(List<User> users);
    void onGetUserInfoFailure();
    void showError();
}
