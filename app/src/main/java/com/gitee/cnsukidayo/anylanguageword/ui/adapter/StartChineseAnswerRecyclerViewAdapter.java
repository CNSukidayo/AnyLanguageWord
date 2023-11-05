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
import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.cnsukidayo.wword.model.dto.WordDTO;

public class StartChineseAnswerRecyclerViewAdapter extends RecyclerView.Adapter<StartChineseAnswerRecyclerViewAdapter.RecyclerViewHolder> {

    private final Context context;
    private final Map<Long, List<WordDTO>> finalStructureWordMap;

    /**
     * key:组件的position<br>
     * value:单词的结构id
     */
    private final Map<Integer, EnglishStructure> positionMap = new HashMap<>(13) {{
        put(0, EnglishStructure.ADJ);
        put(1, EnglishStructure.ADV);
        put(2, EnglishStructure.V);
        put(3, EnglishStructure.VI);
        put(4, EnglishStructure.VT);
        put(5, EnglishStructure.N);
        put(6, EnglishStructure.CONJ);
        put(7, EnglishStructure.PRON);
        put(8, EnglishStructure.NUM);
        put(9, EnglishStructure.ART);
        put(10, EnglishStructure.PREP);
        put(11, EnglishStructure.INT);
        put(12, EnglishStructure.AUX);
    }};

    public StartChineseAnswerRecyclerViewAdapter(Context context, final Map<Long, List<WordDTO>> finalStructureWordMap) {
        this.context = context;
        this.finalStructureWordMap = finalStructureWordMap;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_drawer_chinese_answer_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // position对应wordStructureId
        EnglishStructure englishStructure = Optional.ofNullable(positionMap.get(position)).orElse(EnglishStructure.DEFAULT);
        holder.meaningCategoryHint.setText(context.getResources().getString(englishStructure.getHint()));
        List<WordDTO> currentWordDTO = finalStructureWordMap.get(englishStructure.getWordStructureId());
        if (currentWordDTO != null &&
                currentWordDTO.size() > 0) {
            holder.meaningCategoryAnswer.setText(currentWordDTO.get(0).getValue());
            holder.meaningCategoryHint.setVisibility(View.VISIBLE);
            holder.meaningCategoryAnswer.setVisibility(View.VISIBLE);
        } else {
            holder.meaningCategoryHint.setVisibility(View.GONE);
            holder.meaningCategoryAnswer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 13;
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView meaningCategoryHint, meaningCategoryAnswer;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            meaningCategoryHint = itemView.findViewById(R.id.fragment_word_credit_meaning_category_hint);
            meaningCategoryAnswer = itemView.findViewById(R.id.fragment_word_credit_textview_meaning_category_answer);
        }
    }
}

