package com.gitee.cnsukidayo.anylanguageword.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.AnyLanguageWordProperties;
import com.gitee.cnsukidayo.anylanguageword.context.UserSettings;
import com.gitee.cnsukidayo.anylanguageword.context.interceptor.BadResponseToastInterceptor;
import com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document.UserInfoPath;
import com.gitee.cnsukidayo.anylanguageword.utils.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

import io.github.cnsukidayo.wword.common.request.BadResponseOkHttpInterceptor;
import io.github.cnsukidayo.wword.common.request.OkHttpHostnameVerifier;
import io.github.cnsukidayo.wword.common.request.RequestHandler;
import io.github.cnsukidayo.wword.common.request.RequestRegister;
import io.github.cnsukidayo.wword.common.request.SSLSocketFactoryCreate;
import io.github.cnsukidayo.wword.common.request.TokenCheckOkHttpInterceptor;
import io.github.cnsukidayo.wword.common.request.type.deser.GLocalDateDeSerializer;
import io.github.cnsukidayo.wword.common.request.type.deser.GLocalDateTimeDeSerializer;
import io.github.cnsukidayo.wword.common.request.type.ser.GLocalDateSerializer;
import io.github.cnsukidayo.wword.common.request.type.ser.GLocalDateTimeSerializer;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private UserSettings userSettings;

    // 用该handler来异步读取证书
    private Handler updateUIHandler = new Handler();

    private final String ip = "192.168.31.183";
    private final String port = "8200";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 注册HTTP请求工具,异步执行
        runOnUiThread(this::initOKHttp);
        // 状态栏反色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        // 初始化外部存储路径
        AnyLanguageWordProperties.setExternalFilesDir(getExternalFilesDir(""));
        // 得到用户信息文件
        try {
            userSettings = JsonUtils.readJson(UserInfoPath.USER_SETTINGS.getPath(), UserSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 根据状态创建fragment
        createFragment();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Fragment fragment = getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof KeyEvent.Callback) {
            return ((KeyEvent.Callback) fragment).onKeyUp(keyCode, event);
        }
        return super.onKeyUp(keyCode, event);
    }

    private void createFragment() {
        if (!userSettings.isAcceptUserAgreement()) {
            Navigation.findNavController(this.findViewById(R.id.fragment_main_adapter_viewpager)).navigate(R.id.action_navigation_main_to_navigation_welcome);
        }
    }


    private void initOKHttp() {
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResource("cert/publicKey.cer").openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SSLSocketFactoryCreate sslSocketFactoryCreate = SSLSocketFactoryCreate.newInstance(inputStream);
        Gson gson = new GsonBuilder()
                //LocalDateTime序列化适配器
                .registerTypeAdapter(LocalDateTime.class, new GLocalDateTimeSerializer())
                //LocalDate序列化适配器
                .registerTypeAdapter(LocalDate.class, new GLocalDateSerializer())
                //LocalDateTime反序列化适配器
                .registerTypeAdapter(LocalDateTime.class, new GLocalDateTimeDeSerializer())
                //LocalDate反序列化适配器
                .registerTypeAdapter(LocalDate.class, new GLocalDateDeSerializer())
                .create();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .hostnameVerifier(new OkHttpHostnameVerifier())
                .sslSocketFactory(sslSocketFactoryCreate.getSslSocketFactory(), sslSocketFactoryCreate.getX509TrustManager())
                .addInterceptor(new BadResponseOkHttpInterceptor(gson))
                .addInterceptor(new BadResponseToastInterceptor(gson, this))
                .addInterceptor(new TokenCheckOkHttpInterceptor(gson))
                .build();

        RequestHandler requestHandler = new RequestHandler(okHttpClient, gson, null);
        requestHandler.setBaseUrl("https://" + ip + ":" + port);
        RequestRegister.register(requestHandler);
    }


}