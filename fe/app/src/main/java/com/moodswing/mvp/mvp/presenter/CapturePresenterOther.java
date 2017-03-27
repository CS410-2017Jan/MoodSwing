package com.moodswing.mvp.mvp.presenter;

import android.util.Log;

import com.moodswing.mvp.data.SharedPreferencesManager;
//import com.moodswing.mvp.domain.CaptureUsecase;
import com.moodswing.mvp.domain.CaptureUsecase;
import com.moodswing.mvp.domain.GetCommentsUsecase;
import com.moodswing.mvp.domain.GetEntryPicHighResUsecase;
import com.moodswing.mvp.domain.GetEntryPicUsecase;
import com.moodswing.mvp.mvp.model.Comment;
import com.moodswing.mvp.mvp.model.response.PostCommentResponse;
import com.moodswing.mvp.mvp.view.CaptureView;
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
    private Disposable captureSubscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public CapturePresenterOther(CaptureUsecase captureUsecase, GetCommentsUsecase getCommentsUsecase, GetEntryPicHighResUsecase getEntryPicHighResUsecase) {
        this.captureUsecase = captureUsecase;
        this.getCommentsUsecase = getCommentsUsecase;
        this.getEntryPicHighResUsecase = getEntryPicHighResUsecase;
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

        captureSubscription = captureUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PostCommentResponse>() {
                    @Override
                    public void accept(PostCommentResponse postCommentResponse) throws Exception {
                        if (postCommentResponse.isSuccessful()) {
                            captureView.onPostCommentSuccess();
                        } else {
                            captureView.onPostCommentFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
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

    public void getPic(final String captureId) {
        getEntryPicHighResUsecase.setCaptureId(captureId);
        getEntryPicHighResUsecase.setToken(sharedPreferencesManager.getToken());

        captureSubscription = getEntryPicHighResUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody picture) throws Exception {
                        captureView.showEntryPic(picture);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("", "****GET " + captureId + " ERROR****");
                    }
                });

    }


    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }
}
