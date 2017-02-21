package com.moodswing.injector.module;

import android.app.Application;

import com.moodswing.MoodSwingApplication;
import com.moodswing.injector.scope.PerApplication;

import dagger.Module2;
import dagger.Provides2;

/**
 * Created by daniel on 12/02/17.
 */

@Module2
public class ApplicationModule {
    private final MoodSwingApplication application;

    public ApplicationModule(MoodSwingApplication application) {this.application = application;}

    @Provides2
    @PerApplication
    public MoodSwingApplication provideMvpApplication() {return application;}

    @Provides2
    @PerApplication
    public Application provideApplication() {return application;}

}
