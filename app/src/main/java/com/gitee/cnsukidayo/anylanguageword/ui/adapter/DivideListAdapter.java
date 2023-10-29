package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.handler.RecyclerViewAdapterItemChange;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.divide.ChildDivideListAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.RecycleViewItemClickCallBack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.cnsukidayo.wword.model.dto.DivideDTO;

public class DivideListAdapter extends RecyclerView.Adapter<DivideListAdapter.RecyclerViewHolder>
        implements RecyclerViewAdapterItemChange<DivideDTO> {

    private final Context context;
    private final List<DivideDTO> allDivideDTOList = new ArrayList<>();
    /**
     * 记录被点击的划分id
     */
    private final Set<Long> divideIdSet = new HashSet<>();

    /**
     * 设置点击子划分的回调事件
     */
    private RecycleViewItemClickCallBack<DivideDTO> recycleViewItemOnClickListener;

    public DivideListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.credit_fragment_add_to_plane_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 首先得到position对应的父划分
        DivideDTO divideDTO = allDivideDTOList.get(position);
        if (divideIdSet.contains(divideDTO.getId())) {
            holder.addToPlane.setRotation(180);
            holder.childDivideListAdapter.replaceAll(divideDTO.getChildDivideDTO());
            holder.childDivideListRecyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.addToPlane.setRotation(270);
            holder.childDivideListRecyclerView.setVisibility(View.GONE);
        }
        holder.divideTextView.setText(divideDTO.getName());

    }

    @Override
    public int getItemCount() {
        return allDivideDTOList.size();
    }

    public void setRecycleViewItemOnClickListener(RecycleViewItemClickCallBack<DivideDTO> recycleViewItemOnClickListener) {
        this.recycleViewItemOnClickListener = recycleViewItemOnClickListener;
    }

    @Override
    public void addItem(DivideDTO item) {
        allDivideDTOList.add(item);
        notifyItemInserted(allDivideDTOList.size() - 1);
    }

    @Override
    public void removeItem(DivideDTO item) {

    }

    @Override
    public void addAll(Collection<DivideDTO> divideDTOS) {
        for (DivideDTO divideDTO : divideDTOS) {
            addItem(divideDTO);
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView divideTextView;
        private ImageButton addToPlane;
        private RelativeLayout relativeLayout;
        /**
         * 子划分的RecyclerView
         */
        private RecyclerView childDivideListRecyclerView;
        /**
         * 子划分的adapter
         */
        private ChildDivideListAdapter childDivideListAdapter;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.divideTextView = itemView.findViewById(R.id.credit_fragment_divide_textview);
            this.addToPlane = itemView.findViewById(R.id.credit_fragment_divide_image_button);
            this.relativeLayout = itemView.findViewById(R.id.parent_divide_relative_layout);
            this.childDivideListRecyclerView = itemView.findViewById(R.id.child_divide_recycler_view);

            this.childDivideListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            this.childDivideListAdapter = new ChildDivideListAdapter(context);
            this.childDivideListRecyclerView.setAdapter(childDivideListAdapter);
            this.childDivideListRecyclerView.setVisibility(View.GONE);
            this.childDivideListAdapter.setRecycleViewItemOnClickListener(recycleViewItemOnClickListener);

            this.relativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            DivideDTO divideDTO = allDivideDTOList.get(position);
            if (divideIdSet.contains(divideDTO.getId())) {
                divideIdSet.remove(divideDTO.getId());
            } else {
                divideIdSet.add(divideDTO.getId());
            }
            if (divideIdSet.contains(divideDTO.getId())) {
                addToPlane.setRotation(180);
                // 得到当前父划分下的所有子划分
                this.childDivideListAdapter.replaceAll(divideDTO.getChildDivideDTO());
                this.childDivideListRecyclerView.setVisibility(View.VISIBLE);
            } else {
                addToPlane.setRotation(270);
                this.childDivideListRecyclerView.setVisibility(View.GONE);
            }
            // 父划分被点击不需要回调
        }
    }


}
