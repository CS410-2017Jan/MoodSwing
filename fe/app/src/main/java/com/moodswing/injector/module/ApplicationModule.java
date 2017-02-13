package com.moodswing.injector.module;

import android.app.Application;

import com.moodswing.MoodSwingApplication;
import com.moodswing.injector.scope.PerApplication;

import dagger.Module;
import dagger.Provides;

/**
 * Created by daniel on 12/02/17.
 */

@Module
public class ApplicationModule {
    private final MoodSwingApplication application;

    public ApplicationModule(MoodSwingApplication application) {this.application = application;}

    @Provides
    @PerApplication
    public MoodSwingApplication provideMvpApplication() {return application;}

    @Provides
    @PerApplication
    public Application provideApplication() {return application;}
}
