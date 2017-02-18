package com.moodswing.mvp.domain;

import io.reactivex.*;

/**
 * Created by daniel on 11/02/17.
 */

public interface Usecase<T> {
    Observable<T> execute();
}
