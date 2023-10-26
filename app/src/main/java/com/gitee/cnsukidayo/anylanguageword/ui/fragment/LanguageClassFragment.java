package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.LanguageClassAdapter;

import io.github.cnsukidayo.wword.model.dto.LanguageClassDTO;

public class LanguageClassFragment extends Fragment {

    private View rootView;
    private RecyclerView divideListRecyclerView;
    private GridLayoutManager divideListLayoutManager;
    private LanguageClassAdapter languageClassAdapter;

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
        rootView = inflater.inflate(R.layout.fragment_language_class, container, false);
        bindView();
        initView();
        requestData();
        return rootView;
    }

    private void bindView() {
        divideListRecyclerView = rootView.findViewById(R.id.fragment_divide_word_list_recycler_view);
    }

    private void initView() {
        this.divideListLayoutManager = new GridLayoutManager(getContext(), 3);
        this.divideListRecyclerView.setLayoutManager(divideListLayoutManager);
        this.languageClassAdapter = new LanguageClassAdapter(getContext());

        this.divideListRecyclerView.setAdapter(languageClassAdapter);
    }

    /**
     * 请求数据
     */
    private void requestData() {
        LanguageClassDTO languageClassDTO = new LanguageClassDTO();
        languageClassDTO.setLanguage("中文");
        languageClassAdapter.addItem(languageClassDTO);
    }
}