package com.moodswing.injector.module;

import android.app.Application;

import com.moodswing.injector.scope.PerApplication;
import com.moodswing.mvp.data.SharedPreferencesManager;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by daniel on 20/02/17.
 */

@Module2
public class DataModule {
    @Provides2
    @PerApplication
    SharedPreferencesManager provideSharedPreferencesManager(Application application) {
        return new SharedPreferencesManager(application);
    }
}
