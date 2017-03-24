package com.moodswing.mvp.mvp.view;

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
}
