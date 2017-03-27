package com.moodswing.injector.component;

import com.moodswing.activity.JournalActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.JournalModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;



@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                JournalModule.class
        })

public interface JournalComponent {
    void inject(JournalActivity journalActivity);
}

