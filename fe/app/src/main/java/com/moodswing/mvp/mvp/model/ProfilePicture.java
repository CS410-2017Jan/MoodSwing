package com.moodswing.mvp.mvp.model;

import android.app.DownloadManager;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Kenny on 2017-02-27.
 */

public class ProfilePicture {
    private MultipartBody.Part picture;
    //private RequestBody description;

    public ProfilePicture(MultipartBody.Part picture) {
        this.picture = picture;
     //   this.description = description;
    }

    public MultipartBody.Part getPicture(){
        return picture;
    }
/*
    public RequestBody getDescription() {
        return description;
    }
*/
}
