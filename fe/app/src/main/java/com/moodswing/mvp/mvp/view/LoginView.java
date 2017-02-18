package com.moodswing.mvp.mvp.view;

/**
 * Created by daniel on 12/02/17.
 */

public interface LoginView extends View {
    void onLoginSuccess();
    void onLoginFailure();
    void showError();
}
