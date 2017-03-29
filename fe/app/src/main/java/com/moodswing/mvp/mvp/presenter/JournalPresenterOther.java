package com.moodswing.mvp.mvp.presenter;

import android.util.Log;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.DeleteCaptureUsecase;
import com.moodswing.mvp.domain.FollowUsecase;
import com.moodswing.mvp.domain.GetEntryPicUsecase;
import com.moodswing.mvp.domain.GetJournalsUsecase;
import com.moodswing.mvp.domain.GetProfilePictureUsecase;
import com.moodswing.mvp.domain.GetUserUsecase;
import com.moodswing.mvp.domain.SearchUsecase;
import com.moodswing.mvp.domain.SetTitleUsecase;
import com.moodswing.mvp.domain.UnfollowUsecase;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.Title;
import com.moodswing.mvp.mvp.model.response.SimpleResponse;
import com.moodswing.mvp.mvp.view.JournalViewOther;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;



public class JournalPresenterOther implements Presenter<JournalViewOther> {
    private JournalViewOther journalView;
    private GetJournalsUsecase getJournalsUsecase;
    private DeleteCaptureUsecase deleteCaptureUsecase;
    private SetTitleUsecase setTitleUsecase;
    private SearchUsecase searchUsecase;
    private GetProfilePictureUsecase getProfilePictureUsecase;
    private GetEntryPicUsecase getEntryPicUsecase;
    private GetUserUsecase getUserUsecase;
    private FollowUsecase followUsecase;
    private UnfollowUsecase unfollowUsecase;
    private Disposable getJournalsSubscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public JournalPresenterOther(GetJournalsUsecase getJournalsUsecase,
                                 DeleteCaptureUsecase deleteCaptureUsecase,
                                 SetTitleUsecase setTitleUsecase,
                                 SearchUsecase searchUsecase,
                                 GetProfilePictureUsecase getProfilePictureUsecase,
                                 GetEntryPicUsecase getEntryPicUsecase,
                                 GetUserUsecase getUserUsecase,
                                 FollowUsecase followUsecase,
                                 UnfollowUsecase unfollowUsecase) {
        this.getJournalsUsecase = getJournalsUsecase;
        this.deleteCaptureUsecase = deleteCaptureUsecase;
        this.setTitleUsecase = setTitleUsecase;
        this.searchUsecase = searchUsecase;
        this.getProfilePictureUsecase = getProfilePictureUsecase;
        this.getEntryPicUsecase = getEntryPicUsecase;
        this.getUserUsecase = getUserUsecase;
        this.followUsecase = followUsecase;
        this.unfollowUsecase = unfollowUsecase;
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

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }

    public boolean isUserLoggedIn() {
        return sharedPreferencesManager.isUserLoggedIn();
    }

    @Override
    public void attachView(JournalViewOther view) {
        this.journalView = view;
    }

    public void getEntries(String username) {
        getJournalsUsecase.setUsername(username);

        getJournalsSubscription = getJournalsUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<JournalEntries>>() {
                    @Override
                    public void accept(List<JournalEntries> journalEntries) throws Exception {
                        if (true) {
                            journalView.showEntries(journalEntries);
                        } else {
                            journalView.onEntryFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        journalView.showError();
                    }
                });
    }

    public void getUser(String username) {
        getUserUsecase.setUsername(username);
        getJournalsSubscription = getUserUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<User>>() {
                    @Override
                    public void accept(Response<User> response) throws Exception {
                        if (response.code() == 200) {
                            User user = response.body();
                            journalView.onGetUserInfoSuccess(user);
                        } else {
                            journalView.onGetUserInfoFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        journalView.showError();
                    }
                });
    }


    public void deleteCapture(Capture capture) {
        deleteCaptureUsecase.setDeletionId(capture.getId());
        deleteCaptureUsecase.setToken(sharedPreferencesManager.getToken());

        getJournalsSubscription = deleteCaptureUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SimpleResponse>() {
                    @Override
                    public void accept(SimpleResponse deleteCaptureResponse) throws Exception {
                        if (deleteCaptureResponse.isSuccessful()) {
                            journalView.onDeletionSuccess();
                        } else {
                            journalView.onDeletionFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        journalView.showError();
                    }
                });
    }

    public void setTitle(String title, String id) {
        setTitleUsecase.setDateId(id);
        setTitleUsecase.setTitle(new Title(title));
        setTitleUsecase.setToken(sharedPreferencesManager.getToken());

        getJournalsSubscription = setTitleUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SimpleResponse>() {
                    @Override
                    public void accept(SimpleResponse setTitleResponse) throws Exception {
                        if (setTitleResponse.isSuccessful()) {
                            journalView.onSetTitleSuccess();
                        } else {
                            journalView.onSetTitleFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        journalView.showError();
                    }
                });
    }

    public void getProfilePic(String username) {

        getProfilePictureUsecase.setToken(username);
        getJournalsSubscription = getProfilePictureUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody picture) throws Exception {
                        journalView.getPicture(picture);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });

    }

    public void getEntryPic(final String captureId) {
        getEntryPicUsecase.setCaptureId(captureId);
        getEntryPicUsecase.setToken(sharedPreferencesManager.getToken());

        getJournalsSubscription = getEntryPicUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody picture) throws Exception {
                        journalView.showEntryPic(picture, captureId);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("", "****GET " + captureId + " ERROR****");
                    }
                });

    }

    public void follow(String username) {
        followUsecase.setUsername(username);
        followUsecase.setAccessToken(sharedPreferencesManager.getToken());
        Disposable followSubcription = followUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<SimpleResponse>>() {
                    @Override
                    public void accept(Response<SimpleResponse> followResponseResponse) throws Exception {
                        if (followResponseResponse.code() == 200) {
                            if (!followResponseResponse.isSuccessful()) {
                                throw new Exception("Server encountered an error");
                            }
                        } else {
                            throw new Exception("Server responded with " + followResponseResponse.code());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        journalView.showError("Error during follow: " + throwable.getMessage());
                    }
                });
    }

    public void unfollow(String username) {
        unfollowUsecase.setUsername(username);
        unfollowUsecase.setAccessToken(sharedPreferencesManager.getToken());
        Disposable followSubcription = unfollowUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<SimpleResponse>>() {
                    @Override
                    public void accept(Response<SimpleResponse> followResponseResponse) throws Exception {
                        if (followResponseResponse.code() == 200) {
                            if (!followResponseResponse.isSuccessful()) {
                                throw new Exception("Server encountered an error");
                            }
                        } else {
                            throw new Exception("Server responded with " + followResponseResponse.code());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        journalView.showError("Error during unfollow: " + throwable.getMessage());
                    }
                });
    }
}

