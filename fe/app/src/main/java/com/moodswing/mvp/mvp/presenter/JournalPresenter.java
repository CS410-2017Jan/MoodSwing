package com.moodswing.mvp.mvp.presenter;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.DeleteCaptureUsecase;
import com.moodswing.mvp.domain.GetJournalsUsecase;
import com.moodswing.mvp.domain.GetProfilePictureUsecase;
import com.moodswing.mvp.domain.SearchUsecase;
import com.moodswing.mvp.domain.SetTitleUsecase;
import com.moodswing.mvp.mvp.model.Capture;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.model.response.DeleteCaptureResponse;
import com.moodswing.mvp.mvp.model.JournalEntries;
import com.moodswing.mvp.mvp.model.response.SetTitleResponse;
import com.moodswing.mvp.mvp.model.Title;
import com.moodswing.mvp.mvp.view.JournalView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by daniel on 11/02/17.
 */

public class JournalPresenter implements Presenter<JournalView> {
    private JournalView journalView;
    private GetJournalsUsecase getJournalsUsecase;
    private DeleteCaptureUsecase deleteCaptureUsecase;
    private SetTitleUsecase setTitleUsecase;
    private SearchUsecase searchUsecase;
    private GetProfilePictureUsecase getProfilePictureUsecase;
    private Disposable getJournalsSubscription;
    private SharedPreferencesManager sharedPreferencesManager;

    public JournalPresenter(GetJournalsUsecase getJournalsUsecase, DeleteCaptureUsecase deleteCaptureUsecase, SetTitleUsecase setTitleUsecase, SearchUsecase searchUsecase, GetProfilePictureUsecase getProfilePictureUsecase) {
        this.getJournalsUsecase = getJournalsUsecase;
        this.deleteCaptureUsecase = deleteCaptureUsecase;
        this.setTitleUsecase = setTitleUsecase;
        this.searchUsecase = searchUsecase;
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
    public void attachView(JournalView view) {
        this.journalView = view;
    }

    public void getEntries() {
        getJournalsUsecase.setUsername(sharedPreferencesManager.getCurrentUser());

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

    public void getUsers() {
        getJournalsSubscription = searchUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<List<User>>>() {
                    @Override
                    public void accept(Response<List<User>> listResponse) throws Exception {
                        if (listResponse.code() == 200) {
                            journalView.onGetUserInfoSuccess(listResponse.body());
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
                .subscribe(new Consumer<DeleteCaptureResponse>() {
                    @Override
                    public void accept(DeleteCaptureResponse deleteCaptureResponse) throws Exception {
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
                .subscribe(new Consumer<SetTitleResponse>() {
                    @Override
                    public void accept(SetTitleResponse setTitleResponse) throws Exception {
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

    public void getProfilePic() {
        String token = sharedPreferencesManager.getToken();
        getProfilePictureUsecase.setToken(token);

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
                        journalView.showError();
                    }
                });

    }



    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }

    public boolean isUserLoggedIn() {
        return sharedPreferencesManager.isUserLoggedIn();
    }
}
