package com.moodswing.injector.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.moodswing.injector.scope.PerApplication;
import com.moodswing.mvp.data.SharedPreferencesManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by daniel on 20/02/17.
 */

@Module
public class DataModule {
    @Provides
    @PerApplication
    SharedPreferencesManager provideSharedPreferencesManager(Application application) {
        return new SharedPreferencesManager(application);
    }
}
