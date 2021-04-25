package com.seaky.netframe.build;

import java.io.IOException;

import io.reactivex.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *  自定义拦截器
 *  可在这里添加各种拦截器拦截请求和返回
 *
 *  Created by Seaky
 */

public class HttpInterceptor {

    //请求之前添加统一的header
    public static Interceptor headerInterceptor() {
        return new Interceptor() {
            @Override
            public @NonNull
            Response intercept(@NonNull Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                // builder.addHeader("Connection","close");
                return chain.proceed(builder.build());
            }
        };
    }


}
