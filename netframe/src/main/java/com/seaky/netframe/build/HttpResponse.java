package com.seaky.netframe.build;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import io.reactivex.annotations.NonNull;

/**
 * Http请求响应的基础封装
 * 格式为
 * {
 *     status : 100
 *     data : 接口返回数据
 * }
 *
 * 如果接口返回数据格式和字段名不一样，在这里改
 *
 *  Created by Seaky
 */

public class HttpResponse<T> {

    @Expose
    private int status;

    @Expose
    private T data;


    public @NonNull
    String toString() {
        return "{\"status\": " + status + ",\"data\":" + new Gson().toJson(data) + "}";
    }

    public int getCode() {
        return status;
    }

    public void setCode(int code) {
        this.status = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
