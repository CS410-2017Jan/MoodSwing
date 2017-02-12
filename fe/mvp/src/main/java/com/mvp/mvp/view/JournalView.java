package com.mvp.mvp.view;

import com.mvp.mvp.model.JournalEntry;
import java.util.List;

/**
 * Created by daniel on 11/02/17.
 */

public interface JournalView extends View {

    void showJournals(List<JournalEntry> journalEntries);
    void showLoading();
    void showError();

}
