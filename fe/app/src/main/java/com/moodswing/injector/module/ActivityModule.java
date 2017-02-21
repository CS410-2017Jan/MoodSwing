package com.moodswing.injector.module;

import android.app.Activity;
import android.content.Context;

import com.moodswing.injector.scope.PerActivity;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by daniel on 12/02/17.
 */

@Module2
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {this.activity = activity;}

    @Provides2
    @PerActivity
    public Context context() {return activity;}
}
