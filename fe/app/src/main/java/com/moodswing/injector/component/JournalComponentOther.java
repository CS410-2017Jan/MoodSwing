package com.moodswing.injector.component;

import com.moodswing.activity.JournalActivityOther;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.JournalModuleOther;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;



@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                JournalModuleOther.class
        })

public interface JournalComponentOther {
    void inject(JournalActivityOther journalActivityOther);
}

