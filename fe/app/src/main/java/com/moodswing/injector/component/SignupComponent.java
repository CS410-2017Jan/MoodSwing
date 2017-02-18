package com.moodswing.injector.component;

import com.moodswing.activity.SignupActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.SignupModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by daniel on 18/02/17.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                SignupModule.class
        })
public interface SignupComponent {

    void inject(SignupActivity signupActivity);
    // TODO: inject fragment...?
}
