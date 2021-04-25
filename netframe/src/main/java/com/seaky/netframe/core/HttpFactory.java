package com.seaky.netframe.core;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.seaky.netframe.build.DownloadInfo;
import com.seaky.netframe.build.DownloadObserver;
import com.seaky.netframe.build.HttpInterceptor;
import com.seaky.netframe.build.HttpObserver;
import com.seaky.netframe.build.HttpResponse;
import com.seaky.netframe.function.DisposeFunction;
import com.seaky.netframe.function.ErrorFunction;
import com.seaky.netframe.function.ServerResultFunction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  http请求调度中心
 *
 *  Created by Seaky
 */

public class HttpFactory {

    /**
     * 默认app使用的接口都是统一域名。
     * 如果你们公司真的这么坑，不同接口域名都不一样
     * 那么有几套域名就需要维护几个Retrofit对象
     * 共用一个OkHttpClient就行
     */
    public static final String BASE_URL = "https://gank.io/";
    private static volatile HttpFactory mInstance;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;

    public static HttpFactory getInstance() {
        if(null == mInstance) {
            synchronized (HttpFactory.class) {
                if (null == mInstance) {
                    mInstance = new HttpFactory();
                }
            }
        }
        return mInstance;
    }

    private HttpFactory(){
        initOkHttpClient();
    }

    //设置Retrofit
    private Retrofit initRetrofit() {
        if(null != mRetrofit) {
            return mRetrofit;
        }
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(initOkHttpClient())
                .build();
        return mRetrofit;
    }

    //设置okhttp
    private OkHttpClient initOkHttpClient() {
        if(null != mOkHttpClient) {
            return mOkHttpClient;
        }

        mOkHttpClient =  new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .addInterceptor(HttpInterceptor.headerInterceptor())
                .build();
        return mOkHttpClient;
    }


    /**
     * 线程切换封装
     * 即请求过程和请求操作都在io工作线程中执行
     * 订阅后返回操作切换到主线程执行
     */
    private <T> ObservableTransformer<T, T> setThread() {
        return new ObservableTransformer<T, T>() {
            @Override
            public @NonNull
            ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    //获取代理接口实例
    public <T> T create(Class<T> api) {
        return initRetrofit().create(api);
    }

    /**
     * 提交http请求
     * @param observable         接口订阅
     * @param lifecycleOwner     当前生命周期引用
     * @param observer           回调
     */
    @SuppressWarnings("unchecked")
    public <T> void request(@NonNull Observable<HttpResponse<T>> observable, LifecycleOwner lifecycleOwner, HttpObserver<?> observer) {
        observable.map(new ServerResultFunction<>())
                .onErrorResumeNext(new ErrorFunction<>())
                .as(new DisposeFunction<>(lifecycleOwner))
                .compose(setThread())
                .subscribe((Observer<? super Object>) observer);
    }

    /**
     * 提交http请求
     * @param observable         接口订阅
     * @param mView              当前View
     * @param observer           回调
     */
    @SuppressWarnings("unchecked")
    public <T> void request(@NonNull Observable<HttpResponse<T>> observable, View mView, HttpObserver<?> observer) {
        observable.map(new ServerResultFunction<>())
                .onErrorResumeNext(new ErrorFunction<>())
                .as(new DisposeFunction<>(mView))
                .compose(setThread())
                .subscribe((Observer<? super Object>) observer);
    }

    /**
     * 提交http请求
     * 这个函数适用于那种与UI无交互的后台静默请求
     * 比如app请求接口拉一些配置数据
     * 请求一般在Application中使用
     * 注：此函数不能在短生命周期对象中使用，造成内存泄漏风险。
     * @param observable         接口订阅
     * @param observer           回调
     */
    @SuppressWarnings("unchecked")
    public <T> void request(@NonNull Observable<HttpResponse<T>> observable,  HttpObserver<?> observer) {
        observable.map(new ServerResultFunction<>())
                .onErrorResumeNext(new ErrorFunction<>())
                .compose(setThread())
                .subscribe((Observer<? super Object>) observer);
    }


    /**
     * 下载文件
     * 由代码逻辑触发的下载事件
     * 无页面交互 无进度展示
     * 注：此函数不能在短生命周期对象中使用，造成内存泄漏风险。
     * @param url        文件下载地址
     * @param savePath   文件保存路径
     * @param fileName   文件名
     * @param observer   下载回调
     */
    @SuppressLint("CheckResult")
    public void download(Observable<ResponseBody> observable,String url,String savePath, String fileName,DownloadObserver<DownloadInfo> observer) {
        observable.subscribeOn(Schedulers.io())
                .map(responseBody -> {
                    //文件下载地址
                    DownloadInfo downloadInfo = new DownloadInfo();
                    downloadInfo.setUrl(url);

                    //文件总大小
                    long total = responseBody.contentLength();
                    downloadInfo.setTotal(total);

                    //文件绝对路径
                    File file = new File(savePath + File.separator + fileName);
                    downloadInfo.setFilePath(file.getAbsolutePath());

                    if(file.exists()) {
                        file.delete();
                    }

                    InputStream in = responseBody.byteStream();
                    FileOutputStream fos = new FileOutputStream(file,true);
                    byte[] buffer = new byte[2048];
                    int length = 0;
                    while ((length = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, length);
                        // downloadInfo.setProgress(length);
                    }
                    fos.flush();
                    fos.close();
                    in.close();

                    return downloadInfo;
                })
                .onErrorResumeNext(new ErrorFunction<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
