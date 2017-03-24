package com.moodswing.injector.component;

import com.moodswing.activity.FollowingActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.FollowingModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by daniel on 23/03/17.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                FollowingModule.class
        })
public interface FollowingComponent {

    void inject(FollowingActivity followingActivity);
}
