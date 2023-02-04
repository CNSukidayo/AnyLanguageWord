package com.gitee.cnsukidayo.traditionalenglish.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.ui.adapter.listener.NavigationItemSelectListener;

/**
 * @author sukidayo
 * @date Wednesday, February 01, 2023
 */
public class AnalysisFragment extends Fragment implements NavigationItemSelectListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onClickCurrentPage(@NonNull MenuItem item) {

    }
}