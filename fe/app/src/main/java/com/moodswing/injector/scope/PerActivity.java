package com.moodswing.injector.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope2;

/**
 * Created by daniel on 12/02/17.
 */

@Scope2
@Retention(RetentionPolicy.RUNTIME) public @interface PerActivity {
}
