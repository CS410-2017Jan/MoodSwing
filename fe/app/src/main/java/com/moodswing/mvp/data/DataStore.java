package com.moodswing.mvp.data;

/**
 * Created by daniel on 21/02/17.
 */

public interface DataStore {
    void login(String username, String token);
    void logout(String username);
    boolean isUserLoggedIn();
    String getCurrentUser();
    String getToken();
    void setLastUser(String lastUser);
    String getLastUser();
    void clearDataStore();
}
