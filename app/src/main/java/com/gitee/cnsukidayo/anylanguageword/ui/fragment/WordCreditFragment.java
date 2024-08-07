package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.AnyLanguageWordProperties;
import com.gitee.cnsukidayo.anylanguageword.entity.UserCreditStyle;
import com.gitee.cnsukidayo.anylanguageword.entity.Word;
import com.gitee.cnsukidayo.anylanguageword.entity.WordCategory;
import com.gitee.cnsukidayo.anylanguageword.entity.waper.UserCreditStyleWrapper;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditFilter;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditOrder;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditState;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;
import com.gitee.cnsukidayo.anylanguageword.enums.WordFunctionState;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.handler.WordFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.impl.WordFunctionHandlerImpl;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.ChineseAnswerRecyclerViewAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.SimpleItemTouchHelperCallback;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.StartSingleCategoryAdapter;
import com.gitee.cnsukidayo.anylanguageword.utils.AnimationUtil;
import com.gitee.cnsukidayo.anylanguageword.utils.DPUtils;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WordCreditFragment extends Fragment implements View.OnClickListener, KeyEvent.Callback {
    private View rootView;

    private ImageButton popMoreFunction;
    private HorizontalScrollView moreFunctionHorizontalScrollView;
    private boolean moreFunctionOpen = true, openFlagChange, changingChameleon;
    private Handler updateUIHandler;
    private WordFunctionHandler wordFunctionHandler;
    private DrawerLayout startDrawer;
    private RecyclerView chineseAnswer, chineseAnswerDrawer, starSingleCategory;
    private ChineseAnswerRecyclerViewAdapter chineseAnswerAdapter, chineseAnswerAdapterDrawer;
    private StartSingleCategoryAdapter startSingleCategoryAdapter;
    /*
    以下是所有功能按钮的变量声明
     */
    private ImageButton nextWord, previousWord, popBackStack, playWord;
    private TextView sourceWord, sourceWordPhonetics, getAnswer, exampleSentenceHint, phraseHint, distinguishHint, categorizeOriginHint;
    private TextView exampleSentenceAnswer, phraseAnswer, distinguishAnswer, categorizeOriginAnswer, currentIndexTextView, wordCount;
    private TextView sourceWordDrawer, sourceWordPhoneticsDrawer, phraseHintDrawer, phraseAnswerDrawer, addNewStartCategory;
    private AlertDialog loadingDialog = null;
    private LinearLayout jumpNextWord, flagChangeArea, clickFlag, viewFlagArea, chameleonMode, shuffle, section, changeMode, start, searchWord;
    private CardView popWindowChangeModeLayout;
    private ImageView clickFlagImageView, chameleonImageView, shuffleImageView, sectionImageView;
    private TextView listeningWriteMode, englishTranslationChineseMode, chineseTranslationEnglish, onlyCreditMode;
    private long exitLastTime = 0;
    private UserCreditStyle userCreditStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 如果是从主页进入背词页面是一定会从新加载的,如果是从查词界面进入背词界面则不能重新加载.
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_word_credit, container, false);
        this.updateUIHandler = new Handler();
        /*
        调用流程明细:
        1.绑定相关组件
        2.设置监听事件
        3.一开始隐藏不必要的组件,注意隐藏答案区的时候是不需要手动将chineseAnswer的可见性设为Gone的,否则会出错.
        不管一开始是什么状态,都需要隐藏 英文原文、英文音标、单词翻译答案区域、附加内容.
        4.获取答案时就是展示所有的内容
         */
        // 获取相关组件
        bindView();
        // 设置监听事件
        bindListener();
        // 隐藏不必要的UI
        emptyUI();
        // 弹出Dialog不要阻塞UI线程,通过一个新的线程去请求所有单词信息.
        loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null)).setCancelable(false).show();
        // 锁定startDrawable的关闭
        startDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // 必须在这里设置LayoutManager
        this.chineseAnswer.setLayoutManager(new LinearLayoutManager(getContext()));
        this.chineseAnswerDrawer.setLayoutManager(new LinearLayoutManager(getContext()));
        this.starSingleCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        // 读取状态
        UserCreditStyleWrapper userCreditStyleWrapper = getArguments().getParcelable("userCreditStyleWrapper");
        this.userCreditStyle = userCreditStyleWrapper.getUserCreditStyle();
        // 读取所有单词信息,通过Bundle得到当前用户选中的单词分类,这里暂时以样本单词进行测试.
        readAllWord();
        return rootView;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (startDrawer.isDrawerOpen(GravityCompat.END)) {
                startDrawer.closeDrawer(GravityCompat.END);
                return true;
            }
            if (System.currentTimeMillis() - exitLastTime > 2000) {
                Toast toast = Toast.makeText(getContext(), "再按一次退出", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                exitLastTime = System.currentTimeMillis();
            } else {
                Navigation.findNavController(popBackStack).popBackStack();
            }
        }
        return true;
    }


    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    @Override
    public void onClick(View v) {
        Toast toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        PopupWindow changeModePopupWindow = null;
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
                emptyUI();
                creditWord(wordFunctionHandler.jumpNextWord());
                break;
            case R.id.fragment_word_credit_previous_word:
                emptyUI();
                creditWord(wordFunctionHandler.jumpPreviousWord());
                break;
            case R.id.fragment_word_container_get_answer:
                visibleWordAllMessage(wordFunctionHandler.getCurrentWord());
                break;
            case R.id.fragment_word_credit_play_word:
                creditWord(wordFunctionHandler.getCurrentWord());
                break;
            case R.id.fragment_word_credit_jump_next:
                final EditText inputEditText = new EditText(getContext());
                inputEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                new AlertDialog.Builder(getContext()).setTitle("跳转单词").setMessage("输入要跳转到第几个单词,你应当输入1到" + wordFunctionHandler.size() + "之间的值.").setView(inputEditText).setCancelable(false).setPositiveButton("确定", (dialog, which) -> {
                    String value = inputEditText.getText().toString();
                    int i;
                    try {
                        i = Integer.parseInt(value);
                        if (i < 0 || i > wordFunctionHandler.size()) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast exceptionToast = Toast.makeText(getContext(), "输入错误,请输入1~" + wordFunctionHandler.size() + "之间的值", Toast.LENGTH_LONG);
                        exceptionToast.setGravity(Gravity.CENTER, 0, 500);
                        exceptionToast.show();
                        return;
                    }
                    creditWord(wordFunctionHandler.jumpToWord(i - 1));
                }).setNegativeButton("取消", (dialog, which) -> {
                }).show();
                break;
            case R.id.fragment_word_credit_click_flag:
                if (openFlagChange) {
                    if (AnimationUtil.with().moveToViewEnd(flagChangeArea, 500)) {
                        closeFlagChangeAreaFlush();
                        openFlagChange = !openFlagChange;
                    }
                } else {
                    if (AnimationUtil.with().endMoveToViewLocation(flagChangeArea, 500)) {
                        openFlagChangeAreaFlush();
                        openFlagChange = !openFlagChange;
                    }
                }
                break;
            case R.id.fragment_word_credit_click_chameleon_mode:
                // 如果当前处于按色打乱模式则无法使用变色龙功能,使用区间重背功能可以使用变色龙功能
                if (wordFunctionHandler.getWordFunctionState() == WordFunctionState.SHUFFLE) {
                    toast = Toast.makeText(getContext(), wordFunctionHandler.getWordFunctionState().getInfo(), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    break;
                }
                // 不要管太多,当点击变色龙模式的时候就展开旗帜区域就可以了
                if (!openFlagChange) {
                    if (AnimationUtil.with().endMoveToViewLocation(flagChangeArea, 500)) {
                        openFlagChangeAreaFlush();
                        openFlagChange = !openFlagChange;
                    }
                }
                toast.cancel();
                toast = Toast.makeText(getContext(), R.string.please_click_flag_change_chameleon, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                changingChameleon = true;
                break;
            case R.id.fragment_word_credit_click_shuffle:
                // 如果当前不是普通状态和按色打乱状态,代表当前在执行别的状态,需要先锁定按色打乱的功能
                if (wordFunctionHandler.getWordFunctionState() == WordFunctionState.RANGE) {
                    toast = Toast.makeText(getContext(), wordFunctionHandler.getWordFunctionState().getInfo(), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    break;
                }
                if (wordFunctionHandler.getWordFunctionState() == WordFunctionState.NONE) {
                    this.chameleonImageView.setForeground(getResources().getDrawable(R.drawable.ic_prohibit_foreground, null));
                    this.sectionImageView.setForeground(getResources().getDrawable(R.drawable.ic_prohibit_foreground, null));
                    this.shuffleImageView.getDrawable().setTint(getResources().getColor(wordFunctionHandler.getChameleon().getMapColorID(), null));
                    wordFunctionHandler.shuffle();
                    creditWord(wordFunctionHandler.jumpToWord(0));
                } else {
                    this.chameleonImageView.setForeground(null);
                    this.sectionImageView.setForeground(null);
                    this.shuffleImageView.getDrawable().setTintList(null);
                    wordFunctionHandler.restoreWordList();
                    creditWord(wordFunctionHandler.jumpToWord(0));
                }
                break;
            case R.id.fragment_word_credit_click_section:
                if (wordFunctionHandler.getWordFunctionState() == WordFunctionState.SHUFFLE) {
                    toast = Toast.makeText(getContext(), wordFunctionHandler.getWordFunctionState().getInfo(), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    break;
                }
                if (wordFunctionHandler.getWordFunctionState() == WordFunctionState.NONE) {
                    View rangeRandomWordInputView = getLayoutInflater().inflate(R.layout.fragment_word_credit_dialog_section, null);
                    EditText minValue = rangeRandomWordInputView.findViewById(R.id.fragment_word_credit_dialog_section_min_value);
                    EditText maxValue = rangeRandomWordInputView.findViewById(R.id.fragment_word_credit_dialog_section_max_value);
                    new AlertDialog.Builder(getContext()).setTitle("区间随机:")
                            .setMessage("选择要单独随机的区间:[1," + wordFunctionHandler.size() + "].注意这里是闭区间")
                            .setView(rangeRandomWordInputView)
                            .setCancelable(false)
                            .setPositiveButton("确定", (dialog, which) -> {
                                int minRange, maxRange;
                                try {
                                    minRange = Integer.parseInt(minValue.getText().toString()) - 1;
                                    maxRange = Integer.parseInt(maxValue.getText().toString()) - 1;
                                    if (minRange < 0 || maxRange > wordFunctionHandler.size() || minRange > maxRange) {
                                        throw new IllegalArgumentException("输入参数不合法!");
                                    }
                                } catch (IllegalArgumentException e) {
                                    Toast errorToast = Toast.makeText(getContext(), "输入错误,请输入1~" + wordFunctionHandler.size() + "之间的值", Toast.LENGTH_LONG);
                                    errorToast.setGravity(Gravity.CENTER, 0, 500);
                                    errorToast.show();
                                    return;
                                }
                                // 确定执行区间随机时执行的内容
                                wordFunctionHandler.shuffleRange(minRange, maxRange);
                                creditWord(wordFunctionHandler.jumpToWord(0));
                                this.shuffleImageView.setForeground(getResources().getDrawable(R.drawable.ic_prohibit_foreground, null));
                                this.sectionImageView.getDrawable().setTint(getResources().getColor(R.color.theme_color, null));
                            })
                            .setNegativeButton("取消", (dialog, which) -> {
                            })
                            .show();
                } else {
                    this.shuffleImageView.setForeground(null);
                    this.sectionImageView.getDrawable().setTintList(null);
                    this.wordFunctionHandler.restoreWordList();
                    creditWord(wordFunctionHandler.jumpToWord(0));
                }
                break;
            case R.id.toolbar_back_to_trace:
                new AlertDialog.Builder(getContext())
                        .setMessage("确认返回主页")
                        .setCancelable(false)
                        .setPositiveButton("确定", (dialog, which) -> {
                            Navigation.findNavController(getView()).popBackStack();
                        })
                        .setNegativeButton("取消", (dialog, which) -> {

                        })
                        .show();
                break;
            case R.id.fragment_word_credit_click_start:
                startDrawer.openDrawer(GravityCompat.END);
                break;
            case R.id.fragment_word_credit_start_add:
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
            case R.id.fragment_word_credit_click_change_mode:
                if (changeModePopupWindow == null) {
                    changeModePopupWindow = new PopupWindow(popWindowChangeModeLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    changeModePopupWindow.setOutsideTouchable(true);
                    changeModePopupWindow.setFocusable(true);
                    changeModePopupWindow.setAnimationStyle(R.style.pop_window_anim_style);
                    changeModePopupWindow.setOnDismissListener(() -> ((ViewGroup) rootView.getParent()).removeView(popWindowChangeModeLayout));
                }
                // PopWindow展示在某个组件的上方,这里的changeMode代表要展示在那个组件上方,popWindowChangeModeLayout代表要展示哪个组件.
                popWindowChangeModeLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int[] location = new int[2];
                changeMode.getLocationInSurface(location);
                changeModePopupWindow.showAtLocation(changeMode, Gravity.NO_GRAVITY,
                        (location[0] + changeMode.getWidth() / 2) - popWindowChangeModeLayout.getMeasuredWidth() / 2,
                        location[1] - popWindowChangeModeLayout.getMeasuredHeight());
                break;
            case R.id.fragment_word_credit_search_word:
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_word_credit_to_navigation_search_word, null, StaticFactory.getSimpleNavOptions());
                break;
            case R.id.fragment_word_credit_pop_listening_write_mode:
                wordFunctionHandler.setCurrentCreditState(CreditState.LISTENING);
                updateChangeModePopWindowState();
                break;
            case R.id.fragment_word_credit_pop_english_translation_chinese:
                wordFunctionHandler.setCurrentCreditState(CreditState.ENGLISH_TRANSLATION_CHINESE);
                updateChangeModePopWindowState();
                break;
            case R.id.fragment_word_credit_pop_chinese_translation_english:
                wordFunctionHandler.setCurrentCreditState(CreditState.CHINESE_TRANSLATION_ENGLISH);
                updateChangeModePopWindowState();
                break;
            case R.id.fragment_word_credit_pop_only_credit:
                wordFunctionHandler.setCurrentCreditState(CreditState.CREDIT);
                updateChangeModePopWindowState();
                break;
            case R.id.fragment_word_credit_button_flag_green:
                if (changingChameleon) {
                    changingChameleon = false;
                    wordFunctionHandler.setChameleon(FlagColor.GREEN);
                    this.nextWord.getDrawable().setTint(getResources().getColor(R.color.theme_color, null));
                    creditWord(wordFunctionHandler.jumpToWord(0));
                    wordCount.setText(String.valueOf(wordFunctionHandler.size()));
                    break;
                }
                if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.GREEN)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_green).setAlpha(0.0f);
                } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.GREEN)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_green).setAlpha(1.0f);
                }
                break;
            case R.id.fragment_word_credit_button_flag_red:
                if (changingChameleon) {
                    changingChameleon = false;
                    wordFunctionHandler.setChameleon(FlagColor.RED);
                    this.nextWord.getDrawable().setTint(getResources().getColor(android.R.color.holo_red_dark, null));
                    creditWord(wordFunctionHandler.jumpToWord(0));
                    wordCount.setText(String.valueOf(wordFunctionHandler.size()));
                    break;
                }
                if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.RED)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_red).setAlpha(0.0f);
                } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.RED)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_red).setAlpha(1.0f);
                }
                break;
            case R.id.fragment_word_credit_button_flag_orange:
                if (changingChameleon) {
                    changingChameleon = false;
                    wordFunctionHandler.setChameleon(FlagColor.ORANGE);
                    this.nextWord.getDrawable().setTint(getResources().getColor(android.R.color.holo_orange_dark, null));
                    creditWord(wordFunctionHandler.jumpToWord(0));
                    wordCount.setText(String.valueOf(wordFunctionHandler.size()));
                    break;
                }
                if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.ORANGE)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_orange).setAlpha(0.0f);
                } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.ORANGE)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_orange).setAlpha(1.0f);
                }
                break;
            case R.id.fragment_word_credit_button_flag_yellow:
                if (changingChameleon) {
                    changingChameleon = false;
                    wordFunctionHandler.setChameleon(FlagColor.YELLOW);
                    this.nextWord.getDrawable().setTint(getResources().getColor(R.color.holo_yellow_dark, null));
                    creditWord(wordFunctionHandler.jumpToWord(0));
                    wordCount.setText(String.valueOf(wordFunctionHandler.size()));

                    break;
                }
                if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.YELLOW)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_yellow).setAlpha(0.0f);
                } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.YELLOW)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_yellow).setAlpha(1.0f);
                }
                break;
            case R.id.fragment_word_credit_button_flag_blue:
                if (changingChameleon) {
                    changingChameleon = false;
                    wordFunctionHandler.setChameleon(FlagColor.BLUE);
                    this.nextWord.getDrawable().setTint(getResources().getColor(android.R.color.holo_blue_dark, null));
                    creditWord(wordFunctionHandler.jumpToWord(0));
                    wordCount.setText(String.valueOf(wordFunctionHandler.size()));

                    break;
                }
                if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.BLUE)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_blue).setAlpha(0.0f);
                } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.BLUE)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_blue).setAlpha(1.0f);
                }
                break;
            case R.id.fragment_word_credit_button_flag_cyan:
                if (changingChameleon) {
                    changingChameleon = false;
                    wordFunctionHandler.setChameleon(FlagColor.CYAN);
                    this.nextWord.getDrawable().setTint(getResources().getColor(R.color.holo_cyan_dark, null));
                    creditWord(wordFunctionHandler.jumpToWord(0));
                    wordCount.setText(String.valueOf(wordFunctionHandler.size()));

                    break;
                }
                if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.CYAN)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_cyan).setAlpha(0.0f);
                } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.CYAN)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_cyan).setAlpha(1.0f);
                }
                break;
            case R.id.fragment_word_credit_button_flag_purple:
                if (changingChameleon) {
                    changingChameleon = false;
                    wordFunctionHandler.setChameleon(FlagColor.PURPLE);
                    this.nextWord.getDrawable().setTint(getResources().getColor(android.R.color.holo_purple, null));
                    creditWord(wordFunctionHandler.jumpToWord(0));
                    wordCount.setText(String.valueOf(wordFunctionHandler.size()));
                    break;
                }
                if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.PURPLE)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_purple).setAlpha(0.0f);
                } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.PURPLE)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_purple).setAlpha(1.0f);
                }
                break;
            case R.id.fragment_word_credit_button_flag_pink:
                if (changingChameleon) {
                    changingChameleon = false;
                    wordFunctionHandler.setChameleon(FlagColor.PINK);
                    this.nextWord.getDrawable().setTint(getResources().getColor(R.color.holo_pink_dark, null));
                    creditWord(wordFunctionHandler.jumpToWord(0));
                    wordCount.setText(String.valueOf(wordFunctionHandler.size()));
                    break;
                }
                if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.PINK)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_pink).setAlpha(0.0f);
                } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.PINK)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_pink).setAlpha(1.0f);
                }
                break;
            case R.id.fragment_word_credit_button_flag_gray:
                if (changingChameleon) {
                    changingChameleon = false;
                    wordFunctionHandler.setChameleon(FlagColor.GRAY);
                    this.nextWord.getDrawable().setTint(getResources().getColor(R.color.dark_gray, null));
                    creditWord(wordFunctionHandler.jumpToWord(0));
                    wordCount.setText(String.valueOf(wordFunctionHandler.size()));
                    break;
                }
                if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.GRAY)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_gray).setAlpha(0.0f);
                } else if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.GRAY)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_gray).setAlpha(1.0f);
                }
                break;
            case R.id.fragment_word_credit_button_flag_black:
                if (changingChameleon) {
                    changingChameleon = false;
                    wordFunctionHandler.setChameleon(FlagColor.BLACK);
                    this.nextWord.getDrawable().setTint(getResources().getColor(android.R.color.black, null));
                    creditWord(wordFunctionHandler.jumpToWord(0));
                    wordCount.setText(String.valueOf(wordFunctionHandler.size()));
                    break;
                }
                if (wordFunctionHandler.addFlagToCurrentWord(FlagColor.BLACK)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_black).setAlpha(0.0f);
                } else if (wordFunctionHandler.removeFlagToCurrentWord(FlagColor.BLACK)) {
                    rootView.findViewById(R.id.fragment_word_credit_view_flag_black).setAlpha(1.0f);
                }
                break;
            case R.id.fragment_word_credit_button_flag_brown:
                if (changingChameleon) {
                    changingChameleon = false;
                    wordFunctionHandler.setChameleon(FlagColor.BROWN);
                    this.nextWord.getDrawable().setTint(getResources().getColor(R.color.halo_brown_dark, null));
                    creditWord(wordFunctionHandler.jumpToWord(0));
                    wordCount.setText(String.valueOf(wordFunctionHandler.size()));
                }
                break;
        }
    }

    /**
     * 按模式展示某个单词.
     * 展示单词是一种状态,随着单词的变化,页面的UI也要跟随变化.
     *
     * @param toBeShowWord 待被展示的单词
     */
    private void creditWord(Word toBeShowWord) {
        updateUIHandler.post(() -> {
            switch (wordFunctionHandler.getCurrentCreditState()) {
                case LISTENING:
                    // 听写模式只播放音频
                    this.chineseAnswer.setVisibility(View.GONE);
                    break;
                case ENGLISH_TRANSLATION_CHINESE:
                    // 先展示单词的所有信息,然后将单词的中文意思进行隐藏,再将单词额外信息进行隐藏
                    visibleWordAllMessage(toBeShowWord);
                    this.chineseAnswer.setVisibility(View.GONE);
                    hideLinearLayoutTree(rootView.findViewById(R.id.fragment_word_credit_answer_area_extra));
                    break;
                case CHINESE_TRANSLATION_ENGLISH:
                    // 先展示所有单词信息,然后将英文原文和音标进行隐藏
                    visibleWordAllMessage(toBeShowWord);
                    sourceWord.setText("");
                    sourceWordPhonetics.setText("");
                    break;
                case CREDIT:
                    // 不隐藏任何信息
                    visibleWordAllMessage(toBeShowWord);
                    break;
            }
            /*
            不管是什么状态,如果当前旗帜是打开的,那么都需要刷新旗帜(颜色标记)的状态.
            不管是什么状态,都需要显示当前背诵的位置和总的单词个数.
            不管什么状态,都需要更新drawer里面单词的内容
             */
            chineseAnswerAdapterDrawer.showWordChineseMessage(toBeShowWord);
            this.sourceWordDrawer.setText(toBeShowWord.getWordOrigin());
            this.sourceWordPhoneticsDrawer.setText(toBeShowWord.getWordPhonetics());
            if (!TextUtils.isEmpty(toBeShowWord.getPhrase())) {
                phraseAnswerDrawer.setText(toBeShowWord.getPhrase());
                phraseHintDrawer.setVisibility(View.VISIBLE);
                phraseAnswerDrawer.setVisibility(View.VISIBLE);
            } else {
                phraseHintDrawer.setVisibility(View.GONE);
                phraseAnswerDrawer.setVisibility(View.GONE);
            }
            currentIndexTextView.setText(String.valueOf(wordFunctionHandler.getCurrentOrder() + 1));
            wordCount.setText(String.valueOf(wordFunctionHandler.size()));
            if (openFlagChange) {
                openFlagChangeAreaFlush();
            } else {
                closeFlagChangeAreaFlush();
            }
        });
    }

    /**
     * 读取所有单词信息,读取完毕之后更新UI
     */
    private void readAllWord() {
        StaticFactory.getExecutorService().submit(() -> {
            // todo 这里正确的做法应该是将代表用户当前所选择的所有待加载的单词列表丢入WordMessageHandlerImpl实现类中,这里暂且先这么做.
            File wordFile = new File(AnyLanguageWordProperties.getExternalFilesDir(), Environment.DIRECTORY_DOCUMENTS + File.separator + "temp");
            List<Word> wordList = null;
            for (File singleWordList : wordFile.listFiles()) {
                try {
                    wordList = StaticFactory.getGson().fromJson(new BufferedReader(new FileReader(singleWordList)), new TypeToken<List<Word>>() {

                    }.getType());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            // 根据自定义风格背诵
            Bundle bundle = getArguments();
            if (bundle != null) {
                UserCreditStyleWrapper userCreditStyleWrapper = bundle.getParcelable("userCreditStyleWrapper");
                if (userCreditStyleWrapper != null) {
                    this.userCreditStyle = userCreditStyleWrapper.getUserCreditStyle();
                    // TODO 这个地方可以用责任链设计模式,暂且先这么写
                    if (userCreditStyle.getCreditOrder() == CreditOrder.DISORDER) {
                        Collections.shuffle(wordList);
                    } else if (userCreditStyle.getCreditOrder() == CreditOrder.LEXICOGRAPHIC) {
                        wordList.sort((o1, o2) -> o1.getWordOrigin().compareTo(o2.getWordOrigin()));
                    }
                    if (userCreditStyle.getCreditFilter() == CreditFilter.PHRASE) {
                        wordList = wordList.stream().filter(word -> !TextUtils.isEmpty(word.getPhrase())).collect(Collectors.toList());
                    }
                }
            }
            this.wordFunctionHandler = new WordFunctionHandlerImpl(wordList);
            if (userCreditStyle != null) {
                this.wordFunctionHandler.setCurrentCreditState(userCreditStyle.getCreditState());
            }
            // 初始化所有RecyclerView
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.chineseAnswerAdapter = new ChineseAnswerRecyclerViewAdapter(getContext());
            this.chineseAnswerAdapterDrawer = new ChineseAnswerRecyclerViewAdapter(getContext());
            this.startSingleCategoryAdapter = new StartSingleCategoryAdapter(getContext());
            this.chineseAnswerAdapterDrawer.setRecyclerViewState(ChineseAnswerRecyclerViewAdapter.RecyclerViewState.DRAWER);
            // 绑定ItemTouchHelper,实现单个列表的编辑删除等功能
            ItemTouchHelper touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(startSingleCategoryAdapter));
            startSingleCategoryAdapter.setStartDragListener(touchHelper::startDrag);
            startSingleCategoryAdapter.setStartFunctionHandler(wordFunctionHandler);
            updateUIHandler.post(() -> {
                this.chineseAnswer.setAdapter(chineseAnswerAdapter);
                this.chineseAnswerDrawer.setAdapter(chineseAnswerAdapterDrawer);
                this.starSingleCategory.setAdapter(startSingleCategoryAdapter);
                touchHelper.attachToRecyclerView(starSingleCategory);
                creditWord(wordFunctionHandler.getWordByOrder(0));
                wordCount.setText(String.valueOf(wordFunctionHandler.size()));
                flagChangeArea.setVisibility(View.GONE);
                closeFlagChangeAreaFlush();
                updateChangeModePopWindowState();
                loadingDialog.dismiss();
            });
        });
    }


    /**
     * 隐藏所有暂时不必要出现的UI
     */
    private void emptyUI() {
        // 隐藏单词的附加显示内容
        hideLinearLayoutTree(rootView.findViewById(R.id.fragment_word_credit_answer_area_extra));
        sourceWord.setText("");
        sourceWordPhonetics.setText("");
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

    /**
     * 关闭旗帜改变区域显示时调用
     */
    private void closeFlagChangeAreaFlush() {
        clickFlagImageView.setImageResource(R.drawable.flag_close);
        viewFlagArea.setPadding(0, 0, 5, 0);
        // 最开始除了绿色旗帜外,所有旗帜的对应的View全部置为不显示
        for (int i = 0; i < viewFlagArea.getChildCount(); i++) {
            viewFlagArea.getChildAt(i).setVisibility(View.GONE);
            ((LinearLayout.LayoutParams) viewFlagArea.getChildAt(i).getLayoutParams()).setMargins(0, 0, 0, 0);
        }
        for (FlagColor flagColor : wordFunctionHandler.getCurrentWordFlagColor()) {
            viewFlagArea.getChildAt(flagColor.ordinal()).setVisibility(View.VISIBLE);
            viewFlagArea.getChildAt(flagColor.ordinal()).setAlpha(1.0f);
        }
    }

    /**
     * 打开旗帜改变区域显示时调用
     */
    private void openFlagChangeAreaFlush() {
        clickFlagImageView.setImageResource(R.drawable.flag_open);
        viewFlagArea.setPadding(5, 0, 0, 0);
        // 最开始除了绿色旗帜外,所有旗帜的对应的View全部置为不显示
        for (int i = 0; i < viewFlagArea.getChildCount(); i++) {
            View child = viewFlagArea.getChildAt(i);
            child.setVisibility(View.VISIBLE);
            child.setAlpha(0.0f);
            // 10dp转px的方法
            ((LinearLayout.LayoutParams) child.getLayoutParams()).setMargins(0, DPUtils.dp2px(10), 0, DPUtils.dp2px(10));
        }
        for (FlagColor flagColor : wordFunctionHandler.getCurrentWordFlagColor()) {
            viewFlagArea.getChildAt(flagColor.ordinal()).setAlpha(1.0f);
        }
    }

    /**
     * 更新弹出窗口中所有按钮的状态
     */
    private void updateChangeModePopWindowState() {
        this.listeningWriteMode.setBackground(null);
        this.englishTranslationChineseMode.setBackground(null);
        this.chineseTranslationEnglish.setBackground(null);
        this.onlyCreditMode.setBackground(null);
        CreditState currentCreditState = wordFunctionHandler.getCurrentCreditState();
        if (currentCreditState == CreditState.ENGLISH_TRANSLATION_CHINESE) {
            this.englishTranslationChineseMode.setBackground(getResources().getDrawable(R.drawable.fragment_word_credit_pop_window_change_mode, null));
        } else if (currentCreditState == CreditState.CHINESE_TRANSLATION_ENGLISH) {
            this.chineseTranslationEnglish.setBackground(getResources().getDrawable(R.drawable.fragment_word_credit_pop_window_change_mode, null));
        } else if (currentCreditState == CreditState.LISTENING) {
            this.listeningWriteMode.setBackground(getResources().getDrawable(R.drawable.fragment_word_credit_pop_window_change_mode, null));
        } else if (currentCreditState == CreditState.CREDIT) {
            this.onlyCreditMode.setBackground(getResources().getDrawable(R.drawable.fragment_word_credit_pop_window_change_mode, null));
        }
    }

    /**
     * 显示当前单词的所有信息,<b>具体当前要根据状态隐藏哪些信息有调用者来处理.</b><br>
     * 该方法展示的是最全面的信息,所有隐藏信息都会被显示,但是单词没有的性质(比如没有某个中文意思)那么没有的内容不会展示.
     */
    private void visibleWordAllMessage(Word word) {
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
        this.jumpNextWord = rootView.findViewById(R.id.fragment_word_credit_jump_next);
        this.flagChangeArea = rootView.findViewById(R.id.fragment_word_credit_change_flag_area);
        this.clickFlag = rootView.findViewById(R.id.fragment_word_credit_click_flag);
        this.clickFlagImageView = rootView.findViewById(R.id.fragment_word_credit_imageview_click_flag);
        this.viewFlagArea = rootView.findViewById(R.id.fragment_word_credit_view_flag_area);
        this.chameleonMode = rootView.findViewById(R.id.fragment_word_credit_click_chameleon_mode);
        this.shuffle = rootView.findViewById(R.id.fragment_word_credit_click_shuffle);
        this.chameleonImageView = rootView.findViewById(R.id.fragment_word_credit_imageview_chameleon);
        this.sectionImageView = rootView.findViewById(R.id.fragment_word_credit_imageview_section);
        this.shuffleImageView = rootView.findViewById(R.id.fragment_word_credit_imageview_shuffle);
        this.section = rootView.findViewById(R.id.fragment_word_credit_click_section);
        this.popBackStack = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.changeMode = rootView.findViewById(R.id.fragment_word_credit_click_change_mode);
        this.popWindowChangeModeLayout = (CardView) getLayoutInflater().inflate(R.layout.fragment_word_credit_popwindow_change_mode, null);
        this.listeningWriteMode = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_listening_write_mode);
        this.englishTranslationChineseMode = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_english_translation_chinese);
        this.chineseTranslationEnglish = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_chinese_translation_english);
        this.onlyCreditMode = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_only_credit);
        this.playWord = rootView.findViewById(R.id.fragment_word_credit_play_word);
        this.startDrawer = rootView.findViewById(R.id.fragment_word_credit_start_drawer);
        this.start = rootView.findViewById(R.id.fragment_word_credit_click_start);
        this.starSingleCategory = rootView.findViewById(R.id.fragment_word_credit_start_category_recycler);
        this.addNewStartCategory = rootView.findViewById(R.id.fragment_word_credit_start_add);
        this.searchWord = rootView.findViewById(R.id.fragment_word_credit_search_word);

        this.sourceWordDrawer = rootView.findViewById(R.id.fragment_word_credit_drawer_word_origin);
        this.sourceWordPhoneticsDrawer = rootView.findViewById(R.id.fragment_word_credit_drawer_word_phonetics);
        this.exampleSentenceHint = rootView.findViewById(R.id.fragment_word_credit_example_sentence_hint);
        this.phraseHint = rootView.findViewById(R.id.fragment_word_credit_phrase_hint);
        this.distinguishHint = rootView.findViewById(R.id.fragment_word_credit_distinguish_hint);
        this.categorizeOriginHint = rootView.findViewById(R.id.fragment_word_credit_categorize_origin_hint);
        this.chineseAnswer = rootView.findViewById(R.id.fragment_word_credit_chinese_answer);
        this.chineseAnswerDrawer = rootView.findViewById(R.id.fragment_word_credit_drawer_chinese_answer);
        this.exampleSentenceAnswer = rootView.findViewById(R.id.fragment_word_credit_example_sentence_answer);
        this.phraseAnswer = rootView.findViewById(R.id.fragment_word_credit_phrase_answer);
        this.distinguishAnswer = rootView.findViewById(R.id.fragment_word_credit_distinguish_answer);
        this.categorizeOriginAnswer = rootView.findViewById(R.id.fragment_word_credit_textview_categorize_origin_answer);
        this.phraseHintDrawer = rootView.findViewById(R.id.fragment_word_credit_drawer_phrase_hint);
        this.phraseAnswerDrawer = rootView.findViewById(R.id.fragment_word_credit_drawer_phrase_answer);
    }

    /**
     * 设置所有组件的监听事件
     */
    private void bindListener() {
        this.popMoreFunction.setOnClickListener(this);
        this.nextWord.setOnClickListener(this);
        this.previousWord.setOnClickListener(this);
        this.getAnswer.setOnClickListener(this);
        this.jumpNextWord.setOnClickListener(this);
        this.clickFlag.setOnClickListener(this);
        this.chameleonMode.setOnClickListener(this);
        this.shuffle.setOnClickListener(this);
        this.section.setOnClickListener(this);
        this.popBackStack.setOnClickListener(this);
        this.changeMode.setOnClickListener(this);
        this.listeningWriteMode.setOnClickListener(this);
        this.englishTranslationChineseMode.setOnClickListener(this);
        this.chineseTranslationEnglish.setOnClickListener(this);
        this.onlyCreditMode.setOnClickListener(this);
        this.playWord.setOnClickListener(this);
        this.start.setOnClickListener(this);
        this.addNewStartCategory.setOnClickListener(this);
        this.searchWord.setOnClickListener(this);

        this.rootView.findViewById(R.id.fragment_word_credit_button_flag_green).setOnClickListener(this);
        this.rootView.findViewById(R.id.fragment_word_credit_button_flag_red).setOnClickListener(this);
        this.rootView.findViewById(R.id.fragment_word_credit_button_flag_orange).setOnClickListener(this);
        this.rootView.findViewById(R.id.fragment_word_credit_button_flag_yellow).setOnClickListener(this);
        this.rootView.findViewById(R.id.fragment_word_credit_button_flag_blue).setOnClickListener(this);
        this.rootView.findViewById(R.id.fragment_word_credit_button_flag_cyan).setOnClickListener(this);
        this.rootView.findViewById(R.id.fragment_word_credit_button_flag_purple).setOnClickListener(this);
        this.rootView.findViewById(R.id.fragment_word_credit_button_flag_pink).setOnClickListener(this);
        this.rootView.findViewById(R.id.fragment_word_credit_button_flag_gray).setOnClickListener(this);
        this.rootView.findViewById(R.id.fragment_word_credit_button_flag_black).setOnClickListener(this);
        this.rootView.findViewById(R.id.fragment_word_credit_button_flag_brown).setOnClickListener(this);
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