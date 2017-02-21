package com.moodswing.injector.component;

import com.moodswing.activity.JournalActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.JournalModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by daniel on 20/02/17.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                JournalModule.class
        })
public interface JournalComponent {
    // TODO: inject fragment...?
    void inject(JournalActivity journalActivity);
}

