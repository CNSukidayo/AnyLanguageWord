package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.pathsystem.document.SystemFilePath;
import com.gitee.cnsukidayo.anylanguageword.entity.Comment;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.test.BeanTest;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.CommentRecyclerViewAdapter;
import com.gitee.cnsukidayo.anylanguageword.utils.FileUtils;
import com.google.android.material.appbar.AppBarLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.noties.markwon.Markwon;

/*
 * @author sukidayo
 * @date 2023/2/12 15:19:39
 */
public class PostFragment extends Fragment implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener, View.OnScrollChangeListener {

    private View rootView;
    private Handler updateUIHandler;
    private ProgressBar loadingBar;
    private ImageButton backToTrace;
    private String markdownOrigin;
    private AppBarLayout appBarLayout;
    private RelativeLayout title;
    private LinearLayout jumpComment;
    private RelativeLayout commentOrderLayout;
    private final int[] locationOnScreen = new int[2];
    private volatile boolean isLoadMore;
    private RecyclerView commentRecyclerView;
    private CommentRecyclerViewAdapter commentAdapter;

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
        // ?????????????????????
        this.title.setVisibility(View.GONE);
        showMarkDown();
        return rootView;
    }

    private void showMarkDown() {
        this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        StaticFactory.getExecutorService().submit(() -> {
            try {
                markdownOrigin = FileUtils.readWithExternal(SystemFilePath.USER_AGREEMENT.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Markwon markwon = StaticFactory.getGlobalMarkwon(getContext());
            final Spanned spanned = markwon.toMarkdown(markdownOrigin);
            // ???????????????
            this.commentAdapter = new CommentRecyclerViewAdapter(getContext());
            commentAdapter.setMarkDownText(spanned);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateUIHandler.post(() -> {
                commentRecyclerView.setAdapter(commentAdapter);
                loadingBar.setVisibility(View.GONE);
            });
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_to_trace:
                Navigation.findNavController(getView()).popBackStack();
                break;
            case R.id.fragment_post_jump_comment:
                appBarLayout.setExpanded(false, false);
                this.commentOrderLayout = commentAdapter.getCommentOrderLayout();
                commentOrderLayout.getLocationOnScreen(locationOnScreen);//?????????View????????????????????????
                int dy = locationOnScreen[1];
                commentRecyclerView.getLocationOnScreen(locationOnScreen);
                int distance = dy - locationOnScreen[1];
                if (distance > 0) {
                    commentRecyclerView.fling(0, distance);
                    commentRecyclerView.smoothScrollBy(0, distance);
                }
                break;
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        // ?????????????????????????????????????????????????????????
        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
            this.title.setVisibility(View.VISIBLE);
        } else {
            this.title.setVisibility(View.GONE);
        }
    }

    int count = 0;

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        int offset = commentRecyclerView.computeVerticalScrollOffset();  //??????????????????range???????????????
        int extent = commentRecyclerView.computeVerticalScrollExtent();// ????????????????????????
        int range = commentRecyclerView.computeVerticalScrollRange();//????????????????????????????????????range???
        Log.d("message-offset", String.valueOf(offset));
        Log.d("message-range", String.valueOf(range - extent));
        int percentage = (int) (100.0 * offset / (float) (range - extent));
        // ??????????????????,??????????????????+???????????????-??????????????????<2000?????????????????????(????????????????????????????????????)
        if (!isLoadMore && percentage > 90) {
            StaticFactory.getExecutorService().submit(() -> {
                isLoadMore = true;
                // ???????????????????????????,?????????????????????UI
                List<Comment> list = new ArrayList<>(6);
                for (int i = 0; i < 6; i++) {
                    Comment comment = BeanTest.createComment(getContext());
                    list.add(comment);
                }
                count += 10;
                if (count <= 100) {
                    updateUIHandler.post(() -> commentAdapter.addAll(list));
                }

                isLoadMore = false;
            });

        }
    }

    private void bindView() {
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.loadingBar = rootView.findViewById(R.id.fragment_post_loading_bar);
        this.appBarLayout = rootView.findViewById(R.id.fragment_post_app_bar_layout);
        this.title = rootView.findViewById(R.id.fragment_post_title);
        this.jumpComment = rootView.findViewById(R.id.fragment_post_jump_comment);
        this.commentRecyclerView = rootView.findViewById(R.id.fragment_post_comment_recycler_view);

        this.jumpComment.setOnClickListener(this);
        this.appBarLayout.addOnOffsetChangedListener(this);
        this.backToTrace.setOnClickListener(this);
        this.commentRecyclerView.setOnScrollChangeListener(this);
    }
}