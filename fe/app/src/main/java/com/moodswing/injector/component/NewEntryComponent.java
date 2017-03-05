package com.moodswing.injector.component;

import com.moodswing.activity.NewEntryActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.NewEntryModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by Matthew on 2017-03-04.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                NewEntryModule.class
        })
public interface NewEntryComponent {
    // TODO: inject fragment...?
    void inject(NewEntryActivity newEntryActivity);
}
