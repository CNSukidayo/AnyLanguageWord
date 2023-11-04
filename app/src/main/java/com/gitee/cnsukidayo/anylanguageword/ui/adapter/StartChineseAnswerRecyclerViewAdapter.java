package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.KeyValueMap;
import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;

import java.util.List;

import io.github.cnsukidayo.wword.model.dto.WordDTO;

public class StartChineseAnswerRecyclerViewAdapter extends RecyclerView.Adapter<StartChineseAnswerRecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    private final List<WordDTO> word;
    private final List<KeyValueMap<EnglishStructure, String>> convertWord;

    public StartChineseAnswerRecyclerViewAdapter(Context context, final List<WordDTO> finalWord) {
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
        holder.meaningCategoryHint.setText(Math.toIntExact(convertWord.get(position).getKey().getWordStructureId()));
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

