package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.entity.Comment;
import com.gitee.cnsukidayo.anylanguageword.test.BeanTest;

import java.util.ArrayList;
import java.util.List;

public class Doc_Fragment extends Fragment {

    private View rootView;
    private RecyclerView commentRecyclerView;
    private CommentRecyclerViewAdapter commentAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.doc_fragment, container, false);
        this.commentRecyclerView = rootView.findViewById(R.id.fragment_post_comment_recycler_view);
        this.commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.commentAdapter = new CommentRecyclerViewAdapter(getContext());
        this.commentRecyclerView.setAdapter(commentAdapter);
        return rootView;
    }

    public void add() {
        List<Comment> list = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            Comment comment = BeanTest.createComment(getContext());
            list.add(comment);
        }
        for (Comment comment : list) {
            commentAdapter.addItem(comment);
        }
    }

}
