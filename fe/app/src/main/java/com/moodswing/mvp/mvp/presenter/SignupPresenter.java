package com.moodswing.mvp.mvp.presenter;

import android.app.ProgressDialog;
import android.content.Context;

import com.moodswing.R;
import com.moodswing.mvp.domain.SignupUsecase;
import com.moodswing.mvp.mvp.model.response.SignupResponse;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.view.SignupView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by daniel on 18/02/17.
 */

public class SignupPresenter implements Presenter<SignupView> {
    private SignupView signupView;
    private SignupUsecase signupUsecase;
    private Disposable signupSubscription;

    public SignupPresenter(SignupUsecase signupUsecase) {
        this.signupUsecase = signupUsecase;
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
    public void attachView(SignupView signupView) {
        this.signupView = signupView;
    }

    public void signup(String displayName, String username, String password) {
        signupUsecase.setUser(new User(displayName, username, password));

        final ProgressDialog progressDialog = new ProgressDialog((Context) signupView, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        signupSubscription = signupUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SignupResponse>() {
                    @Override
                    public void accept(SignupResponse signupResponse) throws Exception {
                        progressDialog.dismiss();
                        if (signupResponse.isSuccessful()) {
                            signupView.onSignupSuccess();
                        } else {
                            signupView.onSignupFailure(signupResponse.getMessage());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        progressDialog.dismiss();
                        signupView.showError();
                    }
                });
    }
}
