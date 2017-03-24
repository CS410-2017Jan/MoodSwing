package com.moodswing.injector.component;

import com.moodswing.activity.CameraActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by daniel on 04/03/17.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
        })
public interface CameraComponent {
        void inject(CameraActivity cameraActivity);
}
