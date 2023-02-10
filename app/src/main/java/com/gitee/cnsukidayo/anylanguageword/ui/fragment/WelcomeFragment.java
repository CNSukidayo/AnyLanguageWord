package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.UserSettings;
import com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document.SystemFilePath;
import com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document.UserInfoPath;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.ui.MainActivity;
import com.gitee.cnsukidayo.anylanguageword.utils.FileUtils;
import com.gitee.cnsukidayo.anylanguageword.utils.JsonUtils;

import java.io.IOException;

import io.noties.markwon.Markwon;

/**
 * 首次安装软件的欢迎界面
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener, KeyEvent.Callback {

    private View rootView;
    private MainActivity mainActivity;
    private Button disAgree, accept;
    private TextView welcomeMessage;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mainActivity = (MainActivity) view.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        this.rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        bindView();
        initView();
        return rootView;
    }

    private void bindView() {
        disAgree = rootView.findViewById(R.id.fragment_welcome_disagree);
        accept = rootView.findViewById(R.id.fragment_welcome_accept);
        this.welcomeMessage = rootView.findViewById(R.id.welcome_message);
        disAgree.setOnClickListener(this);
        accept.setOnClickListener(this);
    }

    String message = null;

    private void initView() {
        try {
            message = FileUtils.readWithExternal(SystemFilePath.WELCOME_MESSAGE.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Markwon markwon = StaticFactory.getGlobalMarkwon(getContext());
        markwon.setMarkdown(welcomeMessage, message);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_welcome_disagree:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.fragment_welcome_accept:
                try {
                    UserSettings userSettings = JsonUtils.readJson(UserInfoPath.USER_SETTINGS.getPath(), UserSettings.class);
                    userSettings.setAcceptUserAgreement(true);
                    JsonUtils.writeJson(UserInfoPath.USER_SETTINGS.getPath(),userSettings);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Navigation.findNavController(getView()).popBackStack();
                break;
        }
    }

    /**
     * 重写监听事件,如果在WelcomeFragment页面按下返回键则直接退出程序.
     *
     * @param keyCode 按键代码
     * @param event   事件
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        return true;
    }

    // ----下面是一些用不到的方法----
    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }


}