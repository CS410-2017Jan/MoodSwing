package com.moodswing.mvp.mvp.view;

/**
 * Created by daniel on 11/02/17.
 */

public interface JournalView extends View {

    void showEntries();
    void onEntryFailure();
    void showError();
}
