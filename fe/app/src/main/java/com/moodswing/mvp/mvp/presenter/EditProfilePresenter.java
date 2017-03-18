package com.moodswing.mvp.mvp.presenter;

import android.util.Log;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.EditProfilePictureUsecase;
import com.moodswing.mvp.domain.GetProfilePictureUsecase;
import com.moodswing.mvp.mvp.model.response.ProfilePictureResponse;
import com.moodswing.mvp.mvp.view.EditProfileView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

/**
 * Created by Kenny on 2017-02-27.
 */

public class EditProfilePresenter implements Presenter<EditProfileView> {
    private EditProfilePictureUsecase editProfilePictureUseCase;
    private GetProfilePictureUsecase getProfilePictureUsecase;
    private EditProfileView editProfileView;
    private SharedPreferencesManager sharedPreferencesManager;
    private Disposable newProfilePictureSubscription;
    private Disposable getProfilePictureSubcscription;

    public EditProfilePresenter(EditProfilePictureUsecase editProfilePictureUsecase,
                                GetProfilePictureUsecase getProfilePictureUsecase) {
        this.editProfilePictureUseCase = editProfilePictureUsecase;
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
    public void attachView(EditProfileView view) {
        this.editProfileView = view;
    }

    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
        this.sharedPreferencesManager = sharedPreferencesManager;
    }

    public void postPicture(MultipartBody.Part picture) {
        String token = sharedPreferencesManager.getToken();
        editProfilePictureUseCase.setToken(token);
        editProfilePictureUseCase.setProfilePicture(picture);

        newProfilePictureSubscription = editProfilePictureUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProfilePictureResponse>() {
                    @Override
                    public void accept(ProfilePictureResponse profilePictureResponse) throws Exception {
                        Log.v("PROFILE_PICTURE", "SUCCESS");
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.v("PROFILE_PICTURE", "FAILURE: " + throwable.getMessage());
                    }
                });
    }

    public void getPicture() {
        String token = sharedPreferencesManager.getToken();
        getProfilePictureUsecase.setToken(token);

        getProfilePictureSubcscription = getProfilePictureUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody picture) throws Exception {
                        editProfileView.getPicture(picture);
                        Log.d("GET_PROFILE_PICTURE", "SUCCESS");
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("GET_PROFILE_PICTURE", "FAILURE: " + throwable.getMessage());
                    }
                });

    }


}
