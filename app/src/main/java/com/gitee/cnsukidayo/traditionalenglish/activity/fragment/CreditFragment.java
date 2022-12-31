package com.gitee.cnsukidayo.traditionalenglish.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.activity.MainActivity;
import com.gitee.cnsukidayo.traditionalenglish.activity.adapter.CreditAddToPlaneListAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CreditFragment extends Fragment {

    private View rootView;
    private RecyclerView addToPlaneList;
    private BottomNavigationView viewPageChangeNavigationView;
    private int count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_credit, container, false);
        }
        this.addToPlaneList = rootView.findViewById(R.id.fragment_credit_add_to_plane_view);
        this.viewPageChangeNavigationView = ((MainActivity) rootView.getContext()).findViewById(R.id.fragment_home_navigation_view);
        // todo 更改底部颜色
        // 设置RecyclerView
        initRecyclerView();

        return rootView;
    }


    private void initRecyclerView() {
        this.addToPlaneList.setLayoutManager(new LinearLayoutManager(getContext()));
        CreditAddToPlaneListAdapter addToPlaneListAdapter = new CreditAddToPlaneListAdapter(getContext());
        boolean[] choose = new boolean[15];
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