package com.moodswing.mvp.mvp.view;

import com.moodswing.mvp.mvp.model.User;

import okhttp3.ResponseBody;

/**
 * Created by Kenny on 2017-02-27.
 */

public interface EditProfileView {

    void getPicture(ResponseBody picture);
    void noPictureMessage();
    void displaySavedProfile();
    void displaySaveError(String message);
    void returnFromSavedProfile();
    void onGetUserInfoSuccess(User user);
    void onGetUserInfoFailure();
    void showError();
}
