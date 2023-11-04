package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.AnyLanguageWordProperties;
import com.gitee.cnsukidayo.anylanguageword.entity.Word;
import com.gitee.cnsukidayo.anylanguageword.entity.WordCategory;
import com.gitee.cnsukidayo.anylanguageword.entity.waper.UserCreditStyleWrapper;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditFormat;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.handler.AssociationModeHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.CategoryFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.impl.AbstractCategoryFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.impl.AssociationModeHandlerImpl;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.ChineseAnswerRecyclerViewAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.FlagClickRecyclerViewAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.FlagDecorateRecyclerViewAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.SimpleItemTouchHelperCallback;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.StartSingleCategoryAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.gitee.cnsukidayo.anylanguageword.utils.AnimationUtil;
import com.gitee.cnsukidayo.anylanguageword.utils.DPUtils;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.cnsukidayo.wword.model.dto.WordDTO;

/**
 * @author sukidayo
 * @date Thursday, February 09, 2023
 */
public class SearchWordFragment extends Fragment implements View.OnClickListener, KeyEvent.Callback, RecycleViewItemClickCallBack<FlagColor> {

    private View rootView;
    private ImageButton backToTrace;
    private ImageView clickFlagImageView, saveProgressImageView;
    private AlertDialog loadingDialog = null;
    private RecyclerView chineseAnswer, chineseAnswerDrawer, starSingleCategory, flagAreaRecyclerView, flagDecorateRecyclerView;
    private ChineseAnswerRecyclerViewAdapter chineseAnswerAdapter;
    private ChineseAnswerRecyclerViewAdapter chineseAnswerAdapterDrawer;
    private StartSingleCategoryAdapter startSingleCategoryAdapter;
    private Handler updateUIHandler;
    private LinearLayout analysisWord, openStartDrawer, association, queryWord, flagLinearLayout, saveProgress;
    private Word testWord = null;
    private TextView sourceWord, sourceWordPhonetics, drawerSourceWord, drawerSourceWordPhonetics;
    private TextView exampleSentenceAnswer, exampleSentenceHint, phraseAnswer, phraseHint, distinguishAnswer, distinguishHint, categorizeOriginAnswer, categorizeOriginHint;
    private TextView drawerPhraseHint, drawerPhraseAnswer, addNewCategory, getAnswer;
    private DrawerLayout startDrawer;
    private final CategoryFunctionHandler categoryFunctionHandler = new AbstractCategoryFunctionHandler() {
        @Override
        public List<WordDTO> getCurrentWord() {
            // todo 错误 查询单词的逻辑要推翻重做
            return null;
        }
    };
    // 对于本类来说,如果当前的格式是经典模式,则代表当前展示的功能是查词功能,否则当前是联想模式,不包含查词的功能
    private CreditFormat creditFormat = CreditFormat.CLASSIC;
    private FlagClickRecyclerViewAdapter flagAreaAdapter;
    private FlagDecorateRecyclerViewAdapter flagDecorateAdapter;
    private AssociationModeHandler associationModeHandler;
    private SearchView searchInput;

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
        Log.d("message", "click");
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
//                showWord(testWord);
                break;
            case R.id.fragment_search_word_click_flag:
                setFlagAreaStatus(!flagAreaAdapter.isOpen());
                break;
            case R.id.fragment_search_word_click_start:
                startDrawer.openDrawer(GravityCompat.END);
                break;
            case R.id.fragment_search_word_click_association:
                creditFormat = CreditFormat.ASSOCIATION;
                updateStatus();
                break;
            case R.id.fragment_search_word_click_query_word:
                creditFormat = CreditFormat.CLASSIC;
                updateStatus();
                break;
            case R.id.fragment_search_word_get_answer:
                /*
                associationModeHandler.checkWord(searchInput.getQuery().toString()).ifPresentOrElse(this::showWord, () -> {
                    Toast failEquals = Toast.makeText(getContext(), getContext().getResources().getString(R.string.fail_equal), Toast.LENGTH_SHORT);
                    failEquals.setGravity(Gravity.CENTER, 0, 500);
                    failEquals.show();
                });

                 */
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


    private List<FlagColor> flagColorsList = new ArrayList<>(FlagColor.values().length);

    @Override
    public void viewClickCallBack(FlagColor flagColor) {
        // 点击旗帜的回调事件
        if (flagColor == FlagColor.BROWN) {
            return;
        }
        boolean flag = true;
        for (int i = 0; i < flagColorsList.size(); i++) {
            if (flagColorsList.get(i) == flagColor) {
                flagColorsList.remove(i);
                flag = false;
                break;
            }
        }
        if (flag) {
            flagColorsList.add(flagColor);
        }
        flagDecorateAdapter.setFlagStatus(flagColorsList);
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

    private void showWord(List<WordDTO> toBeShowWord) {
        /*
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
         */
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
        // 根据当前进入的方式,选择要对应展现的View
        Bundle bundle = getArguments();
        UserCreditStyleWrapper userCreditStyleWrapper;
        if (bundle == null) {
            this.creditFormat = CreditFormat.CLASSIC;
            this.association.setVisibility(View.GONE);
            this.flagLinearLayout.setVisibility(View.GONE);
            this.queryWord.setVisibility(View.GONE);
            this.getAnswer.setVisibility(View.GONE);
            this.flagAreaRecyclerView.setVisibility(View.GONE);
            this.flagDecorateRecyclerView.setVisibility(View.GONE);
            this.saveProgress.setVisibility(View.GONE);
        } else if ((userCreditStyleWrapper = bundle.getParcelable(CreditFragment.USER_CREDIT_STYLE_WRAPPER)) != null) {
            this.creditFormat = userCreditStyleWrapper.getUserCreditStyle().getCreditFormat();
            updateStatus();
        }
        this.chineseAnswer.setLayoutManager(new LinearLayoutManager(getContext()));
        this.chineseAnswerDrawer.setLayoutManager(new LinearLayoutManager(getContext()));
        this.starSingleCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        this.flagAreaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.flagDecorateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        StaticFactory.getExecutorService().submit(() -> {
            // todo 这里正确的做法应该是将代表用户当前所选择的所有待加载的单词列表丢入WordMessageHandlerImpl实现类中,这里暂且先这么做.
            File wordFile = new File(AnyLanguageWordProperties.getExternalFilesDir(), Environment.DIRECTORY_DOCUMENTS + File.separator + "temp");
            for (File singleWordList : wordFile.listFiles()) {
                try {
                    List<Word> wordList = StaticFactory.getGson().fromJson(new BufferedReader(new FileReader(singleWordList)), new TypeToken<List<Word>>() {
                    }.getType());
                    this.associationModeHandler = new AssociationModeHandlerImpl(wordList);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            this.chineseAnswerAdapter = new ChineseAnswerRecyclerViewAdapter(getContext());
            this.chineseAnswerAdapterDrawer = new ChineseAnswerRecyclerViewAdapter(getContext());
            this.startSingleCategoryAdapter = new StartSingleCategoryAdapter(getContext());
            // 实现旗帜功能
            this.flagAreaAdapter = new FlagClickRecyclerViewAdapter(getContext());
            this.flagAreaAdapter.setRecycleViewItemOnClickListener(this);
            this.flagDecorateAdapter = new FlagDecorateRecyclerViewAdapter(getContext());
            this.chineseAnswerAdapterDrawer.setRecyclerViewState(ChineseAnswerRecyclerViewAdapter.RecyclerViewState.DRAWER);
            flagColorsList.add(FlagColor.BROWN);
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
                this.flagAreaRecyclerView.setAdapter(flagAreaAdapter);
                this.flagDecorateRecyclerView.setAdapter(flagDecorateAdapter);
                touchHelper.attachToRecyclerView(starSingleCategory);
                updateUIHandler.post(() -> {
                    this.chineseAnswer.setVisibility(View.GONE);
                    this.chineseAnswerDrawer.setVisibility(View.GONE);
                    setFlagAreaStatus(false);
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

    private void updateStatus() {
        ImageView associationImageView = (ImageView) association.getChildAt(0);
        TextView associationTextView = (TextView) association.getChildAt(1);
        ImageView queryWordImageView = (ImageView) queryWord.getChildAt(0);
        TextView queryWordTextView = (TextView) queryWord.getChildAt(1);
        if (creditFormat == CreditFormat.CLASSIC) {
            associationImageView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_gray, null)));
            associationTextView.setTextColor(getResources().getColor(R.color.dark_gray, null));
            queryWordImageView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.theme_color, null)));
            queryWordTextView.setTextColor(getResources().getColor(R.color.theme_color, null));
            this.getAnswer.setVisibility(View.GONE);
            this.clickFlagImageView.setForeground(getResources().getDrawable(R.drawable.ic_prohibit_foreground, null));
            this.flagLinearLayout.setClickable(false);
            this.saveProgressImageView.setForeground(getResources().getDrawable(R.drawable.ic_prohibit_foreground, null));
            this.saveProgress.setClickable(false);
            setFlagAreaStatus(false);
            this.flagDecorateRecyclerView.setVisibility(View.GONE);
        } else {
            associationImageView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.theme_color, null)));
            associationTextView.setTextColor(getResources().getColor(R.color.theme_color, null));
            queryWordImageView.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_gray, null)));
            queryWordTextView.setTextColor(getResources().getColor(R.color.dark_gray, null));
            this.getAnswer.setVisibility(View.VISIBLE);
            this.clickFlagImageView.setForeground(null);
            this.flagLinearLayout.setClickable(true);
            this.saveProgressImageView.setForeground(null);
            this.saveProgress.setClickable(true);
            this.flagDecorateRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 执行该方法不一定会成功,也就是动画无法打断
     *
     * @param targetStatus 想要改成的目标状态
     */
    private void setFlagAreaStatus(boolean targetStatus) {
        if (!targetStatus) {
            if (AnimationUtil.with().moveToViewEnd(flagAreaRecyclerView, 500)) {
                flagAreaAdapter.setOpened(false);
                clickFlagImageView.setImageResource(R.drawable.flag_close);
                for (int i = 0; i < flagDecorateRecyclerView.getChildCount(); i++) {
                    LinearLayout linearLayout = (LinearLayout) flagDecorateRecyclerView.getChildAt(i);
                    linearLayout.getChildAt(0).setVisibility(View.GONE);
                    ((RecyclerView.LayoutParams) linearLayout.getLayoutParams()).setMargins(0, 0, 0, 0);
                }
                for (FlagColor flagColor : flagColorsList) {
                    ((LinearLayout) flagDecorateRecyclerView.getChildAt(flagColor.ordinal())).getChildAt(0).setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (AnimationUtil.with().endMoveToViewLocation(flagAreaRecyclerView, 500)) {
                flagAreaAdapter.setOpened(true);
                clickFlagImageView.setImageResource(R.drawable.flag_open);
                for (int i = 0; i < flagDecorateRecyclerView.getChildCount(); i++) {
                    LinearLayout linearLayout = (LinearLayout) flagDecorateRecyclerView.getChildAt(i);
                    linearLayout.getChildAt(0).setVisibility(View.VISIBLE);
                    ((RecyclerView.LayoutParams) linearLayout.getLayoutParams()).setMargins(0, DPUtils.dp2px(10), 0, DPUtils.dp2px(10));
                }
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
        this.association = rootView.findViewById(R.id.fragment_search_word_click_association);
        this.queryWord = rootView.findViewById(R.id.fragment_search_word_click_query_word);
        this.flagLinearLayout = rootView.findViewById(R.id.fragment_search_word_click_flag);
        this.getAnswer = rootView.findViewById(R.id.fragment_search_word_get_answer);
        this.clickFlagImageView = rootView.findViewById(R.id.fragment_search_word_imageview_click_flag);
        this.flagAreaRecyclerView = rootView.findViewById(R.id.fragment_search_word_flag_recycler_view);
        this.searchInput = rootView.findViewById(R.id.fragment_home_search_view);
        this.saveProgress = rootView.findViewById(R.id.fragment_search_word_save_progress);
        this.saveProgressImageView = rootView.findViewById(R.id.fragment_search_word_imageview_save_progress);
        this.flagDecorateRecyclerView = rootView.findViewById(R.id.fragment_search_word_flag_decorate_recycler_view);

        this.backToTrace.setOnClickListener(this);
        this.analysisWord.setOnClickListener(this);
        this.openStartDrawer.setOnClickListener(this);
        this.addNewCategory.setOnClickListener(this);
        this.association.setOnClickListener(this);
        this.queryWord.setOnClickListener(this);
        this.flagLinearLayout.setOnClickListener(this);
        this.getAnswer.setOnClickListener(this);
        this.saveProgress.setOnClickListener(this);
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