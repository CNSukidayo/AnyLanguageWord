package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.RecycleViewItemOnClickListener;

import java.util.ArrayList;
import java.util.List;

public class FlagAreaRecyclerViewAdapter extends RecyclerView.Adapter<FlagAreaRecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    // 保存所有的旗帜
    private final List<RecyclerViewHolder> allFlags = new ArrayList<>(FlagColor.values().length);
    private volatile boolean open = true;
    private RecycleViewItemOnClickListener recycleViewItemOnClickListener;

    public FlagAreaRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.sign_flag_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (allFlags.size() != getItemCount()) {
            holder.colorView.setBackgroundColor(context.getResources().getColor(FlagColor.values()[position].getMapColorID(), null));
            holder.flagImageButton.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(FlagColor.values()[position].getMapColorID(), null)));
            if (FlagColor.values()[position] == FlagColor.BROWN) {
                holder.colorView.setVisibility(View.VISIBLE);
            }
            allFlags.add(holder);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return FlagColor.values().length;
    }

    public void setRecycleViewItemOnClickListener(RecycleViewItemOnClickListener recycleViewItemOnClickListener) {
        this.recycleViewItemOnClickListener = recycleViewItemOnClickListener;
    }

    /**
     * 设置当前侧边旗帜区域打开的状态,如果是true则打开侧边栏,否则关闭.
     *
     * @param isOpen 打开的状态
     */
    public void setOpened(boolean isOpen) {
        this.open = isOpen;
        if (isOpen) {
            for (RecyclerViewHolder allFlag : allFlags) {
                allFlag.flagImageButton.setVisibility(View.VISIBLE);
            }
        } else {
            for (RecyclerViewHolder allFlag : allFlags) {
                allFlag.flagImageButton.setVisibility(View.GONE);
            }
        }
    }

    public boolean isOpen() {
        return open;
    }

    /**
     * 设置当前的旗帜状态,参数是一个数组<br>
     * 例如你设置的参数为{@link FlagColor#BLUE}和{@link FlagColor#RED},那么右侧就会将BLUE和RED这两种颜色的旗帜设置为标记状态<br>
     * 此外{@link FlagColor#BROWN}旗帜是无法进行任何更改的
     *
     * @param flagColors 提供一个旗帜颜色数组
     */
    public void setFlagStatus(FlagColor[] flagColors) {
        // 注意flagImageButton从头到尾都是不需要做任何操作的
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private View colorView;
        private ImageButton flagImageButton;
        private boolean sign = false;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.colorView = itemView.findViewById(R.id.sign_flag_element_view);
            this.flagImageButton = itemView.findViewById(R.id.sign_flag_element_flag);
            this.flagImageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // 如果是棕色则不更新
            if (FlagColor.values()[getAdapterPosition()] != FlagColor.BROWN) {
                if (sign) {
                    colorView.setVisibility(View.GONE);
                } else {
                    colorView.setVisibility(View.VISIBLE);
                }
                sign = !sign;
            }
            if (recycleViewItemOnClickListener != null) {
                recycleViewItemOnClickListener.recycleViewOnClick(getAdapterPosition());
            }
        }
    }


}
