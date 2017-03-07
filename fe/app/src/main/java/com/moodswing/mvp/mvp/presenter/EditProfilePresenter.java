package com.moodswing.mvp.mvp.presenter;

import android.content.ContentResolver;
import android.media.Image;
import android.media.MediaSyncEvent;
import android.net.Uri;

import com.moodswing.activity.EditProfileActivity;
import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.EditProfilePictureUsecase;
import com.moodswing.mvp.mvp.model.ProfilePicture;
import com.moodswing.mvp.mvp.model.ProfilePictureResponse;
import com.moodswing.mvp.mvp.view.EditProfileView;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Kenny on 2017-02-27.
 */

public class EditProfilePresenter implements Presenter<EditProfileView> {
    private EditProfilePictureUsecase editProfilePictureUseCase;
    private EditProfileView editProfileView;
    private SharedPreferencesManager sharedPreferencesManager;
    private Disposable newProfilePictureSubscription;

    public EditProfilePresenter(EditProfilePictureUsecase editProfilePictureUsecase) {
        this.editProfilePictureUseCase = editProfilePictureUsecase;
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
    public void attachView(EditProfileView view) {
        this.editProfileView = view;
    }

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }

    public void setPicture(RequestBody description, MultipartBody.Part body, File picture) {
        editProfilePictureUseCase.setPicture(new ProfilePicture(picture, sharedPreferencesManager.getToken()));

        newProfilePictureSubscription = editProfilePictureUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProfilePictureResponse>() {
                    @Override
                    public void accept(ProfilePictureResponse profilePictureResponse) throws Exception {
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });

    }


}
