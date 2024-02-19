package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.support.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.DivideListAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.RecycleViewItemClickCallBack;

import java.util.List;

import io.github.cnsukidayo.wword.common.request.factory.AuthServiceRequestFactory;
import io.github.cnsukidayo.wword.common.request.factory.CoreServiceRequestFactory;
import io.github.cnsukidayo.wword.common.request.interfaces.auth.UserRequest;
import io.github.cnsukidayo.wword.common.request.interfaces.core.DivideRequest;
import io.github.cnsukidayo.wword.model.dto.DivideDTO;
import io.github.cnsukidayo.wword.model.dto.LanguageClassDTO;
import io.github.cnsukidayo.wword.model.dto.UserProfileDTO;

/**
 * 显示单词划分的Fragment
 */
public class DivideFragment extends Fragment {

    private View rootView;
    private RecyclerView divideRecyclerView;
    private LinearLayoutManager divideLayoutManager;
    private DivideListAdapter divideListAdapter;
    /**
     * 当前要展示的哪个语种下的所有划分
     */
    private LanguageClassDTO languageClassDTO;

    /**
     * 用于保存当前用户的个人信息
     */
    private UserProfileDTO userProfileDTO;

    /**
     * 更新UI的handler
     */
    private final Handler updateUIHandler = new Handler();

    /**
     * RecycleView回调的事件
     */
    private RecycleViewItemClickCallBack<DivideDTO> recycleViewItemOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_single_recycle_view, container, false);
        bindView();
        initView();
        requestData();
        return rootView;
    }

    /**
     * 设置当前要展示的语种
     *
     * @param languageClassDTO 语种对象不为null
     */
    public void setLanguageClassDTO(LanguageClassDTO languageClassDTO) {
        this.languageClassDTO = languageClassDTO;
    }

    public void setRecycleViewItemOnClickListener(RecycleViewItemClickCallBack<DivideDTO> recycleViewItemOnClickListener) {
        this.recycleViewItemOnClickListener = recycleViewItemOnClickListener;
    }


    private void bindView() {
        this.divideRecyclerView = rootView.findViewById(R.id.single_recycler_view);
    }

    private void initView() {
        this.divideLayoutManager = new LinearLayoutManager(getContext());
        this.divideRecyclerView.setLayoutManager(divideLayoutManager);
        this.divideListAdapter = new DivideListAdapter(getContext());

        this.divideRecyclerView.setAdapter(divideListAdapter);
        this.divideListAdapter.setRecycleViewItemOnClickListener(recycleViewItemOnClickListener);
    }

    private void requestData() {
        // 查询当前用户的所有划分
        UserRequest userRequest = AuthServiceRequestFactory.getInstance().userRequest();
        DivideRequest divideRequest = CoreServiceRequestFactory.getInstance().divideRequest();
        StaticFactory.getExecutorService().execute(() -> {
            // 先获取用户信息
            userRequest.getProfile()
                    .success(data -> userProfileDTO = data.getData())
                    .execute();
            // 获取当前用户的所有划分
            divideRequest.listDivide(String.valueOf(languageClassDTO.getId()), String.valueOf(userProfileDTO.getUuid()))
                    .success(data -> {
                        List<DivideDTO> divideDTOList = data.getData();
                        updateUIHandler.post(() -> divideListAdapter.addAll(divideDTOList));
                    })
                    .execute();
        });
    }

}