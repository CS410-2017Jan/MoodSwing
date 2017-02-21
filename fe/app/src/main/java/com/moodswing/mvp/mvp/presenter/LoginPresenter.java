package com.moodswing.mvp.mvp.presenter;

import android.app.ProgressDialog;
import android.content.Context;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.moodswing.R;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.LoginUsecase;
import com.moodswing.mvp.mvp.model.LoginResponse;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.view.LoginView;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by daniel on 12/02/17.
 */

public class LoginPresenter implements Presenter<LoginView> {
    private LoginView loginView;
    private LoginUsecase loginUsecase;
    private Disposable loginSubscription;
    private SharedPreferencesManager sharedPreferencesManager;

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

    public void login(final String username, String password) {
        loginUsecase.setUser(new User(username, password));

        final ProgressDialog progressDialog = new ProgressDialog((Context) loginView, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        loginSubscription = loginUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(LoginResponse loginResponse) throws Exception {
                        progressDialog.dismiss();
                        if (loginResponse.isSuccessful()) {
                            sharedPreferencesManager.login(username, loginResponse.getToken());
                            loginView.onLoginSuccess();
                        } else {
                            loginView.onLoginFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        progressDialog.dismiss();
                        loginView.showError();
                    }
                });
    }

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }
}
