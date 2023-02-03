package com.gitee.cnsukidayo.traditionalenglish.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.entity.WordCategory;
import com.gitee.cnsukidayo.traditionalenglish.handler.CategoryFunctionHandler;
import com.gitee.cnsukidayo.traditionalenglish.handler.RecyclerViewAdapterItemChange;
import com.gitee.cnsukidayo.traditionalenglish.ui.adapter.listener.MoveAndSwipedListener;
import com.gitee.cnsukidayo.traditionalenglish.ui.adapter.listener.StateChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 每个分类的Adapter
 *
 * @author cnsukidayo
 * @date 2023/1/7 17:35
 */
public class StartSingleCategoryAdapter extends RecyclerView.Adapter<StartSingleCategoryAdapter.RecyclerViewHolder> implements MoveAndSwipedListener, RecyclerViewAdapterItemChange<WordCategory> {

    private Context context;
    // 用于存储所有所有的element
    private List<StartSingleCategoryAdapter.RecyclerViewHolder> cacheElement = new ArrayList<>(15);
    // 用于回调startDrag方法的接口,该接口耦合了,定义方式不好.
    private Consumer<RecyclerView.ViewHolder> startDragListener;
    // 用于处理单词收藏功能的Handler
    private CategoryFunctionHandler startFunctionHandler;

    public StartSingleCategoryAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public StartSingleCategoryAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StartSingleCategoryAdapter.RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_start_single_category, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull StartSingleCategoryAdapter.RecyclerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 重置改变，防止由于复用而导致的显示问题
        holder.scroller.scrollTo(0, 0);
        holder.underNowStartAllWord.setVisibility(View.GONE);
        holder.title.setText(startFunctionHandler.calculationTitle(position));
        holder.describe.setText(startFunctionHandler.calculationDescribe(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return startFunctionHandler.categoryListSize();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        startFunctionHandler.categoryRemove(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        startFunctionHandler.removeCategory(position);
        notifyItemRemoved(position);
    }

    public void setStartFunctionHandler(CategoryFunctionHandler startFunctionHandler) {
        this.startFunctionHandler = startFunctionHandler;
    }

    public void setStartDragListener(Consumer<RecyclerView.ViewHolder> startDragListener) {
        this.startDragListener = startDragListener;
    }


    @Override
    public void addItem(WordCategory wordCategory) {
        if (startFunctionHandler == null) {
            Log.e("no Handler", "caller want to use StartFunction,but no settings startFunctionHandler");
            return;
        }
        startFunctionHandler.addNewCategory(wordCategory);
        notifyItemInserted(startFunctionHandler.categoryListSize() - 1);
    }

    @Override
    public void removeItem(WordCategory wordCategory) {

    }


    protected class RecyclerViewHolder extends RecyclerView.ViewHolder
            implements StateChangedListener, View.OnTouchListener, View.OnClickListener, StartSingleCategoryWordAdapter.FunctionListener {
        public View itemView, scroller;
        public TextView title, describe, edit, delete, addWord;
        public LinearLayout openList;
        public ImageView listState;
        public ImageButton move;
        /*
         如果某个分类展开与否的状态需要由StartFunctionHandler来控制,则托管由StartFunctionHandler来实现该功能.
         目前而言,暂时不需要外部接入,内部就能够完成,就内部来实现这个功能
         */
        private boolean isOpen;
        // 显示当前收藏夹下的所有单词的RecyclerView
        public RecyclerView underNowStartAllWord;
        public StartSingleCategoryWordAdapter startSingleCategoryWordAdapter;
        public ItemTouchHelper touchHelper;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.title = itemView.findViewById(R.id.fragment_word_credit_start_single_category_title);
            this.describe = itemView.findViewById(R.id.fragment_word_credit_start_single_category_detail);
            this.scroller = itemView.findViewById(R.id.fragment_word_credit_start_single_category_scroller);
            this.openList = itemView.findViewById(R.id.fragment_word_credit_start_open_list);
            this.listState = itemView.findViewById(R.id.fragment_word_credit_start_list_state);
            this.edit = itemView.findViewById(R.id.fragment_word_credit_start_edit);
            this.delete = itemView.findViewById(R.id.fragment_word_credit_start_delete);
            this.move = itemView.findViewById(R.id.fragment_word_credit_start_move);
            this.addWord = itemView.findViewById(R.id.fragment_word_credit_start_add_word);
            this.underNowStartAllWord = itemView.findViewById(R.id.fragment_word_credit_start_single_category_word_recycler_view);
            // 加载当前分类下的所有单词
            this.underNowStartAllWord.setLayoutManager(new LinearLayoutManager(context));
            this.startSingleCategoryWordAdapter = new StartSingleCategoryWordAdapter(context);
            this.underNowStartAllWord.setAdapter(startSingleCategoryWordAdapter);
            this.touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(startSingleCategoryWordAdapter));
            this.touchHelper.attachToRecyclerView(underNowStartAllWord);
            startSingleCategoryWordAdapter.setCategoryWordFunctionHandler(startFunctionHandler);
            startSingleCategoryWordAdapter.setFunctionListener(this);

            this.move.setOnTouchListener(this);
            this.delete.setOnClickListener(this);
            this.edit.setOnClickListener(this);
            this.openList.setOnClickListener(this);
            this.addWord.setOnClickListener(this);
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
                case R.id.fragment_word_credit_start_move:
                    if (event.getAction() == MotionEvent.ACTION_DOWN && startDragListener != null) {
                        startDragListener.accept(this);
                    }
                    break;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fragment_word_credit_start_delete:
                    // 删除当前Item
                    startFunctionHandler.removeCategory(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    break;
                case R.id.fragment_word_credit_start_edit:
                    // 编辑收藏夹信息
                    View editStart = LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_start_edit_new_dialog, null);
                    EditText categoryTile = editStart.findViewById(R.id.fragment_word_credit_start_new_title);
                    EditText categoryDescribe = editStart.findViewById(R.id.fragment_word_credit_start_new_describe);
                    CheckBox titleDefault = editStart.findViewById(R.id.fragment_word_credit_start_new_title_default);
                    CheckBox describeDefault = editStart.findViewById(R.id.fragment_word_credit_start_new_describe_default);
                    new AlertDialog.Builder(context)
                            .setView(editStart)
                            .setCancelable(true)
                            .setPositiveButton("确定", (dialog, which) -> {
                                WordCategory wordCategory = startFunctionHandler.getWordCategoryByPosition(getAdapterPosition());
                                wordCategory.setTitle(categoryTile.getText().toString());
                                wordCategory.setDescribe(categoryDescribe.getText().toString());
                                wordCategory.setDefaultTitleRule(titleDefault.isChecked());
                                wordCategory.setDefaultDescribeRule(describeDefault.isChecked());
                                title.setText(startFunctionHandler.calculationTitle(getAdapterPosition()));
                                describe.setText(startFunctionHandler.calculationDescribe(getAdapterPosition()));
                            })
                            .setNegativeButton("取消", (dialog, which) -> {
                            })
                            .show();
                    break;
                case R.id.fragment_word_credit_start_open_list:
                    if (isOpen) {
                        underNowStartAllWord.setVisibility(View.GONE);
                        listState.setRotation(90);
                        listState.getDrawable().setTint(context.getResources().getColor(R.color.dark_gray, null));
                    } else {
                        underNowStartAllWord.setVisibility(View.VISIBLE);
                        listState.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_blue_dark, null));
                        listState.setRotation(180);
                    }
                    isOpen = !isOpen;
                    break;
                case R.id.fragment_word_credit_start_add_word:
                    startSingleCategoryWordAdapter.addItem(startFunctionHandler.getCurrentWord());
                    title.setText(startFunctionHandler.calculationTitle(getAdapterPosition()));
                    describe.setText(startFunctionHandler.calculationDescribe(getAdapterPosition()));
                    break;
            }
        }

        @Override
        public void startDrag(RecyclerView.ViewHolder viewHolder) {
            this.touchHelper.startDrag(viewHolder);
        }

        @Override
        public int getCurrentWordCategoryID() {
            return getAdapterPosition();
        }

        @Override
        public void updateCategoryMessage() {
            title.setText(startFunctionHandler.calculationTitle(getAdapterPosition()));
            describe.setText(startFunctionHandler.calculationDescribe(getAdapterPosition()));
        }
    }

}
