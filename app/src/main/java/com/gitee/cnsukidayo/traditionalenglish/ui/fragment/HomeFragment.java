package com.gitee.cnsukidayo.traditionalenglish.ui.fragment;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.entity.PostCover;
import com.gitee.cnsukidayo.traditionalenglish.factory.StaticFactory;
import com.gitee.cnsukidayo.traditionalenglish.handler.HomeMessageStreamHandler;
import com.gitee.cnsukidayo.traditionalenglish.test.BeanTest;
import com.gitee.cnsukidayo.traditionalenglish.ui.MainActivity;
import com.gitee.cnsukidayo.traditionalenglish.ui.adapter.HomePictureViewAdapter;
import com.gitee.cnsukidayo.traditionalenglish.ui.adapter.PostRecyclerViewAdapter;
import com.gitee.cnsukidayo.traditionalenglish.ui.adapter.listener.NavigationItemSelectListener;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment implements View.OnClickListener, DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, View.OnScrollChangeListener,
        NavigationItemSelectListener {

    private MainActivity mainActivity;
    private View rootView;
    private ImageButton popDrawerLayoutButton;
    private DrawerLayout drawerLayout;
    private NavigationView drawerNavigationView;
    private NavController navController;
    private SwipeRefreshLayout downRefreshLayout;
    private Handler updateUIHandler;
    private RecyclerView postRecyclerView;
    private ViewPager2 imageRotationViewPager;
    private NestedScrollView nestedScrollView;
    private volatile int preImageRotationPosition;
    private LinearLayout imageRotationContainer;
    // 用户是否正在滑动图片的标识
    private volatile boolean userSlideImage;
    private volatile boolean isLoadMore;

    private StaggeredGridLayoutManager postRecyclerViewLayoutManager;
    private HomeMessageStreamHandler homeMessageStreamHandler;
    private PostRecyclerViewAdapter postRecyclerViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }
        updateUIHandler = new Handler();
        mainActivity = (MainActivity) rootView.getContext();
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

    @SuppressLint("RestrictedApi")
    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        // 解决上划冲突
        if (!downRefreshLayout.isRefreshing()) {
            if (scrollY - oldScrollY > 0) {
                downRefreshLayout.setEnabled(false);
            }
            downRefreshLayout.setEnabled(true);
        }
        // 下滑加载更多,当滑动的距离+组件的高度-可滑动的范围<2000时加载新的数据(防止网络卡顿的预加载方案)
        if (!isLoadMore && nestedScrollView.computeVerticalScrollRange() - (nestedScrollView.getScrollY() + nestedScrollView.getHeight()) < 1000) {
            StaticFactory.getExecutorService().submit(() -> {
                isLoadMore = true;
                // 先加载好所有的数据,然后再统一更新UI
                List<PostCover> list = new ArrayList<>(6);
                for (int i = 0; i < 6; i++) {
                    PostCover postCover = BeanTest.createPostCover();
                    list.add(postCover);
                }
                updateUIHandler.post(() -> postRecyclerViewAdapter.addAll(list));
                isLoadMore = false;
            });
        }
    }

    @Override
    public void onRefresh() {
        StaticFactory.getExecutorService().submit(() -> {
            // 先执行网络请求,请求完成后统一更新UI
            homeMessageStreamHandler.refresh();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 更新UI
            updateUIHandler.post(() -> {
                postRecyclerViewAdapter.notifyDataSetChanged();
                downRefreshLayout.setRefreshing(false);
            });
        });
    }


    @Override
    public void onClickCurrentPage(@NonNull MenuItem item) {
        nestedScrollView.smoothScrollTo(nestedScrollView.getScrollX(), 0);
        downRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    /**
     * 初始化所有帖子,耗时方法放到线程池中执行,UI到时候统一更新.
     */
    private void initPost() {
        this.postRecyclerViewLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        this.postRecyclerView.setLayoutManager(postRecyclerViewLayoutManager);
        this.postRecyclerViewAdapter = new PostRecyclerViewAdapter(getContext());
        this.homeMessageStreamHandler = StaticFactory.getHomeMessageStreamHandler();
        homeMessageStreamHandler.refresh();
        postRecyclerViewAdapter.setHomeMessageStreamHandler(homeMessageStreamHandler);
        this.postRecyclerView.setAdapter(postRecyclerViewAdapter);
        this.postRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int distance = getResources().getDimensionPixelSize(R.dimen.fragment_home_post_distance);
                outRect.set(distance, distance, distance, distance);
            }
        });
        this.imageRotationViewPager.setAdapter(new HomePictureViewAdapter(getContext()));
        this.imageRotationViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                View viewOval = imageRotationContainer.getChildAt(preImageRotationPosition);
                viewOval.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray, null)));
                viewOval.setAlpha(0.5f);
                viewOval = imageRotationContainer.getChildAt(position);
                viewOval.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
                viewOval.setAlpha(1.0f);
                preImageRotationPosition = position;
                // 如果当前用户正在滑动图片,那么让图片自动轮播在一段时间内失效,虽然说下面的setCurrentItem方法也会当前监听时间,但是确保下面的方法调用userSlideImage = false;
                userSlideImage = true;
            }
        });
        // 实现图片定时轮转 todo 实际上做起来挺麻烦的,目前这里只是做一个样式
        StaticFactory.getExecutorService().submit(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!userSlideImage) {
                    int tempPosition = preImageRotationPosition;
                    tempPosition++;
                    if (tempPosition == 3) {
                        tempPosition = 0;
                    }
                    imageRotationViewPager.setCurrentItem(tempPosition);
                }
                userSlideImage = false;
            }
        });
    }

    private void bindView() {
        this.popDrawerLayoutButton = rootView.findViewById(R.id.fragment_home_user_face);
        this.drawerLayout = mainActivity.findViewById(R.id.fragment_main_drawer_layout);
        this.drawerNavigationView = mainActivity.findViewById(R.id.fragment_word_credit_view_drawer);
        this.downRefreshLayout = rootView.findViewById(R.id.fragment_home_swipe_refresh);
        this.navController = Navigation.findNavController(drawerLayout);
        this.postRecyclerView = rootView.findViewById(R.id.fragment_home_post_recycler_view);
        this.imageRotationViewPager = rootView.findViewById(R.id.fragment_home_picture_rotation);
        this.nestedScrollView = rootView.findViewById(R.id.fragment_home_nested_scroll_view);
        this.imageRotationContainer = rootView.findViewById(R.id.fragment_home_picture_rotation_oval_container);

        this.popDrawerLayoutButton.setOnClickListener(this);
        this.drawerNavigationView.setNavigationItemSelectedListener(this);
        this.nestedScrollView.setOnScrollChangeListener(this);
        drawerLayout.addDrawerListener(this);

        // 禁止左滑出现
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        downRefreshLayout.setSize(CircularProgressDrawable.LARGE);
        downRefreshLayout.setColorSchemeResources(R.color.theme_color);
        downRefreshLayout.setOnRefreshListener(this);
    }
}