package com.gitee.cnsukidayo.anylanguageword.ui.adapter.wordsearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.entity.local.WordDTOLocal;
import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;
import com.gitee.cnsukidayo.anylanguageword.handler.RecyclerViewAdapterItemChange;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.gitee.cnsukidayo.anylanguageword.utils.DPUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 这是一个标准的RecyclerViewAdapter
 */
public class SelectWordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerViewAdapterItemChange<WordDTOLocal> {

    private final Context context;
    // 所有单词选择列表
    private List<WordDTOLocal> selectWordPage;
    /**
     * 设置点击子划分的回调事件
     */
    private RecycleViewItemClickCallBack<WordDTOLocal> recycleViewItemOnClickListener;

    public SelectWordListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new SelectWordViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_search_word_choice_element, parent, false));
        } else {
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.theme_color)));
            RelativeLayout loadMoreLinearLayout = new RelativeLayout(context);
            loadMoreLinearLayout.setGravity(Gravity.CENTER);
            loadMoreLinearLayout.addView(progressBar, RelativeLayout.LayoutParams.MATCH_PARENT, DPUtils.dp2px(20));
            return new LoadMoreViewHolder(loadMoreLinearLayout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof SelectWordViewHolder) {
            SelectWordViewHolder selectWordViewHolder = (SelectWordViewHolder) holder;
            WordDTOLocal wordESDTO = selectWordPage.get(position);
            selectWordViewHolder.wordOrigin.setText(wordESDTO.getOrigin());
            // 遍历获取单词的额外信息并展示
            Map<EnglishStructure, String> details = wordESDTO.getValue();
            StringBuilder wordTranslationBuilder = new StringBuilder();
            //for (EnglishStructure englishStructure : details.keySet()) {
            //    Long key = englishStructure.getWordStructureId();
            //    String fieldKey = Optional.ofNullable(currentWordStructure.get(key))
            //            .orElse(new WordStructureDTO())
            //            .getField();
            //    wordTranslationBuilder.append("[")
            //            .append(fieldKey)
            //            .append("]")
            //            .append(" ")
            //            .append(wordDetail.getValue())
            //            .append(" | ");
            //}
            selectWordViewHolder.wordTranslation.setText(wordTranslationBuilder.toString());
            // 通过获取当前用户的收藏列表来判断当前单词是否被用户收藏了
        }
    }

    @Override
    public int getItemCount() {
        return selectWordPage == null ? 0 : selectWordPage.size();
    }

    @Override
    public int getItemViewType(int position) {
        // 如果当前的position是selectWordPage的size表示当前这个组件是LoadMore组件
        return 0;
    }

    @Override
    public void addItem(WordDTOLocal item) {
    }

    @Override
    public void removeItem(WordDTOLocal item) {

    }

    @Override
    public void replaceAll(Collection<WordDTOLocal> wordDTOLocals) {
        int preCount = getItemCount();
        // 必须先移除旧数据再添加新数据,否则会造成状态不一致
        notifyItemRangeRemoved(0, preCount);
        this.selectWordPage.clear();
        this.selectWordPage.addAll(wordDTOLocals);
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public void setRecycleViewItemClickCallBack(RecycleViewItemClickCallBack<WordDTOLocal> recycleViewItemClickCallBack) {
        this.recycleViewItemOnClickListener = recycleViewItemClickCallBack;
    }

    /**
     * 单词选择列表
     */
    public class SelectWordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView wordOrigin, wordTranslation;
        private LinearLayout choiceElementWord;

        public SelectWordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.wordOrigin = itemView.findViewById(R.id.fragment_search_word_choice_element_word_origin);
            this.wordTranslation = itemView.findViewById(R.id.fragment_search_word_choice_element_word_translation);
            this.choiceElementWord = itemView.findViewById(R.id.fragment_search_word_choice_element_word);
            this.choiceElementWord.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recycleViewItemOnClickListener.viewClickCallBack(selectWordPage.get(getAdapterPosition()));
        }
    }

    /**
     * 加载更多的组件
     */
    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

}
