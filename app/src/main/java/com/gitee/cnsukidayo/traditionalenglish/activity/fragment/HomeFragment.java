package com.gitee.cnsukidayo.traditionalenglish.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.activity.MainActivity;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment implements View.OnClickListener, DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener {

    private View rootView;
    private ImageButton popDrawerLayoutButton;
    private DrawerLayout drawerLayout;
    private NavigationView drawerNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        this.popDrawerLayoutButton = rootView.findViewById(R.id.fragment_home_user_face);
        this.drawerLayout = ((MainActivity) rootView.getContext()).findViewById(R.id.fragment_main_drawer_layout);
        this.drawerNavigationView = ((MainActivity) rootView.getContext()).findViewById(R.id.fragment_main_navigation_view_drawer);
        this.popDrawerLayoutButton.setOnClickListener(this);
        this.drawerNavigationView.setNavigationItemSelectedListener(this);

        // 禁止左滑出现
        drawerLayout.addDrawerListener(this);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //todo 这是啥来着?
        View menuView = drawerNavigationView.getChildAt(0);
        if (menuView instanceof NavigationMenuView) {
            menuView.setVerticalScrollBarEnabled(false);
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // 通过这种方式切换fragment
        switch (item.getItemId()) {
            case R.id.fragment_main_drawer_i_start:
                Navigation.findNavController(getView()).navigate(R.id.action_main_navigation_to_navigation_i_start);
                break;
        }
        return false;
    }
}