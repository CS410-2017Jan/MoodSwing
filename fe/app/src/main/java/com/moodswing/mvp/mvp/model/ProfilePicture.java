package com.moodswing.mvp.mvp.model;

import android.media.Image;
import android.net.Uri;

import java.io.File;

/**
 * Created by Kenny on 2017-02-27.
 */

public class ProfilePicture {
    private File picture;
    private String token;

    public ProfilePicture(File picture, String token) {
        this.picture = picture;
        this.token = token;
    }

    public File getPicture(){
        return picture;
    }

    public String getToken() {
        return token;
    }
}
