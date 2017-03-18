package com.moodswing.mvp.mvp.view;

import com.moodswing.mvp.mvp.model.journalobjects.JournalEntries;

import java.util.List;

/**
 * Created by Matthew on 2017-03-04.
 */

public interface NewEntryView extends View {
    void onNewEntrySuccess();
    void onNewEntryFailure();
    void onEntrySuccess(List<JournalEntries> je);
    void onEntryFailure();
    void onSetTitleSuccess();
    void onSetTitleFailure();
    void showError();
}
