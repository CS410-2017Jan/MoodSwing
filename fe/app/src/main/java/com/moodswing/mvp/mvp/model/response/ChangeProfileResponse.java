package com.moodswing.mvp.mvp.model.response;

import com.moodswing.mvp.mvp.model.ChangeProfileRequest;

/**
 * Created by Kenny on 2017-03-21.
 */

public class ChangeProfileResponse {
    private boolean success;
    private String message;

    public ChangeProfileResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccessful() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
