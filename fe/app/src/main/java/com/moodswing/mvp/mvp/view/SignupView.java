package com.moodswing.mvp.mvp.view;

/**
 * Created by daniel on 18/02/17.
 */

public interface SignupView extends View {
    void onSignupSuccess();
    void onSignupFailure();
    void showError();
}
