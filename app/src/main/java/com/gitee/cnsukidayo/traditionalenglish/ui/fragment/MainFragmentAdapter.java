package com.gitee.cnsukidayo.traditionalenglish.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.ui.adapter.BottomViewAdapter;
import com.gitee.cnsukidayo.traditionalenglish.ui.adapter.listener.NavigationItemSelectListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainFragmentAdapter extends Fragment implements NavigationBarView.OnItemSelectedListener {

    private View rootView;
    private BottomNavigationView viewPageChangeNavigationView;
    private ViewPager2 viewPager;
    private MenuItem nowSelectMenuItem;
    private ArrayList<Fragment> listFragment;
    private volatile int position = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_main_adapter, container, false);
        this.viewPageChangeNavigationView = rootView.findViewById(R.id.fragment_home_navigation_view);
        this.viewPager = rootView.findViewById(R.id.fragment_main_adapter_viewpager);
        initViewPage();
        return rootView;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int position = 0;
        switch (item.getItemId()) {
            case R.id.fragment_main_bottom_recite:
                position = 1;
                break;
        }
        viewPager.setCurrentItem(position);
        // 如果当前点击的目标页面就是当前页面则触发回调事件
        if (this.position == position) {
            ((NavigationItemSelectListener) listFragment.get(position)).onClickCurrentPage(item);
        }
        this.position = position;
        return true;
    }

    /**
     * 初始化ViewPage,实现滑动切换的功能
     */
    private void initViewPage() {
        this.listFragment = new ArrayList<>();
        listFragment.add(new HomeFragment());
        listFragment.add(new CreditFragment());
        BottomViewAdapter adapter = new BottomViewAdapter(getChildFragmentManager(), getLifecycle(), listFragment);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
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
        this.viewPageChangeNavigationView.setOnItemSelectedListener(this);
    }

}