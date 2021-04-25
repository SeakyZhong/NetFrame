package com.seaky.netframe.build;

import com.seaky.netframe.exception.ApiException;
import com.seaky.netframe.exception.ExceptionEngine;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Http请求回调
 * 可扩展，在请求之前和返回之后，做统一的事情
 *
 * Created by Seaky
 */

public abstract class HttpObserver<T> implements Observer<T> {

    /**
     * 业务模块构建回调函数的时候
     * 需要重写的两个函数
     * onFailure 本次请求失败
     * onSuccess 本次请求成功
     * onStart 请求开始之前调用，不强制重写，仅业务方需要用到的时候使用
     *          例如弹个菊花？
     */
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
    public void onNext(@NonNull T t) {
        onSuccess(t);
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        onStart();
    }

    @Override
    public void onComplete() {

    }
}
