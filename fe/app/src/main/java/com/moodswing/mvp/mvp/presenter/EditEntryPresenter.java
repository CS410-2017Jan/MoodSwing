package com.moodswing.mvp.mvp.presenter;

import android.app.ProgressDialog;
import android.content.Context;

import com.moodswing.R;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.EditEntryUsecase;
import com.moodswing.mvp.mvp.model.Text;
import com.moodswing.mvp.mvp.model.response.SimpleResponse;
import com.moodswing.mvp.mvp.view.EditEntryView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Matthew on 2017-03-21.
 */

public class EditEntryPresenter implements Presenter<EditEntryView> {
    private EditEntryView editEntryView;
    private EditEntryUsecase editEntryUsecase;
    private Disposable editEntrySubscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public EditEntryPresenter(EditEntryUsecase editEntryUsecase) {
        this.editEntryUsecase = editEntryUsecase;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void attachView(EditEntryView view) {
        this.editEntryView = view;
    }


    public void setText(String description, String id) {
        editEntryUsecase.setText(new Text(description));
        editEntryUsecase.setId(id);
        editEntryUsecase.setToken(sharedPreferencesManager.getToken());

        final ProgressDialog progressDialog = new ProgressDialog((Context) editEntryView, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        editEntrySubscription = editEntryUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SimpleResponse>() {
                    @Override
                    public void accept(SimpleResponse editEntryResponse) throws Exception {
                        progressDialog.dismiss();
                        if (editEntryResponse.isSuccessful()) {
                            editEntryView.onEditEntrySuccess();
                        } else {
                            editEntryView.onEditEntryFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        progressDialog.dismiss();
                        editEntryView.showError();
                    }
                });
    }

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }
}

