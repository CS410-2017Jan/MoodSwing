package com.moodswing.mvp.mvp.view;

import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.User;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Kenny on 2017-03-25.
 */

public interface NotificationsView extends View {
    void showEntries(List<JournalEntries> je);
    void showEntryPic(ResponseBody picture, String captureId);
    void showError();
    void onGetUserInfoSuccess(User users);
    void onGetUserInfoFailure();
}
