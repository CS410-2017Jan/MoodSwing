package com.moodswing.injector.component;

import com.moodswing.activity.EditEntryActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.EditEntryModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by Matthew on 2017-03-21.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                EditEntryModule.class
        })
public interface EditEntryComponent {
    void inject(EditEntryActivity editEntryActivity);
}
