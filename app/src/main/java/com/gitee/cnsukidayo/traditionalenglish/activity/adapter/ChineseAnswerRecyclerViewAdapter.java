package com.gitee.cnsukidayo.traditionalenglish.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.entity.Word;
import com.gitee.cnsukidayo.traditionalenglish.enums.MeaningCategory;

import java.util.ArrayList;
import java.util.List;

public class ChineseAnswerRecyclerViewAdapter extends RecyclerView.Adapter<ChineseAnswerRecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    // 用于存储所有所有的element
    private List<RecyclerViewHolder> cacheElement = new ArrayList<>(MeaningCategory.values().length);

    public void showWordChineseMessage(Word word) {

    }

    public ChineseAnswerRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_chinese_answer_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.position = position;
        holder.meaningCategoryHint.setText(context.getResources().getString(MeaningCategory.values()[position].getMapValue()));
        holder.meaningCategoryAnswer.setText("");
        cacheElement.add(holder);
    }

    @Override
    public int getItemCount() {
        return MeaningCategory.values().length;
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private final TextView meaningCategoryHint, meaningCategoryAnswer;
        private int position;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            meaningCategoryHint = this.itemView.findViewById(R.id.fragment_word_credit_meaning_category_hint);
            meaningCategoryAnswer = this.itemView.findViewById(R.id.fragment_word_credit_textview_meaning_category_answer);
        }
    }

}
