package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.entity.PostCover;
import com.gitee.cnsukidayo.anylanguageword.enums.MeaningCategory;
import com.gitee.cnsukidayo.anylanguageword.handler.HomeMessageStreamHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.RecyclerViewAdapterItemChange;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.customview.RoundImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.RecyclerViewHolder> implements RecyclerViewAdapterItemChange<PostCover> {

    private Context context;
    // 用于存储所有所有的element
    private List<RecyclerViewHolder> cacheElement = new ArrayList<>(MeaningCategory.values().length);

    // 处理主页所有信息流的Handler
    private HomeMessageStreamHandler homeMessageStreamHandler;

    public PostRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_home_post_cover_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PostCover postCover = homeMessageStreamHandler.getPostCoverByPosition(position);
        holder.title.setText(postCover.getTitle());
        holder.userName.setText(postCover.getUserName());
        // 点赞数显示处理
        float praiseCount = postCover.getPraiseCount();
        String praiseCountText = String.valueOf(praiseCount);
        if (praiseCount >= 1000 && praiseCount < 10000) {
            praiseCountText = String.format("%.1f", praiseCount / 1000) + context.getResources().getString(R.string.thousand);
        } else if (praiseCount >= 10000) {
            praiseCountText = String.format("%.1f", praiseCount / 10000) + context.getResources().getString(R.string.ten_thousand);
        }
        holder.praiseCount.setText(praiseCountText);
        if (position % 4 == 0) {
            holder.postCover.setImageResource(R.drawable.post_cover0);
        } else if (position % 3 == 1) {
            holder.postCover.setImageResource(R.drawable.post_cover1);
        } else if (position % 3 == 2) {
            holder.postCover.setImageResource(R.drawable.post_cover2);
        } else {
            holder.postCover.setImageResource(R.drawable.post_cover3);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return homeMessageStreamHandler.getPostSize();
    }

    @Override
    public void addItem(PostCover item) {
        homeMessageStreamHandler.addPostCover(item);
        this.notifyItemInserted(homeMessageStreamHandler.getPostSize() - 1);
    }

    @Override
    public void addAll(PostCover... items) {

    }

    @Override
    public void addAll(Collection<PostCover> postCovers) {
        for (PostCover postCover : postCovers) {
            addItem(postCover);
        }
    }

    @Override
    public void removeItem(PostCover item) {

    }

    public void setHomeMessageStreamHandler(HomeMessageStreamHandler homeMessageStreamHandler) {
        this.homeMessageStreamHandler = homeMessageStreamHandler;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private RoundImageView postCover;
        private TextView title, userName, praiseCount;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.postCover = this.itemView.findViewById(R.id.fragment_home_post_cover_element_cover);
            this.title = this.itemView.findViewById(R.id.fragment_home_post_cover_element_title);
            this.userName = this.itemView.findViewById(R.id.fragment_home_post_cover_element_author_name);
            this.praiseCount = this.itemView.findViewById(R.id.fragment_home_post_cover_element_praise_count);
        }
    }

}
