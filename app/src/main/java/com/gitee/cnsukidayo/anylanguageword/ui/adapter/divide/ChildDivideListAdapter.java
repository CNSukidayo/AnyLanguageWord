package com.gitee.cnsukidayo.anylanguageword.ui.adapter.divide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.handler.RecyclerViewAdapterItemChange;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.RecycleViewItemClickCallBack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.cnsukidayo.wword.model.dto.DivideDTO;

public class ChildDivideListAdapter extends RecyclerView.Adapter<ChildDivideListAdapter.RecyclerViewHolder>
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

    public ChildDivideListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.credit_fragment_child_divide_element, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 首先得到子划分
        DivideDTO divideDTO = allDivideDTOList.get(position);
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

    @Override
    public void removeAll() {
        int count = allDivideDTOList.size();
        allDivideDTOList.clear();
        notifyItemRangeRemoved(0, count);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView divideTextView;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.divideTextView = itemView.findViewById(R.id.credit_fragment_child_divide_textview);
        }

        @Override
        public void onClick(View v) {
        }
    }


}
