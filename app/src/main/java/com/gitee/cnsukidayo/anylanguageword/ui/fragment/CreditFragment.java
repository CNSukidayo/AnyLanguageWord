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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document.UserInfoPath;
import com.gitee.cnsukidayo.anylanguageword.entity.UserCreditStyle;
import com.gitee.cnsukidayo.anylanguageword.entity.waper.UserCreditStyleWrapper;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.ui.MainActivity;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.NavigationItemSelectListener;
import com.gitee.cnsukidayo.anylanguageword.utils.JsonUtils;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import io.github.cnsukidayo.wword.model.dto.LanguageClassDTO;

public class CreditFragment extends Fragment implements View.OnClickListener, NavigationItemSelectListener {

    private View rootView;
    private BottomNavigationView viewPageChangeNavigationView;
    private TextView startLearning;
    private ProgressBar loadingBar;
    private boolean isLoading;
    private UserCreditStyle userCreditStyle;
    private Handler updateUIHandler = new Handler();
    private FragmentManager fragmentManager;

    /**
     * 语种的fragment
     */
    private LanguageClassFragment languageClassFragment;

    /**
     * 单词划分的fragment
     */
    private DivideFragment divideFragment;

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
//        this.addToPlaneList = rootView.findViewById(R.id.fragment_credit_add_to_plane_view);
        this.viewPageChangeNavigationView = ((MainActivity) rootView.getContext()).findViewById(R.id.fragment_home_navigation_view);
        this.startLearning = rootView.findViewById(R.id.fragment_credit_start_credit);
        this.loadingBar = rootView.findViewById(R.id.credit_fragment_loading_bar);
        // 设置fragment切换逻辑 语种与划分之间的切换显示
        languageClassRecyclerView();
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
                        bundle.putParcelable("userCreditStyleWrapper", userCreditStyleWrapper);
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
        // todo 添加滑动到顶部的方法
//        addToPlaneList.smoothScrollToPosition(RecyclerView.SCROLLBAR_POSITION_DEFAULT);
    }

    /**
     * 语种
     */
    private void languageClassRecyclerView() {
        this.fragmentManager = getChildFragmentManager();
        // 开启事务，获得FragmentTransaction对象
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 向容器内添加或替换碎片,默认情况下为LanguageClassFragment
        // 点击语种后发送请求,调用divideRecyclerViewLanguageClassFragment
        languageClassFragment = Optional.ofNullable(languageClassFragment).orElse(new LanguageClassFragment());
        transaction.replace(R.id.fragment_credit_frame_layout, languageClassFragment);
        // 设置点击了某个语种后的回调事件
        languageClassFragment.setRecycleViewItemClickCallBack(this::divideRecyclerView);
        // 提交事务
        transaction.commit();
    }


    /**
     * 单词划分的recycleView
     *
     * @param languageClassDTO 展示哪个语种
     */
    private void divideRecyclerView(LanguageClassDTO languageClassDTO) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        divideFragment = Optional.ofNullable(divideFragment).orElse(new DivideFragment());
        transaction.replace(R.id.fragment_credit_frame_layout, divideFragment);
        divideFragment.setLanguageClassDTO(languageClassDTO);
        // 记录划分ID
        Set<Long> divideIdSet = new HashSet<>();
        // 设置点击某个子划分后的回调事件
        divideFragment.setRecycleViewItemOnClickListener(divideDTO -> {
            if (divideIdSet.contains(divideDTO.getId())) {
                divideIdSet.remove(divideDTO.getId());
            } else {
                divideIdSet.add(divideDTO.getId());
            }
            if (divideIdSet.size() < 1) {
                viewPageChangeNavigationView.removeBadge(R.id.fragment_main_bottom_recite);
            } else {
                viewPageChangeNavigationView.getOrCreateBadge(R.id.fragment_main_bottom_recite).setNumber(divideIdSet.size());
                viewPageChangeNavigationView.getOrCreateBadge(R.id.fragment_main_bottom_recite).setBadgeGravity(BadgeDrawable.TOP_END);
                viewPageChangeNavigationView.getOrCreateBadge(R.id.fragment_main_bottom_recite).setMaxCharacterCount(3);
            }
        });
        // 提交事务
        transaction.commit();
    }
}