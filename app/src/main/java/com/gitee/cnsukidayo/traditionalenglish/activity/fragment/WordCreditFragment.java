package com.gitee.cnsukidayo.traditionalenglish.activity.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.context.TraditionalEnglishProperties;
import com.gitee.cnsukidayo.traditionalenglish.entity.Word;
import com.gitee.cnsukidayo.traditionalenglish.factory.StaticFactory;
import com.gitee.cnsukidayo.traditionalenglish.utils.AnimationUtil;
import com.gitee.cnsukidayo.traditionalenglish.utils.Strings;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WordCreditFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private ImageButton popMoreFunction;
    private HorizontalScrollView moreFunctionHorizontalScrollView;
    private boolean moreFunctionOpen = true;
    private Handler updateUIHandler;
    /*
    以下是所有功能按钮的变量声明
     */
    private final List<Word> allWordList = new ArrayList<>();
    private int currentIndex = 0;
    private ImageButton nextWord, previousWord;
    private TextView sourceWord, sourceWordPhonetics, getAnswer, adjHint, advHint, vHint, viHint, vtHint, nHint, conjHint, pronHint, numHint, artHint;
    private TextView prepHint, intHint, auxHint, exampleSentenceHint, phraseHint, distinguishHint, categorizeOriginHint;
    private TextView adjAnswer, advAnswer, vAnswer, viAnswer, vtAnswer, nAnswer, conjAnswer, pronAnswer, numAnswer, artAnswer, prepAnswer;
    private TextView intAnswer, auxAnswer, exampleSentenceAnswer, phraseAnswer, distinguishAnswer, categorizeOriginAnswer, currentIndexTextView, wordCount;
    private AlertDialog loadingDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_word_credit, container, false);
        }
        this.updateUIHandler = new Handler();
        // 获取相关组件
        bindView();
        // 设置监听事件
        bindListener();
        // 隐藏不必要的UI
        hideAnswer();
        // 弹出Dialog不要阻塞UI线程,通过一个新的线程去请求所有单词信息.
        loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null))
                .setCancelable(false)
                .show();
        // 读取所有单词信息,通过Bundle得到当前用户选中的单词分类,这里暂时以样本单词进行测试.
        readAllWord();

        return rootView;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_word_credit_pop_more_function:
                if (moreFunctionOpen) {
                    if (AnimationUtil.with().moveToViewBottom(moreFunctionHorizontalScrollView, 500)) {
                        popMoreFunction.setImageResource(R.drawable.open_previous);
                        moreFunctionOpen = !moreFunctionOpen;
                    }
                } else {
                    if (AnimationUtil.with().bottomMoveToViewLocation(moreFunctionHorizontalScrollView, 500)) {
                        popMoreFunction.setImageResource(R.drawable.open_after);
                        moreFunctionOpen = !moreFunctionOpen;
                    }
                }
                break;
            case R.id.fragment_word_credit_next_word:
                if (currentIndex == allWordList.size() - 1) {
                    currentIndex = -1;
                }
                currentIndex++;
                hideAnswer();
                creditWord(currentIndex);
                break;
            case R.id.fragment_word_credit_previous_word:
                if (currentIndex == 0) {
                    currentIndex = allWordList.size();
                }
                currentIndex--;
                hideAnswer();
                creditWord(currentIndex);
                break;
            case R.id.fragment_word_container_get_answer:
                handleWordAnswer();
                break;
        }
    }

    /**
     * 根据单词在allWorldList中的索引背诵某个单词
     *
     * @param currentIndex 要背诵的单词的在allWordList中的索引
     */
    private void creditWord(int currentIndex) {
        updateUIHandler.post(() -> {
            Word creditWord = allWordList.get(currentIndex);
            sourceWord.setText(creditWord.getWordOrigin());
            sourceWordPhonetics.setText(creditWord.getWordPhonetics());
            currentIndexTextView.setText(String.valueOf(currentIndex + 1));
        });
    }

    /**
     * 读取所有单词信息,读取完毕之后更新UI
     */
    private void readAllWord() {
        StaticFactory.getExecutorService().submit(() -> {
            File wordFile = new File(TraditionalEnglishProperties.getExternalFilesDir(), Environment.DIRECTORY_DOCUMENTS + File.separator + "temp");
            for (File singleWordList : wordFile.listFiles()) {
                try {
                    List<Word> wordList = StaticFactory.getGson().fromJson(
                            new BufferedReader(new FileReader(singleWordList)), new TypeToken<List<Word>>() {

                            }.getType());
                    allWordList.addAll(wordList);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateUIHandler.post(() -> {
                creditWord(0);
                wordCount.setText(String.valueOf(allWordList.size()));
                loadingDialog.dismiss();
            });
        });
    }

    /**
     * 隐藏所有暂时不必要出现的UI
     */
    private void hideAnswer() {
        LinearLayout linearLayout = rootView.findViewById(R.id.fragment_word_credit_answer_area);
        hideLinearLayoutTree(linearLayout);
        sourceWord.setText("");
        sourceWordPhonetics.setText("");
    }

    private void hideLinearLayoutTree(LinearLayout linearLayout) {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View child = linearLayout.getChildAt(i);
            if (child instanceof TextView) {
                child.setVisibility(View.GONE);
            } else if (child instanceof LinearLayout) {
                hideLinearLayoutTree((LinearLayout) child);
            }
        }
    }

    /**
     * 处理单词结果显示逻辑
     */
    private void handleWordAnswer() {
        Word word = allWordList.get(currentIndex);
        if (Strings.notEmpty(word.getADJ())) {
            this.adjAnswer.setText(word.getADJ());
            this.adjAnswer.setVisibility(View.VISIBLE);
            this.adjHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getADV())) {
            this.advAnswer.setText(word.getADV());
            this.advAnswer.setVisibility(View.VISIBLE);
            this.advHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getV())) {
            this.vAnswer.setText(word.getV());
            this.vAnswer.setVisibility(View.VISIBLE);
            this.vHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getVI())) {
            this.viAnswer.setText(word.getVI());
            this.viAnswer.setVisibility(View.VISIBLE);
            this.viHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getVT())) {
            this.vtAnswer.setText(word.getVT());
            this.vtAnswer.setVisibility(View.VISIBLE);
            this.vtHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getN())) {
            this.nAnswer.setText(word.getN());
            this.nAnswer.setVisibility(View.VISIBLE);
            this.nHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getCONJ())) {
            this.conjAnswer.setText(word.getCONJ());
            this.conjAnswer.setVisibility(View.VISIBLE);
            this.conjHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getPRON())) {
            this.pronAnswer.setText(word.getPRON());
            this.pronAnswer.setVisibility(View.VISIBLE);
            this.pronHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getNUM())) {
            this.numAnswer.setText(word.getPRON());
            this.numAnswer.setVisibility(View.VISIBLE);
            this.numHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getART())) {
            this.artAnswer.setText(word.getART());
            this.artAnswer.setVisibility(View.VISIBLE);
            this.artHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getPREP())) {
            this.prepAnswer.setText(word.getPREP());
            this.prepAnswer.setVisibility(View.VISIBLE);
            this.prepHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getINT())) {
            this.intAnswer.setText(word.getINT());
            this.intAnswer.setVisibility(View.VISIBLE);
            this.intHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getAUX())) {
            this.auxAnswer.setText(word.getAUX());
            this.auxAnswer.setVisibility(View.VISIBLE);
            this.auxHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getExampleSentence())) {
            this.exampleSentenceAnswer.setText(word.getExampleSentence());
            this.exampleSentenceAnswer.setVisibility(View.VISIBLE);
            this.exampleSentenceHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getPhrase())) {
            this.phraseAnswer.setText(word.getPhrase());
            this.phraseAnswer.setVisibility(View.VISIBLE);
            this.phraseHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getDistinguish())) {
            this.distinguishAnswer.setText(word.getDistinguish());
            this.distinguishAnswer.setVisibility(View.VISIBLE);
            this.distinguishHint.setVisibility(View.VISIBLE);
        }
        if (Strings.notEmpty(word.getCategory())) {
            this.categorizeOriginAnswer.setText(word.getCategoryID());
            this.categorizeOriginAnswer.setVisibility(View.VISIBLE);
            this.categorizeOriginHint.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 绑定所有组件
     */
    private void bindView() {
        this.popMoreFunction = rootView.findViewById(R.id.fragment_word_credit_pop_more_function);
        this.moreFunctionHorizontalScrollView = rootView.findViewById(R.id.fragment_word_credit_more_function_horizontal_scroll_view);
        this.nextWord = rootView.findViewById(R.id.fragment_word_credit_next_word);
        this.sourceWord = rootView.findViewById(R.id.text_view_source_word);
        this.sourceWordPhonetics = rootView.findViewById(R.id.text_view_source_word_phonetics);
        this.previousWord = rootView.findViewById(R.id.fragment_word_credit_previous_word);
        this.getAnswer = rootView.findViewById(R.id.fragment_word_container_get_answer);
        this.currentIndexTextView = rootView.findViewById(R.id.fragment_word_credit_current_index);
        this.wordCount = rootView.findViewById(R.id.fragment_word_credit_word_count);

        this.adjHint = rootView.findViewById(R.id.fragment_word_credit_adjective_hint);
        this.advHint = rootView.findViewById(R.id.fragment_word_credit_adverb_hint);
        this.vHint = rootView.findViewById(R.id.fragment_word_credit_verb_hint);
        this.viHint = rootView.findViewById(R.id.fragment_word_credit_intransitive_verb_hint);
        this.vtHint = rootView.findViewById(R.id.fragment_word_credit_transitive_verb_hint);
        this.nHint = rootView.findViewById(R.id.fragment_word_credit_noun_hint);
        this.conjHint = rootView.findViewById(R.id.fragment_word_credit_conjunction_hint);
        this.pronHint = rootView.findViewById(R.id.fragment_word_credit_pronoun_hint);
        this.numHint = rootView.findViewById(R.id.fragment_word_credit_number_hint);
        this.artHint = rootView.findViewById(R.id.fragment_word_credit_article_hint);
        this.prepHint = rootView.findViewById(R.id.fragment_word_credit_preposition_hint);
        this.intHint = rootView.findViewById(R.id.fragment_word_credit_int_word_hint);
        this.auxHint = rootView.findViewById(R.id.fragment_word_credit_auxiliary_hint);
        this.exampleSentenceHint = rootView.findViewById(R.id.fragment_word_credit_example_sentence_hint);
        this.phraseHint = rootView.findViewById(R.id.fragment_word_credit_phrase_hint);
        this.distinguishHint = rootView.findViewById(R.id.fragment_word_credit_distinguish_hint);
        this.categorizeOriginHint = rootView.findViewById(R.id.fragment_word_credit_categorize_origin_hint);
        this.adjAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_adjective_answer);
        this.advAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_adverb_answer);
        this.vAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_verb_answer);
        this.viAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_intransitive_verb_answer);
        this.vtAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_transitive_verb_answer);
        this.nAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_noun_answer);
        this.conjAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_conjunction_answer);
        this.pronAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_pronoun_answer);
        this.numAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_number_answer);
        this.artAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_article_answer);
        this.prepAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_preposition_answer);
        this.intAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_int_word_answer);
        this.auxAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_auxiliary_answer);
        this.exampleSentenceAnswer = rootView.findViewById(R.id.fragment_word_credit_example_sentence_answer);
        this.phraseAnswer = rootView.findViewById(R.id.fragment_word_credit_phrase_answer);
        this.distinguishAnswer = rootView.findViewById(R.id.fragment_word_credit_distinguish_answer);
        this.categorizeOriginAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_categorize_origin_answer);
    }

    /**
     * 设置所有组件的监听事件
     */
    private void bindListener() {
        this.popMoreFunction.setOnClickListener(this);
        this.nextWord.setOnClickListener(this);
        this.previousWord.setOnClickListener(this);
        this.getAnswer.setOnClickListener(this);
    }
}