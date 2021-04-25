package com.seaky.netframe.core;

import com.seaky.netframe.bean.DemoBean;
import com.seaky.netframe.build.HttpResponse;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    public static final String QUERY_DEMO = "api/v2/post/5e777432b8ea09cade05263f";

    @GET(QUERY_DEMO)
    Observable<HttpResponse<DemoBean>> getDemo();


    //下载文件
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);
}
