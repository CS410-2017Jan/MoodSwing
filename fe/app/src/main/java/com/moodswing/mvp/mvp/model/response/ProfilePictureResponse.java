package com.moodswing.mvp.mvp.model.response;

import android.media.Image;

import java.io.File;

/**
 * Created by Kenny on 2017-02-27.
 */

public class ProfilePictureResponse {
    private boolean success;

    public ProfilePictureResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccessful() {
        return success;
    }

}
