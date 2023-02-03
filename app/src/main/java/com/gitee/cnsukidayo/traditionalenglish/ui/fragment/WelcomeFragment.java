package com.gitee.cnsukidayo.traditionalenglish.ui.fragment;

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

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.factory.StaticFactory;
import com.gitee.cnsukidayo.traditionalenglish.ui.MainActivity;
import com.gitee.cnsukidayo.traditionalenglish.utils.UserUtils;

/**
 * 首次安装软件的欢迎界面
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private MainActivity mainActivity;
    private Button disAgree, accept;
    private TextView agreement, userPolicy;

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
        return rootView;
    }

    private void bindView() {
        disAgree = rootView.findViewById(R.id.fragment_welcome_disagree);
        accept = rootView.findViewById(R.id.fragment_welcome_accept);
        agreement = rootView.findViewById(R.id.fragment_welcome_user_agreement);
        userPolicy = rootView.findViewById(R.id.fragment_welcome_userPolicy);
        disAgree.setOnClickListener(this);
        accept.setOnClickListener(this);
        agreement.setOnClickListener(this);
        userPolicy.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_welcome_disagree:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.fragment_welcome_accept:
                UserUtils.getUserSettings().setAcceptUserAgreement(true);
                UserUtils.upDateUserSettings();
                Navigation.findNavController(getView()).popBackStack();
                break;
            case R.id.fragment_welcome_user_agreement:
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_welcome_to_navigation_user_agreement, null, StaticFactory.getSimpleNavOptions());
                break;
            case R.id.fragment_welcome_userPolicy:
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_welcome_to_navigation_privacy_policy, null, StaticFactory.getSimpleNavOptions());
                break;
        }
    }

    /**
     * 重写监听事件,如果在WelcomeFragment页面按下返回键则直接退出程序.
     *
     * @param keyCode 按键代码
     * @param event   事件
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        return true;
    }


}