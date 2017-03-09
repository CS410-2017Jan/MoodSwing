package com.moodswing.mvp.mvp.view;

import android.app.ProgressDialog;

/**
 * Created by Matthew on 2017-03-04.
 */

public interface NewEntryView extends View {
    void onNewEntrySuccess();
    void onNewEntryFailure();
    void showError();
}
