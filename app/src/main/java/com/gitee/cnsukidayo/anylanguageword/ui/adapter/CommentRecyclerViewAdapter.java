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
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        boolean old = false;
        if (position > allComments.size() - 1) {
            // 新的View
            Comment comment = new Comment();
            int face = random.nextInt(5);
            if (face == 0) {
                comment.setFace(R.drawable.ikun0);
            } else if (face == 1) {
                comment.setFace(R.drawable.ikun1);
            } else if (face == 2) {
                comment.setFace(R.drawable.ikun2);
            } else if (face == 3) {
                comment.setFace(R.drawable.ikun3);
            } else {
                comment.setFace(R.drawable.ikun4);
            }
            int name = random.nextInt(4);
            if (name == 0) {
                comment.setName("一个真正的Man");
            } else if (name == 1) {
                comment.setName("香精煎鱼");
            } else if (name == 2) {
                comment.setName("油饼食不食?");
            } else {
                comment.setName("是故意的还是不小心的?");
            }
            comment.setLevel(random.nextInt(6));
            comment.setPraise(random.nextInt(8000));
            int commentContext = random.nextInt(3);
            if (commentContext == 0) {
                comment.setCommentContext(context.getResources().getString(R.string.comment0));
            } else if (commentContext == 1) {
                comment.setCommentContext(context.getResources().getString(R.string.comment1));
            } else {
                comment.setCommentContext(context.getResources().getString(R.string.comment2));
            }
            allComments.add(comment);
        } else {
            old = true;
        }
        Comment comment = allComments.get(position);
        holder.commentAuthorFace.setImageResource(comment.getFace());
        holder.name.setText(comment.getName());
        holder.level.setText(String.valueOf(comment.getLevel()));
        holder.comment.setText(comment.getCommentContext());
        holder.praise.setText(String.valueOf(comment.getPraise()));
        if (old) {
            holder.updateStatus();
        }
    }


    @Override
    public int getItemCount() {
        return 30;
    }

    @Override
    public void addItem(Comment item) {

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
