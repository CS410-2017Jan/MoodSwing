package com.moodswing.injector.component;

import com.moodswing.activity.CaptureActivity;
import com.moodswing.activity.CaptureActivityOther;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.CaptureModule;
import com.moodswing.injector.module.CaptureModuleOther;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by Matthew on 2017-03-12.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                CaptureModuleOther.class
        })

public interface CaptureComponentOther {
    void inject(CaptureActivityOther captureActivityOther);
}
