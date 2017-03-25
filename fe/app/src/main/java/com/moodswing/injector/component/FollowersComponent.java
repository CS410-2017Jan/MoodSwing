package com.moodswing.injector.component;

import com.moodswing.activity.FollowersActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.FollowersModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by daniel on 24/03/17.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                FollowersModule.class
        })
public interface FollowersComponent {

    void inject(FollowersActivity followingActivity);
}
