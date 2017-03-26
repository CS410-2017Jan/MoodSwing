package com.moodswing.injector.component;
import com.moodswing.activity.NotificationsActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.NotificationsModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by Kenny on 2017-03-25.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                NotificationsModule.class
        })

public interface NotificationsComponent {
        void inject (NotificationsActivity notificationsActivity);
}
