package com.mvp.mvp.presenter;

import com.mvp.domain.LoginUsecase;
import com.mvp.mvp.model.User;
import com.mvp.mvp.view.LoginView;

/**
 * Created by daniel on 12/02/17.
 */

public class LoginPresenter implements Presenter<LoginView> {
    private LoginView loginView;
    private LoginUsecase loginUsecase;

    public LoginPresenter(LoginUsecase loginUsecase) {
        this.loginUsecase = loginUsecase;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void attachView(LoginView view) {
        this.loginView = view;
    }

    public void login(String username, String password) {
        loginUsecase.setUser(new User(username, password));
        if (username.equals("username") && password.equals("password"))
            loginView.onLoginSuccess();
        else
            loginView.onLoginFailure();
        // return loginUsecase.execute().... // TODO: ... call loginView.dosomething() which will be implemented by loginactivity

    }
}
