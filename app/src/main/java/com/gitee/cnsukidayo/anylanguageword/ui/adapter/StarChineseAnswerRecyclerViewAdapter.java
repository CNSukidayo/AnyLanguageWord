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
import com.gitee.cnsukidayo.anylanguageword.context.support.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.enums.structure.BaseStructure;
import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;
import com.gitee.cnsukidayo.anylanguageword.handler.RecyclerViewAdapterItemChange;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.cnsukidayo.wword.model.dto.WordDTO;

public class StarChineseAnswerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewAdapterItemChange<Map<Long, List<WordDTO>>> {

    private final Context context;
    private Map<Long, List<WordDTO>> structureWordMap;

    private final Map<Integer, BaseStructure> metaInfoFilterMap;

    public StarChineseAnswerRecyclerViewAdapter(Context context, Long languageId) {
        this.context = context;
        metaInfoFilterMap = StaticFactory.getWordMetaInfoFilter().getMetaInfoFilterMap(languageId);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StarChineseAnswerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_drawer_chinese_answer_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof StarChineseAnswerViewHolder) {
            StarChineseAnswerViewHolder starChineseAnswerViewHolder = (StarChineseAnswerViewHolder) holder;
            // position对应wordStructureId
            BaseStructure baseStructure = Optional.ofNullable(metaInfoFilterMap.get(position)).orElse(EnglishStructure.DEFAULT);
            starChineseAnswerViewHolder.meaningCategoryHint.setText(context.getResources().getString(baseStructure.getTitleHint()));
            List<WordDTO> currentWordDTO = structureWordMap.get(baseStructure.getWordStructureId());
            if (currentWordDTO != null &&
                    currentWordDTO.size() > 0) {
                starChineseAnswerViewHolder.meaningCategoryAnswer.setText(currentWordDTO.get(0).getValue());
                starChineseAnswerViewHolder.meaningCategoryHint.setVisibility(View.VISIBLE);
                starChineseAnswerViewHolder.meaningCategoryAnswer.setVisibility(View.VISIBLE);
            } else {
                starChineseAnswerViewHolder.meaningCategoryHint.setVisibility(View.GONE);
                starChineseAnswerViewHolder.meaningCategoryAnswer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return structureWordMap == null ? 0 : metaInfoFilterMap.size();
    }

    @Override
    public void addItem(Map<Long, List<WordDTO>> item) {
        structureWordMap = item;
        notifyItemChanged(0, getItemCount());
    }

    @Override
    public void removeItem(Map<Long, List<WordDTO>> item) {

    }

    public static class StarChineseAnswerViewHolder extends RecyclerView.ViewHolder {
        private final TextView meaningCategoryHint, meaningCategoryAnswer;

        public StarChineseAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            meaningCategoryHint = itemView.findViewById(R.id.fragment_word_credit_meaning_category_hint);
            meaningCategoryAnswer = itemView.findViewById(R.id.fragment_word_credit_textview_meaning_category_answer);
        }
    }
}

