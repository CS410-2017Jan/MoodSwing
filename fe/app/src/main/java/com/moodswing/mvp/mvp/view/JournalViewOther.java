package com.moodswing.mvp.mvp.view;

import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.User;

import java.util.List;

import okhttp3.ResponseBody;



public interface JournalViewOther extends View {

    void showEntries(List<JournalEntries> je);
    void showEntryPic(ResponseBody picture, String captureId);
    void onDeletionSuccess();
    void onEntryFailure();
    void onDeletionFailure();
    void onSetTitleSuccess();
    void onSetTitleFailure();
    void onGetUserInfoSuccess(User users);
    void onGetUserInfoFailure();
    void getPicture(ResponseBody picture);
    void showError();
}