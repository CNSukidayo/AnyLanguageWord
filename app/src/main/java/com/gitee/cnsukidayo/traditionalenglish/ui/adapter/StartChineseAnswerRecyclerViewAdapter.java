package com.gitee.cnsukidayo.traditionalenglish.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.context.KeyValueMap;
import com.gitee.cnsukidayo.traditionalenglish.entity.Word;
import com.gitee.cnsukidayo.traditionalenglish.enums.MeaningCategory;
import com.gitee.cnsukidayo.traditionalenglish.factory.StaticFactory;

import java.util.List;

public class StartChineseAnswerRecyclerViewAdapter extends RecyclerView.Adapter<StartChineseAnswerRecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    private final Word word;
    private final List<KeyValueMap<MeaningCategory, String>> convertWord;

    public StartChineseAnswerRecyclerViewAdapter(Context context, final Word finalWord) {
        this.context = context;
        this.word = finalWord;
        this.convertWord = StaticFactory.getWordMeaningConvertHandler().convertWordMeaning(finalWord);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_drawer_chinese_answer_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.meaningCategoryHint.setText(context.getResources().getString(convertWord.get(position).getKey().getMapValue()));
        holder.meaningCategoryAnswer.setText(convertWord.get(position).getValue());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return convertWord.size();
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private final TextView meaningCategoryHint, meaningCategoryAnswer;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            meaningCategoryHint = this.itemView.findViewById(R.id.fragment_word_credit_meaning_category_hint);
            meaningCategoryAnswer = this.itemView.findViewById(R.id.fragment_word_credit_textview_meaning_category_answer);
        }
    }
}

