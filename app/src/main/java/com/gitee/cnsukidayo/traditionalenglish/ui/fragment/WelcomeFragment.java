package com.gitee.cnsukidayo.traditionalenglish.ui.fragment;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
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
import com.gitee.cnsukidayo.traditionalenglish.ui.MainActivity;
import com.gitee.cnsukidayo.traditionalenglish.utils.UserUtils;

/**
 * 首次安装软件的欢迎界面
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener {

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

    private void initView() {
        SpannableString spannableString = new SpannableString("如果我是陈奕迅");

        ClickableSpan clickableSpan = new ClickableSpan() {



            @Override
            public void onClick(View widget) {
                int i = 0;
                if (widget instanceof TextView) {
                    Log.d("message", "onClick");
                }
            }

            @Override

            public void updateDrawState(TextPaint ds) {

                ds.setUnderlineText(false);
            }

        };

        spannableString.setSpan(clickableSpan, 4, 7, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        welcomeMessage.setMovementMethod(LinkMovementMethod.getInstance());
        welcomeMessage.setHighlightColor(getResources().getColor(android.R.color.transparent,null));
        welcomeMessage.setText(spannableString);
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