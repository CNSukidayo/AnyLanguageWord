package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.entity.Comment;
import com.gitee.cnsukidayo.anylanguageword.handler.RecyclerViewAdapterItemChange;
import com.gitee.cnsukidayo.anylanguageword.test.BeanTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.RecyclerViewHolder> implements RecyclerViewAdapterItemChange<Comment> {

    private final Context context;
    private final List<Comment> allComments = new ArrayList<>(5);
    private final Handler updateUIHandler;
    private Random random = new Random();

    public CommentRecyclerViewAdapter(Context context) {
        this.context = context;
        updateUIHandler = new Handler(context.getMainLooper());
        allComments.add(BeanTest.createComment(context));
        allComments.add(BeanTest.createComment(context));
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Comment comment = allComments.get(position);
        holder.commentAuthorFace.setImageResource(comment.getFace());
        holder.name.setText(comment.getName());
        holder.level.setText(String.valueOf(comment.getLevel()));
        holder.comment.setText(comment.getCommentContext());
        holder.praise.setText(String.valueOf(comment.getPraise()));
        if (position < allComments.size()) {
//            holder.updateStatus();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return allComments.size();
    }

    @Override
    public void addItem(Comment item) {
        allComments.add(item);
        notifyItemInserted(allComments.size() - 1);
    }

    @Override
    public void removeItem(Comment item) {

    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements Runnable, View.OnClickListener {
        private View itemView;
        private ImageFilterView commentAuthorFace;
        private TextView name, level, date, comment, open, praise;
        private int originLines, maxLines;
        // 展开的状态,一开始是false.也就是说一开始是折叠的状态
        private boolean openStatus = false;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.commentAuthorFace = itemView.findViewById(R.id.comment_element_face);
            this.name = itemView.findViewById(R.id.comment_element_name);
            this.level = itemView.findViewById(R.id.comment_element_level);
            this.comment = itemView.findViewById(R.id.comment_element_context);
            this.open = itemView.findViewById(R.id.comment_element_open);
            this.praise = itemView.findViewById(R.id.comment_element_praise);
            updateUIHandler.post(this);
            this.open.setOnClickListener(this);
        }

        @Override
        public void run() {
            originLines = comment.getLineCount();
            maxLines = comment.getMaxLines();
            if (originLines <= maxLines) {
                open.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.comment_element_open:
                    openStatus = !openStatus;
                    updateStatus();
                    break;
            }
        }

        public void updateStatus() {
            if (originLines <= maxLines) {
                open.setVisibility(View.GONE);
            } else {
                open.setVisibility(View.VISIBLE);
            }
            if (openStatus) {
                open.setText(R.string.fold);
                comment.setMaxLines(originLines);
            } else {
                open.setText(R.string.open);
                comment.setMaxLines(maxLines);
            }
        }

    }


}
