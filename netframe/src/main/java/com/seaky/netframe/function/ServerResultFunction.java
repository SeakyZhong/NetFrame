package com.seaky.netframe.function;

import com.seaky.netframe.build.HttpResponse;
import com.seaky.netframe.exception.ServerException;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 *  接口请求结果分离
 *  100为样例接口的请求成功code状态码 按需修改自己接口的成功码
 *
 *  Created by Seaky
 */

public class ServerResultFunction<T> implements Function<HttpResponse<?>,T> {

    @SuppressWarnings("unchecked")
    @Override
    public T apply(@NonNull HttpResponse<?> httpResponse) throws Exception {
        try {
            if(100 == httpResponse.getCode()) {
                return (T)httpResponse.getData();
            } else {
                throw new ServerException(String.valueOf(httpResponse.getCode()),"");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException("-1",e.getMessage());
        }
    }
}
