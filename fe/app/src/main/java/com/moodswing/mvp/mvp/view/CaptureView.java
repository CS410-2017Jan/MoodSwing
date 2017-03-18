package com.moodswing.mvp.mvp.view;


import com.moodswing.mvp.mvp.model.User;

import java.util.List;

/**
 * Created by Matthew on 2017-03-12.
 */

public interface CaptureView extends View {
    void onGetUserInfoSuccess(List<User> users);
    void onGetUserInfoFailure();
    void showError();
}
