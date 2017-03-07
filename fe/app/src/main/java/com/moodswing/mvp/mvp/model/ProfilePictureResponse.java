package com.moodswing.mvp.mvp.model;

import android.media.Image;

import java.io.File;

/**
 * Created by Kenny on 2017-02-27.
 */

public class ProfilePictureResponse {
    private File picture;
    private boolean success;

    public ProfilePictureResponse(File picture, boolean success) {
        this.picture = picture;
        this.success = success;
    }

    public boolean isSuccessful() {
        return success;
    }

    public File getPicture() {
        return picture;
    }

}
