package com.moodswing.injector.component;

import android.app.Application;

import com.moodswing.MoodSwingApplication;
import com.moodswing.injector.module.ApplicationModule;
import com.moodswing.injector.module.NetworkModule;
import com.moodswing.injector.scope.PerApplication;
import com.mvp.network.Repository;

import dagger.Component;

/**
 * Created by daniel on 12/02/17.
 */

@PerApplication
@Component(modules = {ApplicationModule.class,
        NetworkModule.class})
public interface ApplicationComponent {

    Application application();
    MoodSwingApplication moodSwingApplication();
    Repository repository();
}
