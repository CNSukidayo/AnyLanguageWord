package com.gitee.cnsukidayo.traditionalenglish.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.ui.MainActivity;
import com.gitee.cnsukidayo.traditionalenglish.ui.adapter.PostRecyclerViewAdapter;
import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment implements View.OnClickListener, DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private ImageButton popDrawerLayoutButton;
    private DrawerLayout drawerLayout;
    private NavigationView drawerNavigationView;
    private NavController navController;
    private SwipeRefreshLayout downRefreshLayout;
    private Handler updateUIHandler;
    private RecyclerView postRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        updateUIHandler = new Handler();
        bindView();
        initPost();
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
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_main_to_navigation_i_start);
                break;
        }
        return false;
    }

    /**
     * 初始化所有帖子
     */
    private void initPost() {
        this.postRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        this.postRecyclerView.setAdapter(new PostRecyclerViewAdapter(getContext()));
    }

    private void bindView() {
        this.popDrawerLayoutButton = rootView.findViewById(R.id.fragment_home_user_face);
        this.drawerLayout = ((MainActivity) rootView.getContext()).findViewById(R.id.fragment_main_drawer_layout);
        this.drawerNavigationView = ((MainActivity) rootView.getContext()).findViewById(R.id.fragment_word_credit_view_drawer);
        this.downRefreshLayout = rootView.findViewById(R.id.fragment_home_swipe_refresh);
        this.navController = Navigation.findNavController(drawerLayout);
        this.postRecyclerView = rootView.findViewById(R.id.fragment_home_post_recycler_view);

        this.popDrawerLayoutButton.setOnClickListener(this);
        this.drawerNavigationView.setNavigationItemSelectedListener(this);

        drawerLayout.addDrawerListener(this);

        // 禁止左滑出现
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        downRefreshLayout.setSize(CircularProgressDrawable.LARGE);
        downRefreshLayout.setColorSchemeResources(R.color.theme_color);
        downRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        updateUIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                downRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }
}