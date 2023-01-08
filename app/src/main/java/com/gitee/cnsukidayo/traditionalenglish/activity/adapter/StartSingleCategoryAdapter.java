package com.gitee.cnsukidayo.traditionalenglish.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.util.function.Consumer;

/**
 * @author cnsukidayo
 * @date 2023/1/7 17:35
 */
public class StartSingleCategoryAdapter extends RecyclerView.Adapter<StartSingleCategoryAdapter.RecyclerViewHolder> implements MoveAndSwipedListener {

    private Context context;
    // 用于存储所有所有的element
    private List<StartSingleCategoryAdapter.RecyclerViewHolder> cacheElement = new ArrayList<>(15);
    // 用于回调startDrag方法的接口,该接口耦合了,定义方式不好.
    private Consumer<RecyclerView.ViewHolder> startDragListener;

    private boolean isFirst = true;

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
        if (position == getItemCount() - 1) {
            isFirst = false;
        }
    }

    @Override
    public int getItemCount() {
        if (isFirst) {
            return 15;
        }
        return cacheElement.size();
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

    public void setStartDragListener(Consumer<RecyclerView.ViewHolder> startDragListener) {
        this.startDragListener = startDragListener;
    }

    protected class RecyclerViewHolder extends RecyclerView.ViewHolder implements StateChangedListener, View.OnTouchListener, View.OnClickListener {
        public View itemView;
        public TextView title, detail, edit, delete;
        public LinearLayout categoryLinearLayout;
        public ImageButton openList, move;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.title = itemView.findViewById(R.id.fragment_word_credit_start_single_category_title);
            this.detail = itemView.findViewById(R.id.fragment_word_credit_start_single_category_detail);
            this.categoryLinearLayout = itemView.findViewById(R.id.fragment_word_credit_start_single_category_linear_layout);
            this.openList = itemView.findViewById(R.id.fragment_word_credit_start_open_list);
            this.edit = itemView.findViewById(R.id.fragment_word_credit_start_edit);
            this.delete = itemView.findViewById(R.id.fragment_word_credit_start_delete);
            this.move = itemView.findViewById(R.id.fragment_word_credit_start_move);
            this.move.setOnTouchListener(this);
            this.delete.setOnClickListener(this);
        }

        @Override
        public void onItemSelected() {
            categoryLinearLayout.setAlpha(0.5f);
        }

        @Override
        public void onItemClear() {
            categoryLinearLayout.setAlpha(1.0f);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.fragment_word_credit_start_move:
                    if (event.getAction() == MotionEvent.ACTION_DOWN && startDragListener != null) {
                        startDragListener.accept(this);
                    }
                    break;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fragment_word_credit_start_delete:
                    // 删除当前Item
                    cacheElement.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    break;
                case R.id.fragment_word_credit_start_edit:
                    break;
            }
        }
    }

}
