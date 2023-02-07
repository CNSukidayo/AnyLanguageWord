package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
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
import com.gitee.cnsukidayo.anylanguageword.entity.Word;
import com.gitee.cnsukidayo.anylanguageword.handler.CategoryWordFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.RecyclerViewAdapterItemChange;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.MoveAndSwipedListener;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.StateChangedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 每个分类中的每个单词Adapter
 *
 * @author cnsukidayo
 * @date 2023/1/9 14:35
 */
public class StartSingleCategoryWordAdapter extends RecyclerView.Adapter<StartSingleCategoryWordAdapter.RecyclerViewHolder> implements MoveAndSwipedListener, RecyclerViewAdapterItemChange<Word> {

    private Context context;
    private FunctionListener functionListener;
    // 用于处理单词收藏功能的Handler
    private CategoryWordFunctionHandler categoryWordFunctionHandler;
    // 用户缓存所有的element
    private final List<RecyclerViewHolder> cacheElement = new ArrayList<>();

    public StartSingleCategoryWordAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public StartSingleCategoryWordAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StartSingleCategoryWordAdapter.RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_start_single_category_word_element, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull StartSingleCategoryWordAdapter.RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 重置改变，防止由于复用而导致的显示问题
        holder.scroller.scrollTo(0, 0);
        Word word = categoryWordFunctionHandler.getWordFromCategory(functionListener.getCurrentWordCategoryID(), position);
        holder.wordOrigin.setText(word.getWordOrigin());
        holder.wordPhonetics.setText(word.getWordPhonetics());
        // 将第一根线设置为别的颜色
        if (position == 0) {
            holder.separator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light, null));
        } else {
            holder.separator.setBackgroundColor(context.getResources().getColor(R.color.dark_gray, null));
        }
        cacheElement.add(holder);
        // 最后一个嵌套,单词中文意思的嵌套
        holder.chineseAnswerRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.startChineseAnswerRecyclerViewAdapter = new StartChineseAnswerRecyclerViewAdapter(context, categoryWordFunctionHandler.getWordFromCategory(functionListener.getCurrentWordCategoryID(), position));
        holder.chineseAnswerRecyclerView.setAdapter(holder.startChineseAnswerRecyclerViewAdapter);
        if (!TextUtils.isEmpty(word.getPhrase())) {
            holder.phraseAnswer.setText(word.getPhrase());
            holder.phraseHint.setVisibility(View.VISIBLE);
            holder.phraseAnswer.setVisibility(View.VISIBLE);
        } else {
            holder.phraseHint.setVisibility(View.GONE);
            holder.phraseAnswer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return categoryWordFunctionHandler.currentCategorySize(functionListener.getCurrentWordCategoryID());
    }

    public void onItemMove(int fromPosition, int toPosition) {
        categoryWordFunctionHandler.moveCategoryWord(functionListener.getCurrentWordCategoryID(), fromPosition, toPosition);
        functionListener.updateCategoryMessage();
        Collections.swap(cacheElement, fromPosition, toPosition);
        // 维持第一个分割线的颜色是蓝色
        cacheElement.get(fromPosition).separator.setBackgroundColor(context.getResources().getColor(R.color.dark_gray, null));
        cacheElement.get(toPosition).separator.setBackgroundColor(context.getResources().getColor(R.color.dark_gray, null));
        cacheElement.get(0).separator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light, null));
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        notifyItemRemoved(position);
    }

    public void setCategoryWordFunctionHandler(CategoryWordFunctionHandler categoryWordFunctionHandler) {
        this.categoryWordFunctionHandler = categoryWordFunctionHandler;
    }

    public void setFunctionListener(FunctionListener functionListener) {
        this.functionListener = functionListener;
    }

    @Override
    public void addItem(Word word) {
        if (categoryWordFunctionHandler == null) {
            Log.e("no Handler", "caller want to use StartFunction,but no settings startFunctionHandler");
            return;
        }
        categoryWordFunctionHandler.addWordToCategory(functionListener.getCurrentWordCategoryID(), word);
        notifyItemInserted(categoryWordFunctionHandler.currentCategorySize(functionListener.getCurrentWordCategoryID()) - 1);
    }


    @Override
    public void removeItem(Word word) {

    }


    protected class RecyclerViewHolder extends RecyclerView.ViewHolder implements StateChangedListener, View.OnTouchListener, View.OnClickListener {
        public View itemView, scroller, separator;
        public TextView delete, wordOrigin, wordPhonetics, phraseAnswer, phraseHint;
        public ImageButton move;
        public RecyclerView chineseAnswerRecyclerView;
        public StartChineseAnswerRecyclerViewAdapter startChineseAnswerRecyclerViewAdapter;

        public RecyclerViewHolder(@NonNull View itemView) {
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
        }

        @Override
        public void onItemClear() {
            itemView.setAlpha(1.0f);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.fragment_word_credit_start_word_move:
                    if (event.getAction() == MotionEvent.ACTION_DOWN && functionListener != null) {
                        functionListener.startDrag(this);
                    }
                    break;
            }
            return false;
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fragment_word_credit_start_word_delete:
                    categoryWordFunctionHandler.removeWordFromCategory(functionListener.getCurrentWordCategoryID(), getAdapterPosition());
                    functionListener.updateCategoryMessage();
                    cacheElement.remove(getAdapterPosition());
                    cacheElement.get(0).separator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light, null));
                    notifyItemRemoved(getAdapterPosition());
                    break;
            }
        }
    }

    public interface FunctionListener {
        void startDrag(RecyclerView.ViewHolder viewHolder);

        int getCurrentWordCategoryID();

        void updateCategoryMessage();

    }

}
