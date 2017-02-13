package com.mvp.domain;

import com.mvp.mvp.model.ResponseWrapper;

import rx.functions.Func1;

/**
 * Created by daniel on 13/02/17.
 */

public class ResponseMappingFunc <R> implements Func1<ResponseWrapper<R>, R> {

    @Override
    public R call(ResponseWrapper<R> rResponseWrapper) {
        if (rResponseWrapper == null) {
            return null;
        }
        return rResponseWrapper.body;
    }
}
