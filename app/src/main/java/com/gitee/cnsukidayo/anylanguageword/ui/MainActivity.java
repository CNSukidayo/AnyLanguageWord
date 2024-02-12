package com.gitee.cnsukidayo.anylanguageword.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.AnyLanguageWordProperties;
import com.gitee.cnsukidayo.anylanguageword.context.UserSettings;
import com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document.UserInfoPath;
import com.gitee.cnsukidayo.anylanguageword.utils.JsonUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private UserSettings userSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        // 如果当前还没有同意用户协议则跳转到用户协议界面
        changeFragment();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Fragment fragment = getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof KeyEvent.Callback) {
            return ((KeyEvent.Callback) fragment).onKeyUp(keyCode, event);
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 方便每个fragment设置自已的键盘弹起规则
     *
     * @param mode {@link WindowManager.LayoutParams#softInputMode
     *             WindowManager.LayoutParams.softInputMode
     */
    public void setFragmentWindowSoftInputMode(int mode) {
        getWindow().setSoftInputMode(mode);
    }

    private void changeFragment() {
        if (!userSettings.isAcceptUserAgreement()) {
            // 还没有同意用户协议跳转到用户协议界面
            Navigation.findNavController(this.findViewById(R.id.fragment_main_adapter_viewpager)).navigate(R.id.action_navigation_main_to_navigation_welcome);
        }
    }


}