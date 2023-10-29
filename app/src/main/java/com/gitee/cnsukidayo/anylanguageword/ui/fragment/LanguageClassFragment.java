package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.LanguageClassAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.RecycleViewItemClickCallBack;

import java.util.List;

import io.github.cnsukidayo.wword.common.request.factory.CoreServiceRequestFactory;
import io.github.cnsukidayo.wword.common.request.interfaces.core.DivideRequest;
import io.github.cnsukidayo.wword.model.dto.LanguageClassDTO;

/**
 * 语种类fragment
 */
public class LanguageClassFragment extends Fragment {

    private View rootView;
    private RecyclerView languageClassRecyclerView;
    private GridLayoutManager languageClassLayoutManager;
    private LanguageClassAdapter languageClassAdapter;
    private final Handler updateUIHandler = new Handler();
    private RecycleViewItemClickCallBack<LanguageClassDTO> recycleViewItemClickCallBack;

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
     * 设置点击了某个语种后的回调事件,需要传递LanguageClassDTO对象
     *
     * @param recycleViewItemClickCallBack 参数不为null
     */
    public void setRecycleViewItemClickCallBack(RecycleViewItemClickCallBack<LanguageClassDTO> recycleViewItemClickCallBack) {
        this.recycleViewItemClickCallBack = recycleViewItemClickCallBack;
    }


    private void bindView() {
        languageClassRecyclerView = rootView.findViewById(R.id.single_recycler_view);
    }

    private void initView() {
        this.languageClassLayoutManager = new GridLayoutManager(getContext(), 3);
        this.languageClassRecyclerView.setLayoutManager(languageClassLayoutManager);
        this.languageClassAdapter = new LanguageClassAdapter(getContext());

        this.languageClassRecyclerView.setAdapter(languageClassAdapter);
        this.languageClassAdapter.setRecycleViewItemClickCallBack(recycleViewItemClickCallBack);
    }

    /**
     * 请求数据,显示当前所有语种
     */
    private void requestData() {
        DivideRequest divideRequest = CoreServiceRequestFactory.getInstance().divideRequest();
        StaticFactory.getExecutorService().execute(() -> divideRequest.listLanguage()
                .success(data -> {
                    List<LanguageClassDTO> languageClassList = data.getData();
                    updateUIHandler.post(() -> languageClassAdapter.addAll(languageClassList));
                })
                .execute());
    }
}