package com.moodswing.injector.scope;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by daniel on 12/02/17.
 */

@Scope
@Retention(RUNTIME) public @interface PerApplication {}
