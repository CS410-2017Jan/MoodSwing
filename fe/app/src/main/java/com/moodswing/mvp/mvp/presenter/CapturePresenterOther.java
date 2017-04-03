package com.moodswing.mvp.mvp.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.moodswing.R;
import com.moodswing.mvp.data.SharedPreferencesManager;
//import com.moodswing.mvp.domain.CaptureUsecase;
import com.moodswing.mvp.domain.CaptureUsecase;
import com.moodswing.mvp.domain.GetCommentsUsecase;
import com.moodswing.mvp.domain.GetEntryPicHighResUsecase;
import com.moodswing.mvp.domain.GetProfilePictureUsecase;
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.model.response.SimpleResponse;
import com.moodswing.mvp.mvp.view.CaptureViewOther;
import com.moodswing.widget.DateBlock;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Matthew on 2017-03-12.
 */

public class CapturePresenterOther implements Presenter<CaptureViewOther> {
    private CaptureViewOther captureView;
    private CaptureUsecase captureUsecase;
    private GetCommentsUsecase getCommentsUsecase;
    private GetEntryPicHighResUsecase getEntryPicHighResUsecase;
    private GetProfilePictureUsecase getProfilePictureUsecase;
    private Disposable captureSubscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public CapturePresenterOther(CaptureUsecase captureUsecase, GetCommentsUsecase getCommentsUsecase, GetEntryPicHighResUsecase getEntryPicHighResUsecase, GetProfilePictureUsecase getProfilePictureUsecase) {
        this.captureUsecase = captureUsecase;
        this.getCommentsUsecase = getCommentsUsecase;
        this.getEntryPicHighResUsecase = getEntryPicHighResUsecase;
        this.getProfilePictureUsecase = getProfilePictureUsecase;
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
    public void attachView(CaptureViewOther view) {
        this.captureView = view;
    }


    public void postComment(String comment, String id) {
        captureUsecase.setDateId(id);
        captureUsecase.setComment(new Comment(comment));
        captureUsecase.setToken(sharedPreferencesManager.getToken());

        final ProgressDialog progressDialog = new ProgressDialog((Context) captureView, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        captureSubscription = captureUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SimpleResponse>() {
                    @Override
                    public void accept(SimpleResponse postCommentResponse) throws Exception {
                        progressDialog.dismiss();
                        if (postCommentResponse.isSuccessful()) {
                            captureView.onPostCommentSuccess();
                        } else {
                            captureView.onPostCommentFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        progressDialog.dismiss();
                        captureView.showError1();
                    }
                });
    }

    public void getComments(String id) {
        getCommentsUsecase.setDateId(id);

        captureSubscription = getCommentsUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DateBlock>() {
                    @Override
                    public void accept(DateBlock dateBlock) throws Exception {
                        if (true) {
                            captureView.showComments(dateBlock);
                        } else {
                            captureView.onGetCommentFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        captureView.showError2();
                    }
                });
    }

    public void getProfilePic(String username, final String id) {
        getProfilePictureUsecase.setToken(username);
        captureSubscription = getProfilePictureUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody picture) throws Exception {
                        captureView.showProfPic(picture, id);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });

    }

    public void getPic(final String captureId, final ProgressDialog progressDialog) {
        getEntryPicHighResUsecase.setCaptureId(captureId);
        getEntryPicHighResUsecase.setToken(sharedPreferencesManager.getToken());

        captureSubscription = getEntryPicHighResUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody picture) throws Exception {
                        captureView.showEntryPic(picture, progressDialog);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("", "****GET " + captureId + " ERROR****");
                        Log.i("", "****GET " + throwable.getMessage() + " ERROR****");
                    }
                });

    }


    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }
}
