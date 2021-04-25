package com.seaky.netframe.build;

import com.seaky.netframe.exception.ApiException;
import com.seaky.netframe.exception.ExceptionEngine;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public abstract class DownloadObserver<T> implements Observer<T> {

    protected void onStart(){}
    protected abstract void onFailure(ApiException e);
    protected abstract void onSuccess(T t);


    @Override
    public void onError(@NonNull Throwable e) {
        if(e instanceof ApiException) {
            onFailure((ApiException) e);
        } else {
            onFailure(new ApiException(e, ExceptionEngine.UN_KNOWN_ERROR));
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        onStart();
    }

    @Override
    public void onNext(@NonNull T t) {
        onSuccess(t);
    }

    @Override
    public void onComplete() {}
}
