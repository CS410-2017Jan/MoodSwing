package com.moodswing.injector.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by daniel on 12/02/17.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME) public @interface PerActivity {
}
