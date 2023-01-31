package com.gitee.cnsukidayo.traditionalenglish.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.enums.MeaningCategory;
import com.gitee.cnsukidayo.traditionalenglish.ui.adapter.customview.RoundImageView;

import java.util.ArrayList;
import java.util.List;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    // 用于存储所有所有的element
    private List<RecyclerViewHolder> cacheElement = new ArrayList<>(MeaningCategory.values().length);

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
        if (position % 3 == 0) {
            holder.postCover.setImageResource(R.drawable.post_cover0);
        } else if (position % 3 == 1) {
            holder.postCover.setImageResource(R.drawable.post_cover1);
        } else {
            holder.postCover.setImageResource(R.drawable.post_cover2);
        }
    }

    @Override
    public int getItemCount() {
        return 15;
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private RoundImageView postCover;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.postCover = this.itemView.findViewById(R.id.fragment_home_post_cover_element_cover);
        }
    }

}
