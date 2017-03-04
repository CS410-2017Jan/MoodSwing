package com.moodswing.mvp.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.affectiva.android.affdex.sdk.detector.CameraDetector;

/**
 * Created by daniel on 21/02/17.
 */

public class SharedPreferencesManager implements DataStore {
    private SharedPreferences sharedPreferences;
    private String PREF_NAME = "moodswing_preferences";
    private String CURRENT_USER = "current_user";
    private String CURRENT_USER_TOKEN = "current_user_token";
    private String LAST_USER = "last_user";
    private String LAST_CAMERA_TYPE = "last_camera_type";

    public SharedPreferencesManager(Application application) {
        sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void login(String username, String token) {
        sharedPreferences.edit().putString(CURRENT_USER, username).apply();
        sharedPreferences.edit().putString(CURRENT_USER_TOKEN, token).apply();
    }

    @Override
    public void logout(String username) {
        sharedPreferences.edit().remove(CURRENT_USER).apply();
        sharedPreferences.edit().remove(CURRENT_USER_TOKEN).apply();
        sharedPreferences.edit().putString(LAST_USER, username).apply();
    }

    @Override
    public boolean isUserLoggedIn() {
        return sharedPreferences.getString(CURRENT_USER, null) != null;
    }

    @Override
    public String getCurrentUser() {
        return sharedPreferences.getString(CURRENT_USER, null);
    }

    @Override
    public String getToken() {
        return sharedPreferences.getString(CURRENT_USER_TOKEN, null);
    }

    @Override
    public void setLastUser(String lastUser) {
        sharedPreferences.edit().putString(LAST_USER, lastUser).apply();
    }

    @Override
    public String getLastUser() {
        return sharedPreferences.getString(LAST_USER, null);
    }

    @Override
    public void clearDataStore() {
        sharedPreferences.edit().clear().apply();
    }

    @Override
    public void setCameraType(String cameraType) {
        sharedPreferences.edit().putString(LAST_CAMERA_TYPE, cameraType).apply();
    }

    @Override
    public String getCameraType() {
        return sharedPreferences.getString(LAST_CAMERA_TYPE, CameraDetector.CameraType.CAMERA_FRONT.name());
    }
}
