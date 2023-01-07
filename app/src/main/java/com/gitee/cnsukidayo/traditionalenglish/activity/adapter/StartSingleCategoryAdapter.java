package com.gitee.cnsukidayo.traditionalenglish.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.activity.adapter.listener.MoveAndSwipedListener;
import com.gitee.cnsukidayo.traditionalenglish.activity.adapter.listener.StateChangedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cnsukidayo
 * @date 2023/1/7 17:35
 */
public class StartSingleCategoryAdapter extends RecyclerView.Adapter<StartSingleCategoryAdapter.RecyclerViewHolder> implements MoveAndSwipedListener {

    private Context context;
    // 用于存储所有所有的element
    private List<StartSingleCategoryAdapter.RecyclerViewHolder> cacheElement = new ArrayList<>(15);

    public StartSingleCategoryAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public StartSingleCategoryAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StartSingleCategoryAdapter.RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_start_single_category, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull StartSingleCategoryAdapter.RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (cacheElement.size() == getItemCount()) {
            return;
        }
        cacheElement.add(holder);
        holder.title.setText(String.valueOf(position));
    }

    public int getSlideLimitation(RecyclerView.ViewHolder viewHolder) {
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        return viewGroup.getChildAt(1).getLayoutParams().width;
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(cacheElement, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        cacheElement.remove(position);
        notifyItemRemoved(position);
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements StateChangedListener {
        public View itemView;
        public TextView title, detail, tv;
        public LinearLayout categoryLinearLayout;
        public ImageView iv;
        float startX;

        @SuppressLint("ClickableViewAccessibility")
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.title = itemView.findViewById(R.id.item);
            this.tv = itemView.findViewById(R.id.tv_text);
            this.iv = itemView.findViewById(R.id.iv_img);
//            this.detail = itemView.findViewById(R.id.fragment_word_credit_start_single_category_detail);
            this.categoryLinearLayout = itemView.findViewById(R.id.fragment_word_credit_start_single_category_linear_layout);
            categoryLinearLayout.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    float x = event.getX();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = x;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int offsetX = (int) (x - startX);
                            itemView.scrollTo(-(int) offsetX, 0);
                            break;
                    }
                    return true;
                }

            });
        }

        @Override
        public void onItemSelected() {
            categoryLinearLayout.setAlpha(0.5f);
        }

        @Override
        public void onItemClear() {
            categoryLinearLayout.setAlpha(1.0f);
        }
    }

}
