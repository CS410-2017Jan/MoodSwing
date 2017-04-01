package com.moodswing.mvp.mvp.view;


import android.app.ProgressDialog;

import com.moodswing.mvp.mvp.model.User;
import com.moodswing.widget.DateBlock;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Matthew on 2017-03-12.
 */

public interface CaptureView extends View {
    void onPostCommentSuccess();
    void showComments(DateBlock dateBlock);
    void showEntryPic(ResponseBody picture, final ProgressDialog progressDialog);
    void showProfPic(ResponseBody picture, String id);
    void onPostCommentFailure();
    void onGetCommentFailure();
    void showError1();
    void showError2();
}
