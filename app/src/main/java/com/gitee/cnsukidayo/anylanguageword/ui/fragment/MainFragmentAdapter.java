package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.enums.UserLevel;
import com.gitee.cnsukidayo.anylanguageword.enums.VIPLevel;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.BottomViewAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.NavigationItemSelectListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import io.github.cnsukidayo.wword.common.request.RequestRegister;
import io.github.cnsukidayo.wword.common.request.implement.core.UserRequestUtil;
import io.github.cnsukidayo.wword.model.dto.UserProfileDTO;

public class MainFragmentAdapter extends Fragment implements NavigationBarView.OnItemSelectedListener, DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, View.OnLongClickListener {

    private View rootView, headerLayout;
    private BottomNavigationView viewPageChangeNavigationView;
    private ViewPager2 viewPager;
    private MenuItem nowSelectMenuItem;
    private ArrayList<Fragment> listFragment;
    private volatile int position = 0;
    private NavigationView drawerNavigationView;
    private ImageFilterView drawerUserFace;
    private TextView userName, userLevel, userVipLevel, userMoney;
    private LinearLayout settings;
    private DrawerLayout drawerLayout;
    private final Fragment homeFragment = new HomeFragment(), creditFragment = new CreditFragment(), hearingFragment = new HearingFragment(), analysisFragment = new AnalysisFragment();
    private BottomNavigationItemView bottomHome, bottomRecite, bottomHearing, bottomAnalysis;
    private RelativeLayout userInfoArea;
    private final Handler updateUIHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            initView();
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_main_adapter, container, false);
        bindView();
        initView();
        initViewPage();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_main_navigation_header_settings:
                Navigation.findNavController(getView()).navigate(R.id.action_main_navigation_to_settings, null, StaticFactory.getSimpleNavOptions());
                break;
            case R.id.fragment_main_navigation_header_face:
                // TODO 如果当前用户已经登陆了,则跳转到用户个人主页,否则跳转到登陆界面
                Navigation.findNavController(getView()).navigate(R.id.action_main_navigation_to_navigation_login, null, StaticFactory.getSimpleNavOptions());
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getGroupId() == R.id.fragment_main_bottom_group) {
            int position = 0;
            switch (item.getItemId()) {
                case R.id.fragment_main_bottom_recite:
                    position = 1;
                    break;
                case R.id.fragment_main_bottom_hearing:
                    position = 2;
                    break;
                case R.id.fragment_main_bottom_analysis:
                    position = 3;
                    break;
            }
            viewPager.setCurrentItem(position, false);
            // 如果当前点击的目标页面就是当前页面则触发回调事件
            if (this.position == position) {
                ((NavigationItemSelectListener) listFragment.get(position)).onClickCurrentPage(item);
            }
            this.position = position;
        } else if (item.getGroupId() == R.id.fragment_main_drawer_group) {
            switch (item.getItemId()) {
                case R.id.fragment_main_drawer_i_start:
                    Navigation.findNavController(getView()).navigate(R.id.action_navigation_main_to_navigation_i_start, null, StaticFactory.getSimpleNavOptions());
                    break;
                case R.id.fragment_main_drawer_post:
                    Navigation.findNavController(getView()).navigate(R.id.action_main_navigation_to_navigation_post, null, StaticFactory.getSimpleNavOptions());
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_main_bottom_recite:
                Navigation.findNavController(getView()).navigate(R.id.action_main_navigation_to_navigation_search_word, null, StaticFactory.getSimpleNavOptions());
                break;
        }
        return true;
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    public void onClickUserFace(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void initView() {
        // 进入主页时加载用户个人信息
        if (RequestRegister.getAuthToken().getAccessToken() != null) {
            userInfoArea.setVisibility(View.VISIBLE);
            StaticFactory.getExecutorService().execute(() -> UserRequestUtil.getProfile()
                    .success(data -> {
                        UserProfileDTO userProfile = data.getData();
                        updateUIHandler.post(() -> {
                            userName.setText(userProfile.getNick());
                            userLevel.setText(getResources().getString(UserLevel.values()[userProfile.getLevel() - 1].getLevelDescribe()));
                            userVipLevel.setText(getResources().getString(VIPLevel.values()[userProfile.getLevel() - 1].getVipDescribe()));
                        });
                    })
                    .execute());
        } else {
            userInfoArea.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 初始化ViewPage,实现滑动切换的功能
     */
    private void initViewPage() {
        this.listFragment = new ArrayList<>(4);
        listFragment.add(homeFragment);
        listFragment.add(creditFragment);
        listFragment.add(hearingFragment);
        listFragment.add(analysisFragment);
        BottomViewAdapter adapter = new BottomViewAdapter(getChildFragmentManager(), getLifecycle(), listFragment);
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

    private void bindView() {
        this.viewPageChangeNavigationView = rootView.findViewById(R.id.fragment_home_navigation_view);
        this.viewPager = rootView.findViewById(R.id.fragment_main_adapter_viewpager);
        this.drawerNavigationView = rootView.findViewById(R.id.fragment_word_credit_view_drawer);
        this.headerLayout = drawerNavigationView.getHeaderView(0);
        this.drawerUserFace = headerLayout.findViewById(R.id.fragment_main_navigation_header_face);
        this.userName = headerLayout.findViewById(R.id.fragment_main_navigation_header_user_name);
        this.userLevel = headerLayout.findViewById(R.id.fragment_main_navigation_header_user_level);
        this.userVipLevel = headerLayout.findViewById(R.id.fragment_main_navigation_header_vip_level);
        this.userMoney = headerLayout.findViewById(R.id.fragment_main_navigation_header_money);
        this.drawerLayout = rootView.findViewById(R.id.fragment_main_drawer_layout);
        this.settings = rootView.findViewById(R.id.fragment_main_navigation_header_settings);
        this.bottomHome = this.viewPageChangeNavigationView.findViewById(R.id.fragment_main_bottom_main);
        this.bottomRecite = this.viewPageChangeNavigationView.findViewById(R.id.fragment_main_bottom_recite);
        this.bottomHearing = this.viewPageChangeNavigationView.findViewById(R.id.fragment_main_bottom_hearing);
        this.bottomAnalysis = this.viewPageChangeNavigationView.findViewById(R.id.fragment_main_bottom_analysis);
        this.userInfoArea = headerLayout.findViewById(R.id.fragment_main_navigation_hear_user_info);

        ((HomeFragment) homeFragment).setPopDrawerListener(this::onClickUserFace);
        drawerLayout.addDrawerListener(this);
        this.drawerNavigationView.setNavigationItemSelectedListener(this);
        this.settings.setOnClickListener(this);
        this.bottomHome.setOnLongClickListener(this);
        this.bottomRecite.setOnLongClickListener(this);
        this.bottomHearing.setOnLongClickListener(this);
        this.bottomAnalysis.setOnLongClickListener(this);
        this.drawerUserFace.setOnClickListener(this);
        // 禁止左滑出现
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }
}