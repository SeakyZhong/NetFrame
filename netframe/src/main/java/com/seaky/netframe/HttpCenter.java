package com.seaky.netframe;

import androidx.lifecycle.LifecycleOwner;

import com.seaky.netframe.build.DownloadInfo;
import com.seaky.netframe.build.DownloadObserver;
import com.seaky.netframe.build.HttpObserver;
import com.seaky.netframe.build.HttpResponse;
import com.seaky.netframe.core.ApiService;
import com.seaky.netframe.core.HttpFactory;

import io.reactivex.Observable;

/**
 *  Api入口
 *
 *  用法 ：
 *       HttpCenter.send(HttpCenter.getApi(ApiService.class).getDemo(),new HttpObserver<DemoBean>(){
 *           void onFailure(ApiException e);
 *           void onSuccess(T t);
 *       });
 *
 *  接口和接口返回数据已和框架解耦
 *  如果项目的接口很多，就各自业务模块或组件维护自己的ApiService和对应的Bean
 *  如果不是很庞大，也可以直接把ApiService定义在框架里集中维护。
 *  自行取舍
 *
 *  Created by Seaky
 */

public class HttpCenter {


    public static <T> T getApi(Class<T> api) {
        return HttpFactory.getInstance().create(api);
    }


    public static <T> void send(Observable<HttpResponse<T>> observable, LifecycleOwner lifecycleOwner, HttpObserver<T> observer) {
        HttpFactory.getInstance().request(observable,lifecycleOwner,observer);
    }

    public static <T> void send(Observable<HttpResponse<T>> observable, HttpObserver<T> observer) {
        HttpFactory.getInstance().request(observable,observer);
    }

    public static void download(String url, String savePath, String fileName, DownloadObserver<DownloadInfo> observer) {
        HttpFactory.getInstance().download(getApi(ApiService.class).downloadFile(url),url,savePath,fileName,observer);
    }
}
