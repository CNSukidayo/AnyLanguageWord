package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.StartViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class IStartFragment extends Fragment {

    private View rootView;
    private TextView title;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private List<Fragment> listFragment;
    private String[] pageTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_i_start, container, false);
        bindView();
        initView();
        return rootView;
    }

    private void initView() {
        this.pageTitle = new String[4];
        this.pageTitle[0] = getContext().getResources().getString(R.string.post);
        this.pageTitle[1] = getContext().getResources().getString(R.string.word_divide);
        this.pageTitle[2] = getContext().getResources().getString(R.string.word_star);
        this.pageTitle[3] = getContext().getResources().getString(R.string.hearing);
        this.title.setText(R.string.i_start);
        this.listFragment = new ArrayList<>();
        listFragment.add(new HearingFragment());
        listFragment.add(new AnalysisFragment());
        listFragment.add(new AnalysisFragment());
        listFragment.add(new AnalysisFragment());
        StartViewAdapter startViewAdapter = new StartViewAdapter(getChildFragmentManager(), listFragment);
        viewPager.setAdapter(startViewAdapter);
        slidingTabLayout.setViewPager(viewPager, pageTitle);
    }

    private void bindView() {
        this.viewPager = rootView.findViewById(R.id.fragment_i_start_viewpage);
        this.slidingTabLayout = rootView.findViewById(R.id.slide);
        this.title = rootView.findViewById(R.id.toolbar_title);
    }
}