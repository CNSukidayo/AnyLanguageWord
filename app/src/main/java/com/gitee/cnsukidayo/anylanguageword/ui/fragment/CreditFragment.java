package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document.UserInfoPath;
import com.gitee.cnsukidayo.anylanguageword.entity.UserCreditStyle;
import com.gitee.cnsukidayo.anylanguageword.entity.waper.UserCreditStyleWrapper;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.ui.MainActivity;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.CreditAddToPlaneListAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.NavigationItemSelectListener;
import com.gitee.cnsukidayo.anylanguageword.utils.JsonUtils;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class CreditFragment extends Fragment implements View.OnClickListener, NavigationItemSelectListener {

    private View rootView;
    private RecyclerView addToPlaneList;
    private BottomNavigationView viewPageChangeNavigationView;
    private int count;
    private TextView startLearning;
    private ProgressBar loadingBar;
    private boolean isLoading;
    private UserCreditStyle userCreditStyle;
    private Handler updateUIHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_credit, container, false);
        // 初始化View
        this.addToPlaneList = rootView.findViewById(R.id.fragment_credit_add_to_plane_view);
        this.viewPageChangeNavigationView = ((MainActivity) rootView.getContext()).findViewById(R.id.fragment_home_navigation_view);
        this.startLearning = rootView.findViewById(R.id.fragment_credit_start_credit);
        this.loadingBar = rootView.findViewById(R.id.credit_fragment_loading_bar);
        // 设置RecyclerView
        initRecyclerView();
        // 设置各种监听事件
        this.startLearning.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_credit_start_credit:
                if (!isLoading) {
                    loadingBar.setVisibility(View.VISIBLE);
                    StaticFactory.getExecutorService().submit(() -> {
                        try {
                            userCreditStyle = JsonUtils.readJson(UserInfoPath.USER_CREDIT_STYLE.getPath(), UserCreditStyle.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // 拷贝Bean
                        UserCreditStyleWrapper userCreditStyleWrapper = new UserCreditStyleWrapper(userCreditStyle);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("userCreditStyleWrapper",userCreditStyleWrapper);
                        updateUIHandler.post(() -> {
                            if (userCreditStyle.isIgnore()) {
                                Navigation.findNavController(getView()).navigate(R.id.action_navigation_main_to_word_credit, bundle,
                                        StaticFactory.getSimpleNavOptions());
                            } else {
                                Navigation.findNavController(getView()).navigate(R.id.action_main_navigation_to_navigation_word_credit_launch, bundle,
                                        StaticFactory.getSimpleNavOptions());
                            }
                            loadingBar.setVisibility(View.INVISIBLE);
                        });
                    });
                }
                break;
        }
    }

    @Override
    public void onClickCurrentPage(@NonNull MenuItem item) {
        addToPlaneList.smoothScrollToPosition(RecyclerView.SCROLLBAR_POSITION_DEFAULT);
    }

    private void initRecyclerView() {
        this.addToPlaneList.setLayoutManager(new LinearLayoutManager(getContext()));
        CreditAddToPlaneListAdapter addToPlaneListAdapter = new CreditAddToPlaneListAdapter(getContext());
        boolean[] choose = new boolean[30];
        addToPlaneListAdapter.setItemOnClickListener(position -> {
            choose[position] = !choose[position];
            if (choose[position]) {
                count++;
            } else {
                count--;
            }
            if (count < 1) {
                viewPageChangeNavigationView.removeBadge(R.id.fragment_main_bottom_recite);
            } else {
                viewPageChangeNavigationView.getOrCreateBadge(R.id.fragment_main_bottom_recite).setNumber(count);
                viewPageChangeNavigationView.getOrCreateBadge(R.id.fragment_main_bottom_recite).setBadgeGravity(BadgeDrawable.TOP_END);
                viewPageChangeNavigationView.getOrCreateBadge(R.id.fragment_main_bottom_recite).setMaxCharacterCount(3);
            }
        });
        this.addToPlaneList.setAdapter(addToPlaneListAdapter);

    }
}