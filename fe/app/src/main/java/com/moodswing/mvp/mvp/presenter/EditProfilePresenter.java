package com.moodswing.mvp.mvp.presenter;

import android.media.Image;
import android.net.Uri;

import com.moodswing.activity.EditProfileActivity;
import com.moodswing.mvp.domain.EditProfilePictureUsecase;
import com.moodswing.mvp.mvp.view.EditProfileView;

/**
 * Created by Kenny on 2017-02-27.
 */

public class EditProfilePresenter implements Presenter<EditProfileView> {
    private EditProfilePictureUsecase editProfilePictureUseCase;
    private EditProfileView editProfileView;

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

    public void changePicture(Uri picture) {

    }
}
