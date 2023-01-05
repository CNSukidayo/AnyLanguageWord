package com.gitee.cnsukidayo.traditionalenglish.activity.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.context.TraditionalEnglishProperties;
import com.gitee.cnsukidayo.traditionalenglish.entity.Word;
import com.gitee.cnsukidayo.traditionalenglish.enums.CreditState;
import com.gitee.cnsukidayo.traditionalenglish.enums.FlagColor;
import com.gitee.cnsukidayo.traditionalenglish.enums.WordFunctionState;
import com.gitee.cnsukidayo.traditionalenglish.factory.StaticFactory;
import com.gitee.cnsukidayo.traditionalenglish.handler.WordFunctionHandler;
import com.gitee.cnsukidayo.traditionalenglish.handler.WordFunctionHandlerImpl;
import com.gitee.cnsukidayo.traditionalenglish.utils.AnimationUtil;
import com.gitee.cnsukidayo.traditionalenglish.utils.DPUtils;
import com.gitee.cnsukidayo.traditionalenglish.utils.Strings;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WordCreditFragment extends Fragment implements View.OnClickListener {
    private View rootView;

    private ImageButton popMoreFunction;
    private HorizontalScrollView moreFunctionHorizontalScrollView;
    private boolean moreFunctionOpen = true, openFlagChange, changingChameleon;
    private Handler updateUIHandler;
    private WordFunctionHandler wordFunctionHandler;
    private MenuInflater menuInflater;
    private CreditState creditState = CreditState.ENGLISHTRANSLATIONCHINESE;
    /*
    以下是所有功能按钮的变量声明
     */
    private ImageButton nextWord, previousWord, popBackStack, playWord;
    private TextView sourceWord, sourceWordPhonetics, getAnswer, adjHint, advHint, vHint, viHint, vtHint, nHint, conjHint, pronHint, numHint, artHint;
    private TextView prepHint, intHint, auxHint, exampleSentenceHint, phraseHint, distinguishHint, categorizeOriginHint;
    private TextView adjAnswer, advAnswer, vAnswer, viAnswer, vtAnswer, nAnswer, conjAnswer, pronAnswer, numAnswer, artAnswer, prepAnswer;
    private TextView intAnswer, auxAnswer, exampleSentenceAnswer, phraseAnswer, distinguishAnswer, categorizeOriginAnswer, currentIndexTextView, wordCount;
    private AlertDialog loadingDialog = null;
    private LinearLayout jumpNextWord, flagChangeArea, clickFlag, viewFlagArea, chameleonMode, shuffle, section, changeMode, popWindowChangeModeLayout;
    private ImageView clickFlagImageView, chameleonImageView, shuffleImageView, sectionImageView;
    private TextView listeningWriteMode, englishTranslationChineseMode, chineseTranslationEnglish, onlyCreditMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null)).setCancelable(false).show();
        // 读取所有单词信息,通过Bundle得到当前用户选中的单词分类,这里暂时以样本单词进行测试.
        readAllWord();
        // 注册菜上下文菜单栏
        this.menuInflater = new MenuInflater(getContext());
        return rootView;
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
                hideAnswer();
                creditWord(wordFunctionHandler.jumpNextWord());
                break;
            case R.id.fragment_word_credit_previous_word:
                hideAnswer();
                creditWord(wordFunctionHandler.jumpPreviousWord());
                break;
            case R.id.fragment_word_container_get_answer:
                visibleCurrentWordAllMessage();
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
                    this.chameleonImageView.setForeground(getResources().getDrawable(R.drawable.prohibit_foreground, null));
                    this.sectionImageView.setForeground(getResources().getDrawable(R.drawable.prohibit_foreground, null));
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
                                this.shuffleImageView.setForeground(getResources().getDrawable(R.drawable.prohibit_foreground, null));
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
            case R.id.fragment_word_credit_back_to_trace:
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
            case R.id.fragment_word_credit_pop_listening_write_mode:
                wordFunctionHandler.setCurrentCreditState(CreditState.LISTENING);
                clearChangeModePopWindowState();
                this.listeningWriteMode.setBackground(getResources().getDrawable(R.drawable.fragment_word_credit_pop_window_change_mode, null));
                break;
            case R.id.fragment_word_credit_pop_english_translation_chinese:
                wordFunctionHandler.setCurrentCreditState(CreditState.ENGLISHTRANSLATIONCHINESE);
                clearChangeModePopWindowState();
                this.englishTranslationChineseMode.setBackground(getResources().getDrawable(R.drawable.fragment_word_credit_pop_window_change_mode, null));
                break;
            case R.id.fragment_word_credit_pop_chinese_translation_english:
                wordFunctionHandler.setCurrentCreditState(CreditState.CHINESETRANSLATIONENGLISH);
                clearChangeModePopWindowState();
                this.chineseTranslationEnglish.setBackground(getResources().getDrawable(R.drawable.fragment_word_credit_pop_window_change_mode, null));
                break;
            case R.id.fragment_word_credit_pop_only_credit:
                wordFunctionHandler.setCurrentCreditState(CreditState.CREDIT);
                clearChangeModePopWindowState();
                this.onlyCreditMode.setBackground(getResources().getDrawable(R.drawable.fragment_word_credit_pop_window_change_mode, null));
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
     * 直接展示某个单词.
     * 展示单词是一种状态,随着单词的变化,页面的UI也要跟随变化.
     *
     * @param toBeShowWord 待被展示的单词
     */
    private void creditWord(Word toBeShowWord) {
        updateUIHandler.post(() -> {
            switch (wordFunctionHandler.getCurrentCreditState()) {
                case LISTENING:
                    // 听写模式只播放音频,不执行多余的操作.
                    break;
                case ENGLISHTRANSLATIONCHINESE:
                    // 先展示单词的所有信息,然后将单词的中文意思进行隐藏
                    visibleCurrentWordAllMessage();
                    hideChinese();
                    break;
                case CHINESETRANSLATIONENGLISH:
                    // 先展示所有单词信息,然后将英文原文和音标进行隐藏
                    visibleCurrentWordAllMessage();
                    sourceWord.setText("");
                    sourceWordPhonetics.setText("");
                    break;
                case CREDIT:
                    // 不隐藏任何信息
                    visibleCurrentWordAllMessage();
                    break;
            }
            /*
            不管是什么状态,如果当前旗帜是打开的,那么都需要刷新旗帜(颜色标记)的状态.
            不管是什么状态,都需要显示当前背诵的位置和总的单词个数.
             */
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
            File wordFile = new File(TraditionalEnglishProperties.getExternalFilesDir(), Environment.DIRECTORY_DOCUMENTS + File.separator + "temp");
            for (File singleWordList : wordFile.listFiles()) {
                try {
                    List<Word> wordList = StaticFactory.getGson().fromJson(new BufferedReader(new FileReader(singleWordList)), new TypeToken<List<Word>>() {

                    }.getType());
                    this.wordFunctionHandler = new WordFunctionHandlerImpl(wordList);
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
                creditWord(wordFunctionHandler.getWordByOrder(0));
                wordCount.setText(String.valueOf(wordFunctionHandler.size()));
                flagChangeArea.setVisibility(View.GONE);
                closeFlagChangeAreaFlush();
                loadingDialog.dismiss();
            });
        });
    }


    /**
     * 隐藏所有暂时不必要出现的UI
     */
    private void hideAnswer() {
        hideChinese();
        sourceWord.setText("");
        sourceWordPhonetics.setText("");
    }

    /**
     * 隐藏所有的中文
     */
    private void hideChinese() {
        hideLinearLayoutTree(rootView.findViewById(R.id.fragment_word_credit_answer_area));
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
     * 清除模式更改弹出窗口中所有按钮的状态
     */
    private void clearChangeModePopWindowState() {
        this.listeningWriteMode.setBackground(null);
        this.englishTranslationChineseMode.setBackground(null);
        this.chineseTranslationEnglish.setBackground(null);
        this.onlyCreditMode.setBackground(null);
    }

    /**
     * 显示当前单词的所有信息,具体当前要根据状态隐藏哪些信息有调用者来处理.
     */
    private void visibleCurrentWordAllMessage() {
        Word word = wordFunctionHandler.getCurrentWord();
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
        this.sourceWord.setText(word.getWordOrigin());
        this.sourceWordPhonetics.setText(word.getWordPhonetics());
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
        this.popBackStack = rootView.findViewById(R.id.fragment_word_credit_back_to_trace);
        this.changeMode = rootView.findViewById(R.id.fragment_word_credit_click_change_mode);
        this.popWindowChangeModeLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.fragment_word_credit_popwindow_change_mode, null);
        this.listeningWriteMode = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_listening_write_mode);
        this.englishTranslationChineseMode = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_english_translation_chinese);
        this.chineseTranslationEnglish = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_chinese_translation_english);
        this.onlyCreditMode = popWindowChangeModeLayout.findViewById(R.id.fragment_word_credit_pop_only_credit);
        this.playWord = rootView.findViewById(R.id.fragment_word_credit_play_word);

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
}