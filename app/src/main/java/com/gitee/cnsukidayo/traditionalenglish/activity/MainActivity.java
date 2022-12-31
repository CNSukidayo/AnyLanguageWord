package com.gitee.cnsukidayo.traditionalenglish.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.view.Window;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.activity.fragment.DetailFragment;
import com.gitee.cnsukidayo.traditionalenglish.activity.fragment.MainFragment;
import com.gitee.cnsukidayo.traditionalenglish.activity.fragment.WelcomeFragment;
import com.gitee.cnsukidayo.traditionalenglish.context.TraditionalEnglishProperties;
import com.gitee.cnsukidayo.traditionalenglish.context.UserSettings;
import com.gitee.cnsukidayo.traditionalenglish.factory.StaticFactory;
import com.gitee.cnsukidayo.traditionalenglish.utils.UserUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private UserSettings userSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // 状态栏反色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        // 初始化外部存储路径
        TraditionalEnglishProperties.setExternalFilesDir(getExternalFilesDir(""));
        this.userSettings = UserUtils.getUserSettings();
        // 根据状态创建fragment
        createFragment();
    }

    private void createFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (userSettings.isAcceptUserAgreement()) {
            transaction.replace(R.id.mainFrameLayout, new MainFragment());
        } else {
            transaction.replace(R.id.mainFrameLayout, new WelcomeFragment());
        }
        transaction.commit();
    }


}