package com.mvp.mvp.presenter;

import com.mvp.mvp.view.View;

/**
 * Created by daniel on 11/02/17.
 */

public interface Presenter<T> extends View {
    void onCreate();

    void onStart();

    void onStop();

    void onPasue();

    void attachView(T view);
}
