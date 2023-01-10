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
import com.gitee.cnsukidayo.traditionalenglish.handler.WordMeaningConvertHandler;

import java.util.ArrayList;
import java.util.List;

public class ChineseAnswerRecyclerViewAdapter extends RecyclerView.Adapter<ChineseAnswerRecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    // 用于存储所有所有的element
    private List<RecyclerViewHolder> cacheElement = new ArrayList<>(MeaningCategory.values().length);
    // 当前的RecyclerViewAdapter是可以复用的,根据不同的界面,组件有不同的样式
    private RecyclerViewState recyclerViewState = RecyclerViewState.MAIN;

    public ChineseAnswerRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    /**
     * 展示一个单词的中文意思,该方法只负责将组件展示出来.至于哪些组件要动态地隐藏,由调用者决定.
     *
     * @param word 待展示的单词
     */
    public void showWordChineseMessage(Word word) {
        // 根据单词的意思,定义哪些组件是要显示的
        for (RecyclerViewHolder holder : cacheElement) {
            holder.meaningCategoryAnswer.setVisibility(View.GONE);
            holder.meaningCategoryHint.setVisibility(View.GONE);
        }
        WordMeaningConvertHandler wordMeaningConvertHandler = StaticFactory.getWordMeaningConvertHandler();
        List<KeyValueMap<MeaningCategory, String>> meaningCategories = wordMeaningConvertHandler.convertWordMeaning(word);
        for (KeyValueMap<MeaningCategory, String> meaningCategory : meaningCategories) {
            RecyclerViewHolder holder = cacheElement.get(meaningCategory.getKey().ordinal());
            holder.meaningCategoryAnswer.setVisibility(View.VISIBLE);
            holder.meaningCategoryHint.setVisibility(View.VISIBLE);
            holder.meaningCategoryAnswer.setText(meaningCategory.getValue());
        }
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
        if (cacheElement.size() != MeaningCategory.values().length) {
            holder.position = position;
            holder.meaningCategoryHint.setText(context.getResources().getString(MeaningCategory.values()[position].getMapValue()));
            cacheElement.add(holder);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return MeaningCategory.values().length;
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
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

    public enum RecyclerViewState {
        MAIN,
        DRAWER
    }

}
