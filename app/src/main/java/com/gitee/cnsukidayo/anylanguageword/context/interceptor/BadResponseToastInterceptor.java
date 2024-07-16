package com.gitee.cnsukidayo.anylanguageword.context.interceptor;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import io.github.cnsukidayo.wword.common.request.RequestHandler;
import io.github.cnsukidayo.wword.model.support.BaseResponse;
import io.github.cnsukidayo.wword.model.vo.ErrorVo;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 如果出现错误,默认会弹出一个toast
 *
 * @author cnsukidayo
 * @date 2023/9/3 16:30
 */
public class BadResponseToastInterceptor implements Interceptor {

    private Gson gson;

    private final Context mContext;

    private Handler updateUIHandler;

    public BadResponseToastInterceptor(Gson gson, Context context) {
        this.gson = gson;
        this.mContext = context;
        this.updateUIHandler = new Handler();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 获取当前的请求体
        Request request = chain.request();
        Response response = chain.proceed(request);
        // 如果出现异常,终止操作,下面这段代码会最后执行.
        String body = null;
        ResponseBody responseBody = response.body();
        try {
            body = getResponseBody(responseBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(!responseBody.contentType().equals(RequestHandler.APPLICATION_JSON_VALUE)){
            return response;
        }
        // 执行到此方法表明外层肯定没有出现错误,但是内层可能出现错误
        BaseResponse baseResponse = gson.fromJson(body, BaseResponse.class);
        if (!baseResponse.getStatus().equals(200)) {
            // 如果有异常则将其转为ErrorVO,判断status的值是多少
            Type type = new TypeToken<BaseResponse<ErrorVo>>() {
            }.getType();
            BaseResponse<ErrorVo> errorVo = gson.fromJson(body, type);
            // 将message值进行回显
            updateUIHandler.post(() -> Toast.makeText(mContext, errorVo.getData().getMessage(), Toast.LENGTH_SHORT).show());
        }
        return response;
    }

    private String getResponseBody(ResponseBody responseBody) throws Exception {
        // todo 封装
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
