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
    ??????????????????????????????????????????
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
        // ???????????????????????????????????????????????????????????????,???????????????????????????????????????????????????????????????.
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_word_credit, container, false);
        this.updateUIHandler = new Handler();
        /*
        ??????????????????:
        1.??????????????????
        2.??????????????????
        3.?????????????????????????????????,???????????????????????????????????????????????????chineseAnswer??????????????????Gone???,???????????????.
        ??????????????????????????????,??????????????? ?????????????????????????????????????????????????????????????????????.
        4.??????????????????????????????????????????
         */
        // ??????????????????
        bindView();
        // ??????????????????
        bindListener();
        // ??????????????????UI
        emptyUI();
        // ??????Dialog????????????UI??????,???????????????????????????????????????????????????.
        loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null)).setCancelable(false).show();
        // ??????startDrawable?????????
        startDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // ?????????????????????LayoutManager
        this.chineseAnswer.setLayoutManager(new LinearLayoutManager(getContext()));
        this.chineseAnswerDrawer.setLayoutManager(new LinearLayoutManager(getContext()));
        this.starSingleCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        // ????????????
        UserCreditStyleWrapper userCreditStyleWrapper = getArguments().getParcelable("userCreditStyleWrapper");
        this.userCreditStyle = userCreditStyleWrapper.getUserCreditStyle();
        // ????????????????????????,??????Bundle???????????????????????????????????????,???????????????????????????????????????.
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
                Toast toast = Toast.makeText(getContext(), "??????????????????", Toast.LENGTH_SHORT);
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
                new AlertDialog.Builder(getContext()).setTitle("????????????").setMessage("?????????????????????????????????,???????????????1???" + wordFunctionHandler.size() + "????????????.").setView(inputEditText).setCancelable(false).setPositiveButton("??????", (dialog, which) -> {
                    String value = inputEditText.getText().toString();
                    int i;
                    try {
                        i = Integer.parseInt(value);
                        if (i < 0 || i > wordFunctionHandler.size()) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Toast exceptionToast = Toast.makeText(getContext(), "????????????,?????????1~" + wordFunctionHandler.size() + "????????????", Toast.LENGTH_LONG);
                        exceptionToast.setGravity(Gravity.CENTER, 0, 500);
                        exceptionToast.show();
                        return;
                    }
                    creditWord(wordFunctionHandler.jumpToWord(i - 1));
                }).setNegativeButton("??????", (dialog, which) -> {
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
                // ??????????????????????????????????????????????????????????????????,???????????????????????????????????????????????????
                if (wordFunctionHandler.getWordFunctionState() == WordFunctionState.SHUFFLE) {
                    toast = Toast.makeText(getContext(), wordFunctionHandler.getWordFunctionState().getInfo(), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    break;
                }
                // ???????????????,??????????????????????????????????????????????????????????????????
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
                // ???????????????????????????????????????????????????,?????????????????????????????????,????????????????????????????????????
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
                    new AlertDialog.Builder(getContext()).setTitle("????????????:")
                            .setMessage("??????????????????????????????:[1," + wordFunctionHandler.size() + "].????????????????????????")
                            .setView(rangeRandomWordInputView)
                            .setCancelable(false)
                            .setPositiveButton("??????", (dialog, which) -> {
                                int minRange, maxRange;
                                try {
                                    minRange = Integer.parseInt(minValue.getText().toString()) - 1;
                                    maxRange = Integer.parseInt(maxValue.getText().toString()) - 1;
                                    if (minRange < 0 || maxRange > wordFunctionHandler.size() || minRange > maxRange) {
                                        throw new IllegalArgumentException("?????????????????????!");
                                    }
                                } catch (IllegalArgumentException e) {
                                    Toast errorToast = Toast.makeText(getContext(), "????????????,?????????1~" + wordFunctionHandler.size() + "????????????", Toast.LENGTH_LONG);
                                    errorToast.setGravity(Gravity.CENTER, 0, 500);
                                    errorToast.show();
                                    return;
                                }
                                // ??????????????????????????????????????????
                                wordFunctionHandler.shuffleRange(minRange, maxRange);
                                creditWord(wordFunctionHandler.jumpToWord(0));
                                this.shuffleImageView.setForeground(getResources().getDrawable(R.drawable.ic_prohibit_foreground, null));
                                this.sectionImageView.getDrawable().setTint(getResources().getColor(R.color.theme_color, null));
                            })
                            .setNegativeButton("??????", (dialog, which) -> {
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
                        .setMessage("??????????????????")
                        .setCancelable(false)
                        .setPositiveButton("??????", (dialog, which) -> {
                            Navigation.findNavController(getView()).popBackStack();
                        })
                        .setNegativeButton("??????", (dialog, which) -> {

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
                        .setPositiveButton("??????", (dialog, which) -> {
                            WordCategory wordCategory = new WordCategory(categoryTile.getText().toString(),
                                    categoryDescribe.getText().toString(), titleDefault.isChecked(), describeDefault.isChecked());
                            startSingleCategoryAdapter.addItem(wordCategory);
                        })
                        .setNegativeButton("??????", (dialog, which) -> {
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
                // PopWindow??????????????????????????????,?????????changeMode????????????????????????????????????,popWindowChangeModeLayout???????????????????????????.
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
     * ???????????????????????????.
     * ???????????????????????????,?????????????????????,?????????UI??????????????????.
     *
     * @param toBeShowWord ?????????????????????
     */
    private void creditWord(Word toBeShowWord) {
        updateUIHandler.post(() -> {
            switch (wordFunctionHandler.getCurrentCreditState()) {
                case LISTENING:
                    // ???????????????????????????
                    this.chineseAnswer.setVisibility(View.GONE);
                    break;
                case ENGLISH_TRANSLATION_CHINESE:
                    // ??????????????????????????????,??????????????????????????????????????????,????????????????????????????????????
                    visibleWordAllMessage(toBeShowWord);
                    this.chineseAnswer.setVisibility(View.GONE);
                    hideLinearLayoutTree(rootView.findViewById(R.id.fragment_word_credit_answer_area_extra));
                    break;
                case CHINESE_TRANSLATION_ENGLISH:
                    // ???????????????????????????,??????????????????????????????????????????
                    visibleWordAllMessage(toBeShowWord);
                    sourceWord.setText("");
                    sourceWordPhonetics.setText("");
                    break;
                case CREDIT:
                    // ?????????????????????
                    visibleWordAllMessage(toBeShowWord);
                    break;
            }
            /*
            ?????????????????????,??????????????????????????????,???????????????????????????(????????????)?????????.
            ?????????????????????,?????????????????????????????????????????????????????????.
            ??????????????????,???????????????drawer?????????????????????
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
     * ????????????????????????,????????????????????????UI
     */
    private void readAllWord() {
        StaticFactory.getExecutorService().submit(() -> {
            // todo ???????????????????????????????????????????????????????????????????????????????????????????????????WordMessageHandlerImpl????????????,????????????????????????.
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
            // ???????????????????????????
            Bundle bundle = getArguments();
            if (bundle != null) {
                UserCreditStyleWrapper userCreditStyleWrapper = bundle.getParcelable("userCreditStyleWrapper");
                if (userCreditStyleWrapper != null) {
                    this.userCreditStyle = userCreditStyleWrapper.getUserCreditStyle();
                    // TODO ??????????????????????????????????????????,??????????????????
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
            // ???????????????RecyclerView
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.chineseAnswerAdapter = new ChineseAnswerRecyclerViewAdapter(getContext());
            this.chineseAnswerAdapterDrawer = new ChineseAnswerRecyclerViewAdapter(getContext());
            this.startSingleCategoryAdapter = new StartSingleCategoryAdapter(getContext());
            this.chineseAnswerAdapterDrawer.setRecyclerViewState(ChineseAnswerRecyclerViewAdapter.RecyclerViewState.DRAWER);
            // ??????ItemTouchHelper,??????????????????????????????????????????
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
     * ????????????????????????????????????UI
     */
    private void emptyUI() {
        // ?????????????????????????????????
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
     * ???????????????????????????????????????
     */
    private void closeFlagChangeAreaFlush() {
        clickFlagImageView.setImageResource(R.drawable.flag_close);
        viewFlagArea.setPadding(0, 0, 5, 0);
        // ??????????????????????????????,????????????????????????View?????????????????????
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
     * ???????????????????????????????????????
     */
    private void openFlagChangeAreaFlush() {
        clickFlagImageView.setImageResource(R.drawable.flag_open);
        viewFlagArea.setPadding(5, 0, 0, 0);
        // ??????????????????????????????,????????????????????????View?????????????????????
        for (int i = 0; i < viewFlagArea.getChildCount(); i++) {
            View child = viewFlagArea.getChildAt(i);
            child.setVisibility(View.VISIBLE);
            child.setAlpha(0.0f);
            // 10dp???px?????????
            ((LinearLayout.LayoutParams) child.getLayoutParams()).setMargins(0, DPUtils.dp2px(10), 0, DPUtils.dp2px(10));
        }
        for (FlagColor flagColor : wordFunctionHandler.getCurrentWordFlagColor()) {
            viewFlagArea.getChildAt(flagColor.ordinal()).setAlpha(1.0f);
        }
    }

    /**
     * ??????????????????????????????????????????
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
     * ?????????????????????????????????,<b>??????????????????????????????????????????????????????????????????.</b><br>
     * ???????????????????????????????????????,?????????????????????????????????,???????????????????????????(??????????????????????????????)?????????????????????????????????.
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
     * ??????????????????
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
     * ?????????????????????????????????
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

    // ----?????????????????????????????????----
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