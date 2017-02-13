package com.moodswing.activity;

import android.app.ProgressDialog;
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
import com.moodswing.injector.component.DaggerLoginComponent;
import com.moodswing.injector.component.LoginComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.LoginModule;
import com.mvp.mvp.presenter.LoginPresenter;
import com.mvp.mvp.view.LoginView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by daniel on 12/02/17.
 */

public class LoginActivity extends AppCompatActivity implements LoginView {

    @Inject
    LoginPresenter _loginPresenter;

    @BindView(R.id.input_username)
    EditText _usernameText;

    @BindView(R.id.input_password)
    EditText _passwordText;

    @BindView(R.id.btn_login)
    Button _loginButton;

    @BindView(R.id.link_signup)
    TextView _signupLink;

    // TODO: adapter...?
    private LoginComponent _loginComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        _loginComponent = DaggerLoginComponent.builder()
                .loginModule(new LoginModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();
        _loginComponent.inject(this);

        initializePresenter();
        initializeLoginButton();
        initializeSignupLink();
    }

    private void initializeSignupLink() {
        _signupLink.setOnClickListener(new View.OnClickListener() {
            public static final int REQUEST_SIGNUP = 0;

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    private void initializeLoginButton() {
        _loginButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        if (!validate()) {
             onLoginFailure();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        // authenticate
        // TODO
        _loginPresenter.login(username, password);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailure
                        // onLoginSuccess()
                        // TODO: Fix this
                        onLoginFailure();
                        progressDialog.dismiss();
                    }
                }, 3000);

    }

    public void onLoginFailure() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    private boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError("Enter a valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) { // TODO: Do we want something like this?
            _passwordText.setError("Password must be between 4 and 20 characters");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        // disable going back to the main activity
        moveTaskToBack(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        _loginPresenter.onStop();
    }

    private void initializePresenter() {
        _loginPresenter.attachView(this);
    }
}
