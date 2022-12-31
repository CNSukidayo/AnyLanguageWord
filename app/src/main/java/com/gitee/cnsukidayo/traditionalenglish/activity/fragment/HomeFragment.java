package com.gitee.cnsukidayo.traditionalenglish.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.activity.MainActivity;

public class HomeFragment extends Fragment implements View.OnClickListener, DrawerLayout.DrawerListener {

    private View rootView;
    private ImageButton popDrawerLayout;
    private DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }
        this.popDrawerLayout = rootView.findViewById(R.id.fragment_home_user_face);
        this.drawerLayout = ((MainActivity) rootView.getContext()).findViewById(R.id.fragment_main_drawer_layout);
        this.popDrawerLayout.setOnClickListener(this);
        // 禁止左滑出现
        drawerLayout.addDrawerListener(this);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
}