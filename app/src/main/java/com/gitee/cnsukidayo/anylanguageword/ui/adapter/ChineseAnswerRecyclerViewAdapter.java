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

public class ChineseAnswerRecyclerViewAdapter extends RecyclerView.Adapter<ChineseAnswerRecyclerViewAdapter.RecyclerViewHolder> {

    private final Context context;
    // 当前的RecyclerViewAdapter是可以复用的,根据不同的界面,组件有不同的样式
    private RecyclerViewState recyclerViewState = RecyclerViewState.MAIN;
    /**
     * 当前单词详细信息的map
     */
    private Map<Long, List<WordDTO>> currentWordMap = new HashMap<>();
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

    public ChineseAnswerRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    /**
     * 展示一个单词的中文意思,该方法只负责将组件展示出来.至于哪些组件要动态地隐藏,由调用者决定.
     *
     * @param currentWordMap 待展示的单词
     */
    public void showWordChineseMessage(Map<Long, List<WordDTO>> currentWordMap) {
        this.currentWordMap = currentWordMap;
        notifyItemRangeChanged(0, getItemCount());
    }

    public void setRecyclerViewState(RecyclerViewState recyclerViewState) {
        this.recyclerViewState = recyclerViewState;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 根据不同的状态,解析不同的页面
        if (recyclerViewState == RecyclerViewState.DRAWER) {
            return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_drawer_chinese_answer_element, parent, false));
        }
        return new RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_chinese_answer_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // position对应wordStructureId
        EnglishStructure englishStructure = Optional.ofNullable(positionMap.get(position)).orElse(EnglishStructure.DEFAULT);
        holder.meaningCategoryHint.setText(context.getResources().getString(englishStructure.getHint()));
        List<WordDTO> currentWordDTO = currentWordMap.get(englishStructure.getWordStructureId());
        if (currentWordDTO != null &&
                currentWordDTO.size() > 0) {
            holder.meaningCategoryAnswer.setText(currentWordDTO.get(0).getValue());
            holder.meaningCategoryHint.setVisibility(View.VISIBLE);
            holder.meaningCategoryAnswer.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return 13;
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

    public enum RecyclerViewState {
        MAIN,
        DRAWER
    }

}
