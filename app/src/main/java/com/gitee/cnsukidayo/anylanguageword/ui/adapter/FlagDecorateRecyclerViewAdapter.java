package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;

import java.util.ArrayList;
import java.util.List;

public class FlagDecorateRecyclerViewAdapter extends RecyclerView.Adapter<FlagDecorateRecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    // 保存所有的旗帜
    private final List<RecyclerViewHolder> allFlags = new ArrayList<>(FlagColor.values().length);

    public FlagDecorateRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.sign_flag_decoreate_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (allFlags.size() != getItemCount()) {
            // 所有颜色都初始化,但是只有棕色和绿色显示
            holder.flagDecorate.setBackgroundColor(context.getResources().getColor(FlagColor.values()[position].getMapColorID(), null));
            if (FlagColor.values()[position] != FlagColor.BROWN) {
                holder.flagDecorate.setAlpha(0.0f);
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

    /**
     * 设置当前的旗帜状态,参数是一个数组<br>
     * 例如你设置的参数为{@link FlagColor#BLUE}和{@link FlagColor#RED},那么右侧就会将BLUE和RED这两种颜色的旗帜设置为标记状态<br>
     * 此外{@link FlagColor#BROWN}旗帜是无法进行任何更改的
     *
     * @param flagColors 提供一个旗帜颜色数组
     */
    public void setFlagStatus(List<FlagColor> flagColors) {
        for (RecyclerViewHolder allFlag : allFlags) {
            allFlag.flagDecorate.setAlpha(0.0f);
        }
        for (int i = 0; i < flagColors.size(); i++) {
            allFlags.get(flagColors.get(i).ordinal()).flagDecorate.setAlpha(1.0f);
        }
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private View flagDecorate;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.flagDecorate = itemView.findViewById(R.id.sign_flag_decorate_element_view);
        }

    }


}
