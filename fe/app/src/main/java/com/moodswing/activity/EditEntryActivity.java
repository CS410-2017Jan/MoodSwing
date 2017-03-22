package com.moodswing.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moodswing.MoodSwingApplication;
import com.moodswing.R;
import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerEditEntryComponent;
import com.moodswing.injector.component.DaggerNewEntryComponent;
import com.moodswing.injector.component.EditEntryComponent;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.EditEntryModule;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.mvp.presenter.EditEntryPresenter;
import com.moodswing.mvp.mvp.view.EditEntryView;

import javax.inject.Inject2;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Matthew on 2017-03-21.
 */

public class EditEntryActivity extends MoodSwingActivity implements EditEntryView {

    @Inject2
    EditEntryPresenter _editEntryPresenter;

    @Inject2
    SharedPreferencesManager _sharedPreferencesManager;

    @BindView(R.id.btn_save)
    Button _saveButton;

    @BindView(R.id.edit_entry_desc)
    EditText _editDescText;

    private EditEntryComponent _editEntryComponent;
    String text;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editentry);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        ApplicationComponent applicationComponent = ((MoodSwingApplication) getApplication()).getApplicationComponent();

        _editEntryComponent = DaggerEditEntryComponent.builder()
                .editEntryModule(new EditEntryModule())
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build();
        _editEntryComponent.inject(this);

        initializeShareButton();
        initializePresenter();
        initializeBottomNavigationView();
        text = getIntent().getStringExtra("TEXT");
        id = getIntent().getStringExtra("ID");

        _editDescText.setText(text);
        _editDescText.setSelection(_editDescText.getText().length());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onPause(){
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),0);
    }

    @Override
    public void onEditEntrySuccess() {
        exitToJounal();
    }

    @Override
    public void onEditEntryFailure() {
        Toast.makeText(getBaseContext(), "Failure changing entries", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError() {
        Toast.makeText(EditEntryActivity.this, "Error changing entry", Toast.LENGTH_LONG).show();
    }

    private void initializeShareButton() {
        _saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String description = _editDescText.getText().toString();
                if(!isEmpty(description)){
                    saveEntry(description);
                }
                else{
                    displayError();
                }
            }
        });
    }

    private void saveEntry(String description) {
        _editEntryPresenter.setText(description, id);
    }

    private void displayError() {
        AlertDialog alertDialog = new AlertDialog.Builder(EditEntryActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("The description cannot have an empty value.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        displayWarning("Are you sure you want to go back? Any changes will be discarded.");
    }

    private void displayWarning(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditEntryActivity.this);
        builder.setTitle("Warning");
        builder.setMessage(s);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                exitToJounal();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void exitToJounal(){
        Intent intent = new Intent(getApplicationContext(), JournalActivity.class);
        startActivity(intent);
    }

    private void initializePresenter() {
        _editEntryPresenter.attachView(this);
        _editEntryPresenter.attachSharedPreferencesManager(_sharedPreferencesManager);
    }
}
