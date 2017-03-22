package com.moodswing.mvp.mvp.view;

/**
 * Created by Matthew on 2017-03-21.
 */

public interface EditEntryView extends View{
    void onEditEntrySuccess();
    void onEditEntryFailure();
    void showError();
}
