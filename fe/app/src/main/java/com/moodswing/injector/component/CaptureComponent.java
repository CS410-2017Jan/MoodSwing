package com.moodswing.injector.component;

import com.moodswing.activity.CaptureActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.CaptureModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by Matthew on 2017-03-12.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                CaptureModule.class
        })

public interface CaptureComponent {
    void inject(CaptureActivity captureActivity);
}
