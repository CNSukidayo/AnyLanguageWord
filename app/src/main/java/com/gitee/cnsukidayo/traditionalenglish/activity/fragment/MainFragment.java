package com.gitee.cnsukidayo.traditionalenglish.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.activity.MainActivity;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

public class MainFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private View rootView;
    private DrawerLayout drawerLayout;
    private NavController navController;
    private NavigationView drawerNavigationView;
    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
        }
        // todo 状态栏反色
        ((MainActivity)(rootView.getContext())).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        // 该页面用于操作抽屉布局 获取到抽屉布局
        this.drawerLayout = rootView.findViewById(R.id.fragment_main_drawer_layout);
        this.drawerNavigationView = rootView.findViewById(R.id.fragment_main_navigation_view_drawer);
        this.navController = Navigation.findNavController(rootView.findViewById(R.id.fragment_main_fragment));
        drawerNavigationView.setNavigationItemSelectedListener(this);
        //
        View menuView = drawerNavigationView.getChildAt(0);
        if (menuView instanceof NavigationMenuView) {
            menuView.setVerticalScrollBarEnabled(false);
        }
        return rootView;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // 通过这种方式切换fragment
        switch (item.getItemId()) {
            case R.id.fragment_main_drawer_i_start:
                this.navController.navigate(R.id.navigation_i_start);
                break;
        }
        return false;
    }


}