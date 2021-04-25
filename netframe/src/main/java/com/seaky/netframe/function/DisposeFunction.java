package com.seaky.netframe.function;

import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import io.reactivex.Observable;
import io.reactivex.ObservableConverter;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 参考了AutoDispose的源码
 * 实现轻量级非侵入式的订阅和组件之间的生命周期自动绑定和解绑
 *
 *  Created by Seaky
 */

public class DisposeFunction<T> implements ObservableConverter<T, Observable<T>> {

    /**
     * mLifecycleOwner 当前生命周期对象的引用
     * 在AndroidX，新api中的Activity和Fragment都实现了LifecycleOwner接口
     * 直接传activity/fragment就行
     * 如果你的项目还没有升级到androidX（还是早点升级sdk，迟早要升的）
     * 需要在项目的BaseActivity/BaseFragment中去实现LifecycleOwner接口
     * 如果请求是在逻辑层发生的，例如在Adapter中，拿不到当前页面引用的情况
     * 那么传一个请求发生关联的view引用，比如是点击发送请求，就传具体点击事件的那个view
     * 通过模拟view的状态变化同步生命周期
     */
    private LifecycleOwner mLifecycleOwner;
    //订阅容器
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public DisposeFunction(@androidx.annotation.NonNull LifecycleOwner owner) {
        mLifecycleOwner = owner;
        initLifecycle();
    }

    public DisposeFunction(@androidx.annotation.NonNull View view) {
        mLifecycleOwner = new ViewLifecycleOwner(view);
        initLifecycle();
    }


    private void initLifecycle() {
        mLifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@androidx.annotation.NonNull LifecycleOwner source, @androidx.annotation.NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    //生命周期对象被销毁时，解除掉所有订阅关系
                    mLifecycleOwner.getLifecycle().removeObserver(this);
                    mLifecycleOwner = null;
                    if (mDisposable != null) {
                        mDisposable.dispose();
                    }
                }
            }
        });
    }


    @NonNull
    @Override
    public Observable<T> apply(@NonNull Observable<T> upstream) {
        return upstream.filter(this::canCallback).doOnSubscribe(disposable -> {
            if (mDisposable != null) {
                mDisposable.add(disposable);
            }
        });
    }


    private boolean canCallback(T t) {
        if (mLifecycleOwner == null || mDisposable == null) {
            return false;
        }
        if (mDisposable.isDisposed()) {
            return false;
        }
        if (mLifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            return false;
        }
        return true;
    }


    private static class ViewLifecycleOwner implements LifecycleOwner {
        private final LifecycleRegistry lifecycleRegistry;

        private ViewLifecycleOwner(View view) {
            lifecycleRegistry = new LifecycleRegistry(this);
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
                    view.removeOnAttachStateChangeListener(this);
                }
            });
        }

        @androidx.annotation.NonNull
        @Override
        public Lifecycle getLifecycle() {
            return lifecycleRegistry;
        }
    }
}
