package com.gitee.cnsukidayo.anylanguageword.context.interceptor;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import io.github.cnsukidayo.wword.common.request.ResponseWrapper;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @author cnsukidayo
 * @date 2024/2/12 13:09
 */
public class HttpLogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        // 获取方法的调用栈
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            // 如果调用的栈是从ResponseWrapper得到的则打印它的上一个调用
            try {
                if (Class.forName(stackTrace[i].getClassName()).isAssignableFrom(ResponseWrapper.class)) {
                    Log.d("httpLog-caller", stackTrace[i + 1].getClassName());
                    break;
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        // 获取当前的请求体
        Request request = chain.request();
        Log.d("httpLog-Request", request.toString());
        Response response = chain.proceed(request);
        try {
            Log.d("httpLog-Response", getResponseBody(response.body()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    private String getResponseBody(ResponseBody responseBody) throws Exception {
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE);
        Buffer buffer = source.getBuffer();

        Charset charset = StandardCharsets.UTF_8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(StandardCharsets.UTF_8);
            } catch (UnsupportedCharsetException e) {
                e.printStackTrace();
            }
        }

        if (responseBody.contentLength() != 0) {
            return buffer.clone().readString(charset);
        }
        return null;
    }


}
