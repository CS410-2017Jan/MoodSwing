package com.moodswing.mvp.mvp.view;

import com.moodswing.widget.DateBlock;

import okhttp3.ResponseBody;

/**
 * Created by Matthew on 2017-03-27.
 */

public interface CaptureViewOther extends View {
    void onPostCommentSuccess();
    void showComments(DateBlock dateBlock);
    void showEntryPic(ResponseBody picture);
    void onPostCommentFailure();
    void onGetCommentFailure();
    void showError1();
    void showError2();
}
