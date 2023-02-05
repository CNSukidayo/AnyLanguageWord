package com.gitee.cnsukidayo.anylanguageword.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.AnyLanguageWordProperties;
import com.gitee.cnsukidayo.anylanguageword.context.UserSettings;
import com.gitee.cnsukidayo.anylanguageword.ui.fragment.WelcomeFragment;
import com.gitee.cnsukidayo.anylanguageword.ui.fragment.WordCreditFragment;
import com.gitee.cnsukidayo.anylanguageword.utils.UserUtils;

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
        this.userSettings = UserUtils.getUserSettings();
        // 根据状态创建fragment
        createFragment();
    }

    private void createFragment() {
        if (!userSettings.isAcceptUserAgreement()) {
            Navigation.findNavController(this.findViewById(R.id.fragment_main_adapter_viewpager)).navigate(R.id.action_navigation_main_to_navigation_welcome);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Fragment fragment = getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof WelcomeFragment) {
            return ((WelcomeFragment) fragment).onKeyUp(keyCode, event);
        }
        if (fragment instanceof WordCreditFragment) {
            return ((WordCreditFragment) fragment).onKeyUp(keyCode, event);
        }
        return super.onKeyUp(keyCode, event);
    }
}