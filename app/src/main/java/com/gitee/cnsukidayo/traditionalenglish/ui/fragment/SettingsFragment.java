package com.gitee.cnsukidayo.traditionalenglish.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.gitee.cnsukidayo.traditionalenglish.R;

/**
 * @author sukidayo
 * @date 2023/2/2 20:00
 */
public class SettingsFragment extends Fragment {

    private View rootView;

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
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        bindView();
        initView();
        return rootView;
    }

    private void bindView() {

    }

    private void initView() {

    }


}