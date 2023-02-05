package com.gitee.cnsukidayo.traditionalenglish.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.ui.adapter.listener.NavigationItemSelectListener;

/**
 * @author sukidayo
 * @date Saturday, February 04, 2023
 */
public class HearingFragment extends Fragment implements NavigationItemSelectListener {


    private View rootView;
    private TextView spannable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_hearing, container, false);
        bindView();
        return rootView;
    }

    @Override
    public void onClickCurrentPage(@NonNull MenuItem item) {

    }

    private void bindView() {
        this.spannable = rootView.findViewById(R.id.spannable);

        SpannableString spannableString = new SpannableString("如果我是陈奕迅");

        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.GREEN);

        spannableString.setSpan(foregroundColorSpan, 4, 7, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


    }


}