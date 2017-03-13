package com.moodswing.mvp.mvp.view;

/**
 * Created by Matthew on 2017-03-12.
 */

public interface CaptureView extends View {
    void onCaptureSuccess();
    void onCaptureFailure();
    void showError();
}
