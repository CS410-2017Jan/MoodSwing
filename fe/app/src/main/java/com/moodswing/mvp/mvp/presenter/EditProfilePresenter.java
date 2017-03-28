package com.moodswing.mvp.mvp.presenter;

import android.util.Log;

import com.moodswing.mvp.data.SharedPreferencesManager;
import com.moodswing.mvp.domain.EditProfilePictureUsecase;
import com.moodswing.mvp.domain.GetProfilePictureUsecase;
import com.moodswing.mvp.domain.GetUserUsecase;
import com.moodswing.mvp.domain.PutProfileUsecase;
import com.moodswing.mvp.mvp.model.ChangeProfileRequest;
import com.moodswing.mvp.mvp.model.User;
import com.moodswing.mvp.mvp.model.response.ChangeProfileResponse;
import com.moodswing.mvp.mvp.model.response.ProfilePictureResponse;
import com.moodswing.mvp.mvp.view.EditProfileView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Kenny on 2017-02-27.
 */

public class EditProfilePresenter implements Presenter<EditProfileView> {
    private EditProfilePictureUsecase editProfilePictureUseCase;
    private GetProfilePictureUsecase getProfilePictureUsecase;
    private PutProfileUsecase putProfileUsecase;
    private GetUserUsecase getUserUsecase;
    private EditProfileView editProfileView;
    private SharedPreferencesManager sharedPreferencesManager;
    private Disposable newProfilePictureSubscription;
    private Disposable getProfilePictureSubcscription;
    private Disposable putProfileSubscription;
    private Disposable getUserSubscription;

    public EditProfilePresenter(EditProfilePictureUsecase editProfilePictureUsecase,
                                GetProfilePictureUsecase getProfilePictureUsecase,
                                PutProfileUsecase putProfileUsecase,
                                GetUserUsecase getUserUsecase) {
        this.editProfilePictureUseCase = editProfilePictureUsecase;
        this.getProfilePictureUsecase = getProfilePictureUsecase;
        this.putProfileUsecase = putProfileUsecase;
        this.getUserUsecase = getUserUsecase;
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
                        editProfileView.returnFromSavedProfile();
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.v("PROFILE_PICTURE", "FAILURE: " + throwable.getMessage());
                    }
                });
    }

    public void getUser() {
        getUserUsecase.setUsername(sharedPreferencesManager.getCurrentUser());
        getUserSubscription = getUserUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<User>>() {
                    @Override
                    public void accept(Response<User> response) throws Exception {
                        if (response.code() == 200) {
                            User user = response.body();
                            editProfileView.onGetUserInfoSuccess(user);
                        } else {
                            editProfileView.onGetUserInfoFailure();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        editProfileView.showError();
                    }
                });
    }

    public void putProfile(String oldPassword, String newPassword, String newDisplayName) {
        String token = sharedPreferencesManager.getToken();
        putProfileUsecase.seToken(token);
        putProfileUsecase.setChangeProfileRequest(new ChangeProfileRequest(oldPassword, newPassword, newDisplayName));

        putProfileSubscription = putProfileUsecase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<ChangeProfileResponse>>() {
                    @Override
                    public void accept(Response<ChangeProfileResponse> response) throws Exception {
                        if (response.code() == 200) {
                            editProfileView.displaySavedProfile();
                        } else {
//                            Log.i("RESPONSE ERROR", "IMMMM IN THE ELSSSEEEE");
//                            Log.i("RESPONSE ERROR", response.errorBody().string().toString());
//                            String t[] = response.errorBody().string().toString().split(":");
//                            String temp = t[1];
//                            temp = temp.substring(0, temp.length() - 1);
                            editProfileView.displaySaveError("Your password is wrong");
                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("THROWABLE ERROR", throwable.getMessage());

                    }
                });
    }

    public void getPicture() {
        getProfilePictureUsecase.setToken(sharedPreferencesManager.getCurrentUser());

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
                        editProfileView.noPictureMessage();
                        Log.d("GET_PROFILE_PICTURE", "FAILURE: " + throwable.getMessage());
                    }
                });

    }


}
