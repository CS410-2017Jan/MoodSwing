package com.moodswing.injector.component;

import com.moodswing.activity.SearchActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.SearchModule;
import com.moodswing.injector.scope.PerActivity;

import dagger.Component;

/**
 * Created by daniel on 16/03/17.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                SearchModule.class
        })
public interface SearchComponent {

    void inject(SearchActivity searchActivity);
}
