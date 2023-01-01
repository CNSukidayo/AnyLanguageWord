package com.gitee.cnsukidayo.traditionalenglish.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.utils.AnimationUtil;

public class WordCreditFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private ImageButton popMoreFunction;
    private HorizontalScrollView moreFunctionHorizontalScrollView;
    private boolean moreFunctionOpen = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_word_credit, container, false);
        }
        // 获取相关组件
        this.popMoreFunction = rootView.findViewById(R.id.fragment_word_credit_pop_more_function);
        this.moreFunctionHorizontalScrollView = rootView.findViewById(R.id.fragment_word_credit_more_function_horizontal_scroll_view);
        // 设置监听事件
        this.popMoreFunction.setOnClickListener(this);
        // 隐藏不必要的UI
        hideAnswer();
        // 弹出Dialog不要阻塞UI线程,通过一个新的线程去请求所有单词信息.
        AlertDialog loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null))
                .setCancelable(false)
                .show();
        // 通过Bundle得到当前用户选中的单词分类,这里暂时以样本单词进行测试.
        loadingDialog.dismiss();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_word_credit_pop_more_function:
                if (moreFunctionOpen) {
                    AnimationUtil.with().moveToViewBottom(moreFunctionHorizontalScrollView, 500);
                }else {
                    AnimationUtil.with().bottomMoveToViewLocation(moreFunctionHorizontalScrollView, 500);
                }
                moreFunctionOpen = !moreFunctionOpen;
                break;
        }
    }

    /**
     * 隐藏所有暂时不必要出现的UI
     */
    private void hideAnswer() {
        rootView.findViewById(R.id.fragment_word_credit_answer_area).setVisibility(View.GONE);
    }

}