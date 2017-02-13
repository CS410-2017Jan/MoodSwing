package com.moodswing;

import android.app.Application;

import com.moodswing.injector.component.ApplicationComponent;
import com.moodswing.injector.component.DaggerApplicationComponent;
import com.moodswing.injector.module.ApplicationModule;

/**
 * Created by daniel on 12/02/17.
 */

public class MoodSwingApplication extends Application {
    protected ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setupInjector();
    }

    private void setupInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {return applicationComponent;}
}
