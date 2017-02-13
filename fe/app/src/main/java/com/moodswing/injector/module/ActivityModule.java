package com.moodswing.injector.module;

import android.app.Activity;
import android.content.Context;

import com.moodswing.injector.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by daniel on 12/02/17.
 */

@Module
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {this.activity = activity;}

    @Provides
    @PerActivity
    public Context context() {return activity;}
}
