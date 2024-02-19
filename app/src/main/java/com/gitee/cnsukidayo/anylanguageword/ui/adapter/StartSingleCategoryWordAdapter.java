package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;
import com.gitee.cnsukidayo.anylanguageword.handler.CategoryWordFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.RecyclerViewAdapterItemChange;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.MoveAndSwipedListener;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.StateChangedListener;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.cnsukidayo.wword.model.dto.WordCategoryWordDTO;
import io.github.cnsukidayo.wword.model.dto.WordDTO;

/**
 * 每个分类中的每个单词Adapter
 *
 * @author cnsukidayo
 * @date 2023/1/9 14:35
 */
public class StartSingleCategoryWordAdapter extends RecyclerView.Adapter<StartSingleCategoryWordAdapter.SingleCategoryWordViewHolder> implements
        MoveAndSwipedListener, RecyclerViewAdapterItemChange<WordCategoryWordDTO> {

    private final Context context;
    private FunctionContentCallBack functionContentCallBack;
    // 用于处理单词收藏功能的Handler
    private CategoryWordFunctionHandler categoryWordFunctionHandler;
    // 当前是否正在移动单词的标识
    private volatile boolean itemMoving = false;

    public StartSingleCategoryWordAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SingleCategoryWordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SingleCategoryWordViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_start_single_category_word_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SingleCategoryWordViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 将第一根线设置为别的颜色
        if (position == 0) {
            holder.separator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light, null));
        } else {
            holder.separator.setBackgroundColor(context.getResources().getColor(R.color.dark_gray, null));
        }
        if (itemMoving) {
            // 如果物体正在移动,则只更新线条颜色;
            return;
        }
        // 重置改变，防止由于复用而导致的显示问题
        holder.scroller.scrollTo(0, 0);
        Map<Long, List<WordDTO>> structureWordMap = categoryWordFunctionHandler.getWordFromCategory(functionContentCallBack.getCurrentWordCategoryPosition(), position);
        Optional.ofNullable(structureWordMap.get(EnglishStructure.WORD_ORIGIN.getWordStructureId()))
                .ifPresent(wordDTOS -> holder.wordOrigin
                        .setText(wordDTOS.size() > 0 && wordDTOS.get(0).getValue() != null ? wordDTOS.get(0).getValue() : ""));
        Optional.ofNullable(structureWordMap.get(EnglishStructure.UK_PHONETIC.getWordStructureId()))
                .ifPresent(wordDTOS -> holder.wordPhonetics
                        .setText(wordDTOS.size() > 0 && wordDTOS.get(0).getValue() != null ? wordDTOS.get(0).getValue() : ""));
        // 最后一个嵌套,单词中文意思的嵌套
        holder.chineseAnswerRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.startChineseAnswerRecyclerViewAdapter = new StartChineseAnswerRecyclerViewAdapter(context, categoryWordFunctionHandler.getWordFromCategory(functionContentCallBack.getCurrentWordCategoryPosition(), position));
        holder.chineseAnswerRecyclerView.setAdapter(holder.startChineseAnswerRecyclerViewAdapter);
        String phraseTranslation = Optional.ofNullable(structureWordMap.get(EnglishStructure.PHRASE_TRANSLATION.getWordStructureId()))
                .map(phraseWord -> phraseWord.size() > 0 ? phraseWord.get(0).getValue() : "")
                .orElse("");
        // 设置介词短语
        Optional.ofNullable(structureWordMap.get(EnglishStructure.PHRASE.getWordStructureId()))
                .ifPresentOrElse(wordDTOS -> {
                            holder.phraseAnswer.setText(wordDTOS.size() > 0 && wordDTOS.get(0).getValue() != null ? wordDTOS.get(0).getValue() : "" +
                                    " " +
                                    phraseTranslation);
                            holder.phraseHint.setVisibility(View.VISIBLE);
                            holder.phraseAnswer.setVisibility(View.VISIBLE);
                        }
                        , () -> {
                            holder.phraseHint.setVisibility(View.GONE);
                            holder.phraseAnswer.setVisibility(View.GONE);
                        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return categoryWordFunctionHandler.currentCategorySize(functionContentCallBack.getCurrentWordCategoryPosition());
    }

    public void onItemMove(int fromPosition, int toPosition) {
        categoryWordFunctionHandler.moveCategoryWord(functionContentCallBack.getCurrentWordCategoryPosition(), fromPosition, toPosition);
        functionContentCallBack.updateCategoryMessage();
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(fromPosition, Boolean.FALSE);
        notifyItemChanged(toPosition, Boolean.FALSE);
    }

    @Override
    public void onItemDismiss(int position) {
    }

    public void setCategoryWordFunctionHandler(CategoryWordFunctionHandler categoryWordFunctionHandler) {
        this.categoryWordFunctionHandler = categoryWordFunctionHandler;
    }

    public void setFunctionListener(FunctionContentCallBack functionContentCallBack) {
        this.functionContentCallBack = functionContentCallBack;
    }

    @Override
    public void addItem(WordCategoryWordDTO wordCategoryWordDTO) {
        categoryWordFunctionHandler.addWordToCategory(functionContentCallBack.getCurrentWordCategoryPosition(), wordCategoryWordDTO);
        notifyItemChanged(categoryWordFunctionHandler.currentCategorySize(functionContentCallBack.getCurrentWordCategoryPosition()) - 1);
    }

    @Override
    public void removeItem(WordCategoryWordDTO item) {

    }

    public class SingleCategoryWordViewHolder extends RecyclerView.ViewHolder implements
            StateChangedListener, View.OnTouchListener, View.OnClickListener {
        // separator颜色线条
        private final View itemView, scroller, separator;
        private final TextView delete, wordOrigin, wordPhonetics, phraseAnswer, phraseHint;
        private final ImageButton move;
        private final RecyclerView chineseAnswerRecyclerView;
        private StartChineseAnswerRecyclerViewAdapter startChineseAnswerRecyclerViewAdapter;

        public SingleCategoryWordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.delete = itemView.findViewById(R.id.fragment_word_credit_start_word_delete);
            this.scroller = itemView.findViewById(R.id.fragment_word_credit_start_single_category_word_scroller);
            this.move = itemView.findViewById(R.id.fragment_word_credit_start_word_move);
            this.wordOrigin = itemView.findViewById(R.id.fragment_word_credit_start_single_category_word_origin);
            this.wordPhonetics = itemView.findViewById(R.id.fragment_word_credit_start_single_category_word_phonetics);
            this.chineseAnswerRecyclerView = itemView.findViewById(R.id.fragment_word_credit_start_category_chinese_answer);
            this.phraseHint = itemView.findViewById(R.id.fragment_word_credit_start_phrase_hint);
            this.phraseAnswer = itemView.findViewById(R.id.fragment_word_credit_start_phrase_answer);
            this.separator = itemView.findViewById(R.id.fragment_word_credit_start_single_category_word_separator);
            this.move.setOnTouchListener(this);
            this.delete.setOnClickListener(this);
        }

        @Override
        public void onItemSelected() {
            itemView.setAlpha(0.5f);
            itemMoving = true;
        }

        @Override
        public void onItemClear() {
            itemView.setAlpha(1.0f);
            itemMoving = false;
            // 批量更新本次移动的情况
            categoryWordFunctionHandler.updateWordCategoryWordOrder(functionContentCallBack.getCurrentWordCategoryPosition());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int clickViewId = v.getId();
            if (clickViewId == R.id.fragment_word_credit_start_word_move &&
                    event.getAction() == MotionEvent.ACTION_DOWN &&
                    functionContentCallBack != null) {
                functionContentCallBack.startDrag(this);
            }
            return false;
        }


        @Override
        public void onClick(View v) {
            int clickViewId = v.getId();
            if (clickViewId == R.id.fragment_word_credit_start_word_delete) {
                categoryWordFunctionHandler.removeWordFromCategory(functionContentCallBack.getCurrentWordCategoryPosition(), getAdapterPosition());
                functionContentCallBack.updateCategoryMessage();
                // 更新第0个元素的内容,把蓝色线条画上
                notifyItemChanged(0);
                notifyItemRemoved(getAdapterPosition());
            }
        }
    }

    /**
     * 功能函数上下文回调接口
     */
    public interface FunctionContentCallBack {
        void startDrag(RecyclerView.ViewHolder viewHolder);

        /**
         * 得到当前单词隶属于哪个收藏夹
         *
         * @return 返回收藏夹的position
         */
        int getCurrentWordCategoryPosition();

        /**
         * 当发生单词移动时更新,如果收藏夹采用默认的命名规则;则该方法会更新收藏夹的名称
         */
        void updateCategoryMessage();

    }

}
