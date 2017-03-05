package com.moodswing.injector.component;

import com.moodswing.activity.EditProfileActivity;
import com.moodswing.injector.module.ActivityModule;
import com.moodswing.injector.module.EditProfileModule;
import com.moodswing.injector.scope.PerActivity;
import dagger.Component;

/**
 * Created by Kenny on 2017-02-27.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class,
                EditProfileModule.class

        })
public interface EditProfileComponent {
    void inject(EditProfileActivity editProfileActivity);
}
