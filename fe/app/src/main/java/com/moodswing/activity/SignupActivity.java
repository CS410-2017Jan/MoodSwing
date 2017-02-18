package com.moodswing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerSignupComponent;
import com.moodswing.injector.component.SignupComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.SignupModule;
import com.moodswing.mvp.mvp.presenter.SignupPresenter;
import com.moodswing.mvp.mvp.view.SignupView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by daniel on 12/02/17.
 */

public class SignupActivity extends AppCompatActivity implements SignupView {
    @Inject
    SignupPresenter _signupPresenter;

    @BindView(R.id.signup_name)
    EditText _nameText;

    @BindView(R.id.signup_username)
    EditText _usernameText;

    @BindView(R.id.signup_password)
    EditText _passwordText;

    @BindView(R.id.signup_confirm_password)
    EditText _confirmPasswordText;

    @BindView(R.id.btn_signup)
    Button _signupButton;

    @BindView(R.id.link_login)
    TextView _loginLink;

    private SignupComponent _signupComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        _signupComponent = DaggerSignupComponent.builder()
                .signupModule(new SignupModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();
        _signupComponent.inject(this);

        initializePresenter();
        initializeSignupButton();
        initializeLoginLink();
    }

    @Override
    public void onSignupSuccess() {
        _signupButton.setEnabled(true);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("USERNAME", _usernameText.getText().toString());
        setResult(RESULT_OK, resultIntent);

        finish();
    }

    @Override
    public void onSignupFailure() {
        Toast.makeText(SignupActivity.this, R.string.signup_failure, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    @Override
    public void showError() {
        Toast.makeText(SignupActivity.this, R.string.signup_error, Toast.LENGTH_LONG).show();
    }

    private void initializeSignupButton() {
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    private void signup() {
        String name = _nameText.getText().toString();
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmPassword = _confirmPasswordText.getText().toString();

        if (!valid(username, password, confirmPassword)) {
            onSignupFailure();
            return;
        }

        _signupButton.setEnabled(false);
        _signupPresenter.signup(username, password); // TODO: Not actually using name
    }

    private boolean valid(String username, String password, String confirmPassword) {
        boolean valid = true;

        if (username.isEmpty()) {
            _usernameText.setError("Enter a valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _passwordText.setError("Password must be between 4 and 20 characters");
            valid = false;
        } else if (!password.equals(confirmPassword)) {
            _confirmPasswordText.setError("Passwords must match");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }

    private void initializePresenter() {
        _signupPresenter.attachView(this);
    }

    private void initializeLoginLink() {
        _loginLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
