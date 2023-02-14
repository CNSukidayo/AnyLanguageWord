package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.utils.DPUtils;

/**
 * @author cnsukidayo
 * @date 2023/2/14 16:07
 */
public class TransparentViewHolder extends RecyclerView.ViewHolder {

    public TransparentViewHolder(@NonNull View itemView, @Px int height) {
        super(itemView);
        itemView.getLayoutParams().height = DPUtils.dp2px(height);
    }
}
