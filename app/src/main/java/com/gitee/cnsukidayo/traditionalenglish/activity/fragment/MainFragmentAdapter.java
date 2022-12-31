package com.gitee.cnsukidayo.traditionalenglish.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.activity.adapter.BottomViewAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentAdapter extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    private View rootView;
    private BottomNavigationView viewPageChangeNavigationView;
    private ViewPager2 viewPager;
    private MenuItem nowSelectMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main_adapter, container, false);
        }
        this.viewPageChangeNavigationView = rootView.findViewById(R.id.fragment_home_navigation_view);
        this.viewPager = rootView.findViewById(R.id.fragment_main_adapter_viewpager);
        initViewPage();
        return rootView;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_main_bottom_main:
                viewPager.setCurrentItem(0);
                break;
            case R.id.fragment_main_bottom_recite:
                viewPager.setCurrentItem(1);
                break;
        }
        return true;
    }

    /**
     * 初始化ViewPage,实现滑动切换的功能
     */
    private void initViewPage() {
        List<Fragment> list_fragment = new ArrayList<>();
        list_fragment.add(new HomeFragment());
        list_fragment.add(new CreditFragment());
        BottomViewAdapter adapter = new BottomViewAdapter(this, list_fragment);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setSaveEnabled(false);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (nowSelectMenuItem != null) {
                    nowSelectMenuItem.setChecked(false);
                } else {
                    viewPageChangeNavigationView.getMenu().getItem(0).setChecked(false);
                }
                nowSelectMenuItem = viewPageChangeNavigationView.getMenu().getItem(position);
                nowSelectMenuItem.setChecked(true);
            }
        });
        viewPager.beginFakeDrag();
        if (viewPager.fakeDragBy(100f)) {
            viewPager.endFakeDrag();
        }
        this.viewPageChangeNavigationView.setOnNavigationItemSelectedListener(this);
    }
}