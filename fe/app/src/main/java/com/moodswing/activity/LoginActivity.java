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
import com.moodswing.injector.component.DaggerLoginComponent;
import com.moodswing.injector.component.LoginComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.LoginModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.presenter.LoginPresenter;
import com.moodswing.mvp.mvp.view.LoginView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by daniel on 12/02/17.
 */

public class LoginActivity extends AppCompatActivity implements LoginView {

    @Inject
    LoginPresenter _loginPresenter;

    @Inject
    SharedPreferencesManager _sharedPreferencesManager;

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
        initializeUsernameText();
        initializeLoginButton();
        initializeSignupLink();
    }

    @Override
    protected void onStop() {
        super.onStop();
        _loginPresenter.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeUsernameText();
    }


    public void onLoginFailure() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    @Override
    public void showError() {
        Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_LONG).show();
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final int REQUEST_SIGNUP = 0;

        Toast.makeText(LoginActivity.this, R.string.request_login, Toast.LENGTH_LONG).show();

        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                String username = data.getStringExtra("USERNAME");
                _usernameText.setText(username);
                _passwordText.setText(null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the main activity
        moveTaskToBack(true);
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
        if (!valid()) {
            onLoginFailure();
            return;
        }
        _loginButton.setEnabled(false);

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        _loginPresenter.login(username, password);
    }


    private boolean valid() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError("Enter a valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _passwordText.setError("Password must be between 4 and 20 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }

    private void initializePresenter() {
        _loginPresenter.attachView(this);
        _loginPresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
    }

    private void initializeUsernameText() {
        if (_sharedPreferencesManager.getLastUser() != null) {
            _usernameText.setText(_sharedPreferencesManager.getLastUser());
            _passwordText.requestFocus();
        }
    }
}
