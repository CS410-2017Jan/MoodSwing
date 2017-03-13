//package com.moodswing.mvp.mvp.presenter;
//
//import com.moodswing.mvp.data.SharedPreferencesManager;
//import com.moodswing.mvp.domain.CaptureUsecase;
//import com.moodswing.mvp.mvp.view.CaptureView;
//
//import io.reactivex.disposables.Disposable;
//
///**
// * Created by Matthew on 2017-03-12.
// */
//
//public class CapturePresenter implements Presenter<CaptureView> {
//    private CaptureView captureView;
//    private CaptureUsecase captureUsecase;
//    private Disposable captureSubscription;
//    private SharedPreferencesManager sharedPreferencesManager;
//
//    public CapturePresenter(CaptureUsecase captureUsecase) {
//        this.captureUsecase = captureUsecase;
//    }
//
//    @Override
//    public void onCreate() {
//
//    }
//
//    @Override
//    public void onStart() {
//
//    }
//
//    @Override
//    public void onStop() {
//
//    }
//
//    @Override
//    public void onPause() {
//
//    }
//
//    @Override
//    public void attachView(CaptureView view) {
//        this.captureView = view;
//    }
//
//
//    public void getCaptureData(String description, String date) {
////        newEntryUsecase.setCapture(new Capture(description, date));
////        newEntryUsecase.setToken(sharedPreferencesManager.getToken());
////
////        newEntrySubscription = newEntryUsecase.execute()
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(new Consumer<NewEntryResponse>() {
////                    @Override
////                    public void accept(NewEntryResponse newEntryResponse) throws Exception {
////                        if (newEntryResponse.isSuccessful()) {
////                            newEntryView.onNewEntrySuccess();
////                        } else {
////                            newEntryView.onNewEntryFailure();
////                        }
////                    }
////                }, new Consumer<Throwable>() {
////                    @Override
////                    public void accept(Throwable throwable) throws Exception {
////                        newEntryView.showError();
////                    }
////                });
//    }
//
//
//    public void attachSharedPreferencesManager(SharedPreferencesManager sharedPreferencesManager) {
//        this.sharedPreferencesManager = sharedPreferencesManager;
//    }
//}
