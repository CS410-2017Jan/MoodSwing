package com.moodswing.mvp.mvp.model;

/**
 * Created by Kenny on 2017-03-22.
 */

public class ChangeProfileRequest {
    private String oldPassword;
    private String newPassword;
    private String newDisplayName;

    public ChangeProfileRequest(String oldPassword, String newPassword, String newDisplayName) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.newDisplayName = newDisplayName;
    }
}
