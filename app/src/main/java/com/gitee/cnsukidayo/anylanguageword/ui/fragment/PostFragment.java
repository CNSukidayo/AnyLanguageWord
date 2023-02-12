package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document.SystemFilePath;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.utils.FileUtils;
import com.google.android.material.appbar.AppBarLayout;

import java.io.IOException;

import io.noties.markwon.Markwon;

/*
 * @author sukidayo
 * @date 2023/2/12 15:19:39
 */
public class PostFragment extends Fragment implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    private View rootView;
    private TextView markDownTextView;
    private Handler updateUIHandler;
    private ProgressBar loadingBar;
    private ImageButton backToTrace;
    private String markdownOrigin;
    private AppBarLayout appBarLayout;
    private RelativeLayout title;

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
        rootView = inflater.inflate(R.layout.fragment_post, container, false);
        updateUIHandler = new Handler();
        bindView();
        // 先隐藏标题信息
        this.title.setVisibility(View.GONE);
        showMarkDown();
        return rootView;
    }

    private void showMarkDown() {
        StaticFactory.getExecutorService().submit(() -> {
            try {
                markdownOrigin = FileUtils.readWithExternal(SystemFilePath.USER_AGREEMENT.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Markwon markwon = StaticFactory.getGlobalMarkwon(getContext());
            updateUIHandler.post(() -> {
                markwon.setMarkdown(markDownTextView, markdownOrigin);
                loadingBar.setVisibility(View.GONE);
            });
        });
    }

    @Override
    public void onClick(View v) {
        Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        // 如果这两个值相等则代表当前滑动到底端了
        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
            this.title.setVisibility(View.VISIBLE);
        } else {
            this.title.setVisibility(View.GONE);
        }
    }

    private void bindView() {
        this.markDownTextView = rootView.findViewById(R.id.fragment_post_markdown_context);
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.loadingBar = rootView.findViewById(R.id.fragment_post_loading_bar);
        this.appBarLayout = rootView.findViewById(R.id.fragment_post_app_bar_layout);
        this.title = rootView.findViewById(R.id.fragment_post_title);
        this.appBarLayout.addOnOffsetChangedListener(this);
        this.backToTrace.setOnClickListener(this);
    }
}