package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.entity.Word;
import com.gitee.cnsukidayo.anylanguageword.entity.WordCategory;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.handler.CategoryFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.impl.ABSCategoryFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.ChineseAnswerRecyclerViewAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.SimpleItemTouchHelperCallback;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.StartSingleCategoryAdapter;

import java.util.concurrent.TimeUnit;

/**
 * @author sukidayo
 * @date Thursday, February 09, 2023
 */
public class SearchWordFragment extends Fragment implements View.OnClickListener, KeyEvent.Callback {

    private View rootView;
    private ImageButton backToTrace;
    private AlertDialog loadingDialog = null;
    private RecyclerView chineseAnswer, chineseAnswerDrawer, starSingleCategory;
    private ChineseAnswerRecyclerViewAdapter chineseAnswerAdapter;
    private ChineseAnswerRecyclerViewAdapter chineseAnswerAdapterDrawer;
    private StartSingleCategoryAdapter startSingleCategoryAdapter;
    private Handler updateUIHandler;
    private LinearLayout analysisWord, openStartDrawer;
    private Word testWord = null;
    private TextView sourceWord, sourceWordPhonetics, drawerSourceWord, drawerSourceWordPhonetics;
    private TextView exampleSentenceAnswer, exampleSentenceHint, phraseAnswer, phraseHint, distinguishAnswer, distinguishHint, categorizeOriginAnswer, categorizeOriginHint;
    private TextView drawerPhraseHint, drawerPhraseAnswer, addNewCategory;
    private DrawerLayout startDrawer;
    private final CategoryFunctionHandler categoryFunctionHandler = new ABSCategoryFunctionHandler() {
        @Override
        public Word getCurrentWord() {
            return testWord;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_search_word, container, false);
        bindView();
        initView();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 注意每次进入到该页面的时候都必须更新收藏夹信息,包括背诵界面也要更新
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back_to_trace:
                Navigation.findNavController(getView()).popBackStack();
                break;
            case R.id.fragment_search_word_click_analysis_word:
                testWord = new Word();
                testWord.setWordOrigin("apple");
                testWord.setADJ("形容词");
                testWord.setV("动词");
                testWord.setDistinguish("描述信息");
                testWord.setPhrase("介词短语");
                showWord(testWord);
                break;
            case R.id.fragment_search_word_click_start:
                startDrawer.openDrawer(GravityCompat.END);
                break;
            case R.id.fragment_search_word_start_add:
                View addNewCategory = getLayoutInflater().inflate(R.layout.fragment_word_credit_start_edit_new_dialog, null);
                EditText categoryTile = addNewCategory.findViewById(R.id.fragment_word_credit_start_new_title);
                EditText categoryDescribe = addNewCategory.findViewById(R.id.fragment_word_credit_start_new_describe);
                CheckBox titleDefault = addNewCategory.findViewById(R.id.fragment_word_credit_start_new_title_default);
                CheckBox describeDefault = addNewCategory.findViewById(R.id.fragment_word_credit_start_new_describe_default);
                new AlertDialog.Builder(getContext())
                        .setView(addNewCategory)
                        .setCancelable(true)
                        .setPositiveButton("确定", (dialog, which) -> {
                            WordCategory wordCategory = new WordCategory(categoryTile.getText().toString(),
                                    categoryDescribe.getText().toString(), titleDefault.isChecked(), describeDefault.isChecked());
                            startSingleCategoryAdapter.addItem(wordCategory);
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                        })
                        .show();
                break;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (startDrawer.isDrawerOpen(GravityCompat.END)) {
                startDrawer.closeDrawer(GravityCompat.END);
                return true;
            }
            Navigation.findNavController(getView()).popBackStack();
        }
        return true;
    }

    private void showWord(Word word) {
        this.sourceWord.setText(word.getWordOrigin());
        this.sourceWordPhonetics.setText(word.getWordPhonetics());
        chineseAnswer.setVisibility(View.VISIBLE);
        chineseAnswerAdapter.showWordChineseMessage(word);
        if (!TextUtils.isEmpty(word.getExampleSentence())) {
            this.exampleSentenceAnswer.setText(word.getExampleSentence());
            this.exampleSentenceAnswer.setVisibility(View.VISIBLE);
            this.exampleSentenceHint.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(word.getPhrase())) {
            this.phraseAnswer.setText(word.getPhrase());
            this.phraseAnswer.setVisibility(View.VISIBLE);
            this.phraseHint.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(word.getDistinguish())) {
            this.distinguishAnswer.setText(word.getDistinguish());
            this.distinguishAnswer.setVisibility(View.VISIBLE);
            this.distinguishHint.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(word.getCategory())) {
            this.categorizeOriginAnswer.setText(word.getCategoryID());
            this.categorizeOriginAnswer.setVisibility(View.VISIBLE);
            this.categorizeOriginHint.setVisibility(View.VISIBLE);
        }
        chineseAnswerDrawer.setVisibility(View.VISIBLE);
        chineseAnswerAdapterDrawer.showWordChineseMessage(word);
        this.drawerSourceWord.setText(word.getWordOrigin());
        this.drawerSourceWordPhonetics.setText(word.getWordPhonetics());
        if (!TextUtils.isEmpty(word.getPhrase())) {
            drawerPhraseAnswer.setText(word.getPhrase());
            drawerPhraseHint.setVisibility(View.VISIBLE);
            drawerPhraseAnswer.setVisibility(View.VISIBLE);
        } else {
            drawerPhraseHint.setVisibility(View.GONE);
            drawerPhraseAnswer.setVisibility(View.GONE);
        }
    }

    private void initView() {
        updateUIHandler = new Handler();
        startDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // 隐藏单词的附加显示内容、隐藏收藏界面不必要的内容
        hideLinearLayoutTree(rootView.findViewById(R.id.fragment_search_word_answer_area));
        sourceWord.setText("");
        sourceWordPhonetics.setText("");
        drawerSourceWord.setText("");
        drawerSourceWordPhonetics.setText("");
        drawerPhraseHint.setVisibility(View.GONE);
        drawerPhraseAnswer.setVisibility(View.GONE);

        loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null)).setCancelable(false).show();
        this.chineseAnswer.setLayoutManager(new LinearLayoutManager(getContext()));
        this.chineseAnswerDrawer.setLayoutManager(new LinearLayoutManager(getContext()));
        this.starSingleCategory.setLayoutManager(new LinearLayoutManager(getContext()));

        StaticFactory.getExecutorService().submit(() -> {
            this.chineseAnswerAdapter = new ChineseAnswerRecyclerViewAdapter(getContext());
            this.chineseAnswerAdapterDrawer = new ChineseAnswerRecyclerViewAdapter(getContext());
            this.startSingleCategoryAdapter = new StartSingleCategoryAdapter(getContext());
            this.chineseAnswerAdapterDrawer.setRecyclerViewState(ChineseAnswerRecyclerViewAdapter.RecyclerViewState.DRAWER);
            // 绑定ItemTouchHelper,实现单个列表的编辑删除等功能
            ItemTouchHelper touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(startSingleCategoryAdapter));
            startSingleCategoryAdapter.setStartDragListener(touchHelper::startDrag);
            startSingleCategoryAdapter.setStartFunctionHandler(categoryFunctionHandler);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateUIHandler.post(() -> {
                this.chineseAnswer.setAdapter(chineseAnswerAdapter);
                this.chineseAnswerDrawer.setAdapter(chineseAnswerAdapterDrawer);
                this.starSingleCategory.setAdapter(startSingleCategoryAdapter);
                touchHelper.attachToRecyclerView(starSingleCategory);

                updateUIHandler.post(() -> {
                    this.chineseAnswer.setVisibility(View.GONE);
                    this.chineseAnswerDrawer.setVisibility(View.GONE);
                });
                // 请求收藏夹信息,这一步是有可能造成网络延迟的
                loadingDialog.dismiss();
            });
        });
    }

    private void hideLinearLayoutTree(ViewGroup linearLayout) {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View child = linearLayout.getChildAt(i);
            if (child instanceof TextView) {
                child.setVisibility(View.GONE);
            } else if (child instanceof ViewGroup) {
                hideLinearLayoutTree((ViewGroup) child);
            }
        }
    }


    private void bindView() {
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.sourceWord = rootView.findViewById(R.id.fragment_search_word_source_word);
        this.sourceWordPhonetics = rootView.findViewById(R.id.fragment_search_word_phonetics);
        this.chineseAnswer = rootView.findViewById(R.id.fragment_search_word_chinese_answer);
        this.analysisWord = rootView.findViewById(R.id.fragment_search_word_click_analysis_word);
        this.exampleSentenceHint = rootView.findViewById(R.id.fragment_search_word_example_sentence_hint);
        this.exampleSentenceAnswer = rootView.findViewById(R.id.fragment_search_word_example_sentence_answer);
        this.phraseHint = rootView.findViewById(R.id.fragment_search_word_phrase_hint);
        this.phraseAnswer = rootView.findViewById(R.id.fragment_search_word_phrase_answer);
        this.distinguishHint = rootView.findViewById(R.id.fragment_search_word_distinguish_hint);
        this.distinguishAnswer = rootView.findViewById(R.id.fragment_search_word_distinguish_answer);
        this.categorizeOriginHint = rootView.findViewById(R.id.fragment_search_word_categorize_origin_hint);
        this.categorizeOriginAnswer = rootView.findViewById(R.id.fragment_search_word_categorize_origin_answer);
        this.startDrawer = rootView.findViewById(R.id.fragment_search_word_start_drawer);
        this.openStartDrawer = rootView.findViewById(R.id.fragment_search_word_click_start);
        this.chineseAnswerDrawer = rootView.findViewById(R.id.fragment_search_word_drawer_chinese_answer);
        this.starSingleCategory = rootView.findViewById(R.id.fragment_search_word_start_category_recycler);
        this.drawerSourceWord = rootView.findViewById(R.id.fragment_word_credit_drawer_word_origin);
        this.drawerSourceWordPhonetics = rootView.findViewById(R.id.fragment_word_credit_drawer_word_phonetics);
        this.drawerPhraseHint = rootView.findViewById(R.id.fragment_search_word_drawer_phrase_hint);
        this.drawerPhraseAnswer = rootView.findViewById(R.id.fragment_search_word_drawer_phrase_answer);
        this.starSingleCategory = rootView.findViewById(R.id.fragment_search_word_start_category_recycler);
        this.addNewCategory = rootView.findViewById(R.id.fragment_search_word_start_add);

        this.backToTrace.setOnClickListener(this);
        this.analysisWord.setOnClickListener(this);
        this.openStartDrawer.setOnClickListener(this);
        this.addNewCategory.setOnClickListener(this);
    }

    // ----下面是一些用不到的方法----
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }
}