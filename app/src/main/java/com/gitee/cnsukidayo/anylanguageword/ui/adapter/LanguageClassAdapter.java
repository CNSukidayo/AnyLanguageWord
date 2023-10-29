package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

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
import java.util.List;

import io.github.cnsukidayo.wword.model.dto.LanguageClassDTO;

public class LanguageClassAdapter extends RecyclerView.Adapter<LanguageClassAdapter.RecyclerViewHolder> implements RecyclerViewAdapterItemChange<LanguageClassDTO> {

    private Context context;
    /**
     * 保留所有的语种
     */
    private final List<LanguageClassDTO> allLanguageClassList = new ArrayList<>();

    /**
     * 点击语种后的回调事件
     */
    private RecycleViewItemClickCallBack<LanguageClassDTO> recycleViewItemClickCallBack;

    public LanguageClassAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.language_class_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        LanguageClassDTO languageClassDTO = allLanguageClassList.get(position);
        holder.languageClass.setText(languageClassDTO.getLanguage());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return allLanguageClassList.size();
    }

    @Override
    public void addItem(LanguageClassDTO item) {
        allLanguageClassList.add(item);
        notifyItemInserted(allLanguageClassList.size() - 1);
    }

    @Override
    public void removeItem(LanguageClassDTO item) {
        if (item == null) {

        }
    }

    @Override
    public void addAll(Collection<LanguageClassDTO> languageClassDTOS) {
        // todo 重复请求会造成数据不一致
        for (LanguageClassDTO languageClass : languageClassDTOS) {
            addItem(languageClass);
        }
    }

    public void setRecycleViewItemClickCallBack(RecycleViewItemClickCallBack<LanguageClassDTO> recycleViewItemClickCallBack) {
        this.recycleViewItemClickCallBack = recycleViewItemClickCallBack;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView languageClass;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.languageClass = itemView.findViewById(R.id.language_class_element_language_text);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // 负责传递消息给上一层,表明当前点击了某个语种
            if (recycleViewItemClickCallBack != null) {
                int position = getAdapterPosition();
                LanguageClassDTO languageClassDTO = allLanguageClassList.get(position);
                recycleViewItemClickCallBack.viewClickCallBack(languageClassDTO);
            }
        }
    }


}
