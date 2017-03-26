package com.moodswing.mvp.mvp.model.response;

/**
 * Created by daniel on 26/03/17.
 */

public class FollowResponse {
    private boolean success;

    public FollowResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccessful() { return success;}
}
