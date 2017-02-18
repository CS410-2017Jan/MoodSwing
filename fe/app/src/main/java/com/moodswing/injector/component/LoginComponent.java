package com.moodswing.injector.component;

import com.moodswing.activity.LoginActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.LoginModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by daniel on 13/02/17.
 */

@PerActivity
@Component (dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                LoginModule.class
        })
public interface LoginComponent {

    void inject(LoginActivity loginActivity);
    // TODO: inject fragment...?
}
