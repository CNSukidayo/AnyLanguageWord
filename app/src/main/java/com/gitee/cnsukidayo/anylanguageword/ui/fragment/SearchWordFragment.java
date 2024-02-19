package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.handler.CategoryFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.impl.AbstractCategoryFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.ui.MainActivity;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.ChineseAnswerRecyclerViewAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.SimpleItemTouchHelperCallback;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.StartChineseAnswerRecyclerViewAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.StartSingleCategoryAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.listener.RecycleViewItemClickCallBack;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.wordsearch.SelectWordListAdapter;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import io.github.cnsukidayo.wword.common.request.factory.CoreServiceRequestFactory;
import io.github.cnsukidayo.wword.common.request.factory.SearchServiceRequestFactory;
import io.github.cnsukidayo.wword.model.dto.WordCategoryDTO;
import io.github.cnsukidayo.wword.model.dto.WordCategoryWordDTO;
import io.github.cnsukidayo.wword.model.dto.WordDTO;
import io.github.cnsukidayo.wword.model.dto.WordStructureDTO;
import io.github.cnsukidayo.wword.model.dto.es.WordESDTO;
import io.github.cnsukidayo.wword.model.dto.support.DataPage;
import io.github.cnsukidayo.wword.model.params.SearchWordParam;
import io.github.cnsukidayo.wword.model.params.WordCategoryParam;

/**
 * @author sukidayo
 * @date Thursday, February 09, 2023
 */
public class SearchWordFragment extends Fragment implements View.OnClickListener,
        KeyEvent.Callback,
        RecycleViewItemClickCallBack<WordESDTO>, SearchView.OnQueryTextListener {

    private View rootView;
    // 界面标题
    private TextView title;
    private ImageButton backToTrace;
    private AlertDialog loadingDialog = null;
    private RecyclerView chineseAnswer, chineseAnswerDrawer, starSingleCategory;
    private ChineseAnswerRecyclerViewAdapter chineseAnswerAdapter;
    // 收藏夹列表单词显示适配器
    private StartChineseAnswerRecyclerViewAdapter chineseAnswerAdapterDrawer;
    private StartSingleCategoryAdapter startSingleCategoryAdapter;
    private Handler updateUIHandler;
    private LinearLayout analysisWord, openStarDrawer;
    private TextView sourceWord, sourceWordPhonetics, sourceWordDrawer, sourceWordPhoneticsDrawer;
    private TextView drawerPhraseHint, drawerPhraseAnswer, addNewCategory;
    // 收藏界抽屉布局
    private DrawerLayout startDrawer;
    private final CategoryFunctionHandler categoryFunctionHandler = new AbstractCategoryFunctionHandler() {
        @Override
        public WordCategoryWordDTO getCurrentViewWord() {
            return currentViewWord;
        }
    };
    // 对于本类来说,如果当前的格式是经典模式,则代表当前展示的功能是查词功能,否则当前是联想模式,不包含查词的功能
    private SearchView searchInput;
    // 当前查询单词的结构
    private List<WordStructureDTO> currentWordStructure;
    // 执行器
    private volatile Timer queryTimer;
    // 单词搜索列表
    private RecyclerView selectWordList;
    // 单词搜索列表的adapter
    private SelectWordListAdapter selectWordListAdapter;
    // 当前正在使用的单词搜索事件
    private SearchWordParam searchWordEvent = null;
    // 当前选中的单词
    private WordCategoryWordDTO currentViewWord = null;
    // 当前是否是最后一页,为了防止重复请求
    private volatile boolean lastList = false;
    // 判断当前是否有任务正在执行
    private volatile boolean isRunning = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            initView();
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_search_word, container, false);
        bindView();
        initView();
        return rootView;
    }

    @Override
    public void onDestroy() {
        if (queryTimer != null) {
            queryTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.toolbar_back_to_trace) {
            Navigation.findNavController(getView()).popBackStack();
        } else if (itemId == R.id.fragment_search_word_click_analysis_word) {

        } else if (itemId == R.id.drawer_start_add_category) {
            View addNewCategory = getLayoutInflater().inflate(R.layout.fragment_word_credit_start_edit_new_dialog, null);
            EditText categoryTile = addNewCategory.findViewById(R.id.fragment_word_credit_start_new_title);
            EditText categoryDescribe = addNewCategory.findViewById(R.id.fragment_word_credit_start_new_describe);
            new AlertDialog.Builder(getContext())
                    .setView(addNewCategory)
                    .setCancelable(true)
                    .setPositiveButton("确定", (dialog, which) -> {
                        WordCategoryParam wordCategoryParam = new WordCategoryParam();
                        wordCategoryParam.setTitle(categoryTile.getText().toString());
                        wordCategoryParam.setDescribeInfo(categoryDescribe.getText().toString());
                        StaticFactory.getExecutorService().submit(() -> CoreServiceRequestFactory.getInstance()
                                .wordCategoryRequest()
                                .save(wordCategoryParam)
                                .success(data -> {
                                    WordCategoryDTO wordCategoryDTO = data.getData();
                                    updateUIHandler.post(() -> startSingleCategoryAdapter.addItem(wordCategoryDTO));
                                })
                                .execute());
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                    })
                    .show();
        } else if (itemId == R.id.fragment_search_word_search_view) {
            searchInput.setIconified(false);
            searchInput.requestFocus();
        } else if (itemId == R.id.fragment_search_word_click_star) {
            starSingleCategory.setVisibility(View.VISIBLE);
            startDrawer.openDrawer(GravityCompat.END);
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

    @Override
    public void viewClickCallBack(WordESDTO wordESDTO) {
        // 设置搜索单词的回调事件
        if (queryTimer != null) {
            queryTimer.cancel();
            isRunning = false;
        }
        // 将当前搜索得到的单词转换为选中的单词
        this.currentViewWord = new WordCategoryWordDTO();
        currentViewWord.setWordId(wordESDTO.getWordId());
        StaticFactory.getExecutorService().submit(() -> {
            // 查询单词的详细信息
            CoreServiceRequestFactory.getInstance()
                    .wordRequest()
                    .selectWordById(wordESDTO.getWordId())
                    .success(data -> {
                        // 得到单词详情信息
                        List<WordDTO> currentSelectWord = data.getData();
                        // 首先转换成以单词结构id为Key的集合
                        Map<Long, List<WordDTO>> structureWordMap = currentSelectWord.stream()
                                .collect(Collectors.groupingBy(WordDTO::getWordStructureId, Collectors.toList()));
                        Map<Long, Map<Long, List<WordDTO>>> cacheWord = new HashMap<>();
                        cacheWord.put(wordESDTO.getWordId(), structureWordMap);
                        // 将单词添加到查询缓存中
                        categoryFunctionHandler.addWordQueryCache(cacheWord);
                        updateUIHandler.post(() -> visibleWordAllMessage(structureWordMap));
                    })
                    .execute();
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return onQueryTextChange(query);
    }

    /**
     * 搜索列表监听事件
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        // 监听用户的搜索
        if (!TextUtils.isEmpty(newText)) {
            // 保存本次查询的字符串
            // 查询单词
            SearchWordParam searchWordParam = new SearchWordParam();
            searchWordParam.setWord(newText);
            searchWordParam.setSize(20);
            searchWordParam.setCurrent(1);
            searchWordParam.setLanguageId(2L);
            searchWordEvent = searchWordParam;
            if (queryTimer != null) {
                queryTimer.cancel();
                isRunning = false;
            }
            this.queryTimer = new Timer();
            isRunning = true;
            this.queryTimer.schedule(getQueryTask(), 1000);
        }
        return false;
    }

    private void initView() {
        this.title.setText(R.string.search_word);
        ((MainActivity) requireActivity()).setFragmentWindowSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        updateUIHandler = new Handler();
        startDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // 隐藏单词的附加显示内容、隐藏收藏界面的答案信息
        hideLinearLayoutTree(rootView.findViewById(R.id.fragment_search_word_answer_area));
        sourceWord.setText("");
        sourceWordPhonetics.setText("");
        sourceWordDrawer.setText("");
        sourceWordPhoneticsDrawer.setText("");
        drawerPhraseHint.setVisibility(View.GONE);
        drawerPhraseAnswer.setVisibility(View.GONE);
        loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null)).setCancelable(false).show();
        // 中文结果显示的RecyclerView需要使用FlexboxLayoutManager
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getContext());
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        flexboxLayoutManager.setAlignItems(AlignItems.FLEX_START);
        this.chineseAnswer.setLayoutManager(flexboxLayoutManager);
        this.chineseAnswerDrawer.setLayoutManager(new LinearLayoutManager(getContext()));
        this.starSingleCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        this.selectWordList.setLayoutManager(new LinearLayoutManager(getContext()));

        StaticFactory.getExecutorService().submit(() -> {
            // 获取所有的单词结构
            CoreServiceRequestFactory.getInstance()
                    .wordStructureRequest()
                    .selectWordStructureById("2")
                    .success(data -> currentWordStructure = data.getData())
                    .execute();
            this.chineseAnswerAdapter = new ChineseAnswerRecyclerViewAdapter(getContext(), currentWordStructure);
            this.startSingleCategoryAdapter = new StartSingleCategoryAdapter(getContext());
            // 初始化单词列表的adapter
            this.selectWordListAdapter = new SelectWordListAdapter(getContext(), currentWordStructure);
            // 设置选中单词的回调事件
            this.selectWordListAdapter.setRecycleViewItemClickCallBack(this);
            // 绑定ItemTouchHelper,实现单个列表的编辑删除等功能
            ItemTouchHelper touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(startSingleCategoryAdapter));
            startSingleCategoryAdapter.setStartDragListener(touchHelper::startDrag);
            startSingleCategoryAdapter.setStartFunctionHandler(categoryFunctionHandler);
            // 获取当前用户的所有收藏夹信息
            CoreServiceRequestFactory.getInstance()
                    .wordCategoryRequest()
                    .list()
                    .success(data -> categoryFunctionHandler.batchAddCategory(data.getData()))
                    .execute();
            updateUIHandler.post(() -> {
                this.chineseAnswer.setAdapter(chineseAnswerAdapter);
                Optional.ofNullable(((SimpleItemAnimator) this.chineseAnswer.getItemAnimator())).ifPresent(simpleItemAnimator -> {
                    simpleItemAnimator.setSupportsChangeAnimations(false);
                    chineseAnswer.setItemAnimator(null);
                    chineseAnswer.setHasFixedSize(false);
                });
                this.chineseAnswerDrawer.setAdapter(chineseAnswerAdapterDrawer);
                this.starSingleCategory.setAdapter(startSingleCategoryAdapter);
                // 设置单词选择列表的adapter
                this.selectWordList.setAdapter(selectWordListAdapter);
                touchHelper.attachToRecyclerView(starSingleCategory);
                this.chineseAnswer.setVisibility(View.GONE);
                loadingDialog.dismiss();
            });
        });
        // 检测当前单词搜索列表是否滑动到底部需要加载更多的单词
        selectWordList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 滑动到底部
                if (!recyclerView.canScrollVertically(1) && !lastList) {
                    // 优先执行搜索事件或者已有的分页查询事件
                    if (!isRunning) {
                        searchWordEvent.setCurrent(searchWordEvent.getCurrent() + 1);
                        queryTimer = new Timer();
                        queryTimer.schedule(getQueryTask(), 1000);
                    }
                }
            }
        });

    }

    /**
     * 显示当前单词的所有信息,<b>具体当前要根据状态隐藏哪些信息有调用者来处理.</b><br>
     * 该方法展示的是最全面的信息,所有隐藏信息都会被显示,但是单词没有的性质(比如没有某个中文意思)那么没有的内容不会展示.
     */
    private void visibleWordAllMessage(Map<Long, List<WordDTO>> structureWordMap) {
        // 隐藏单词选择列表,显示单词详情列表
        selectWordList.setVisibility(View.GONE);
        chineseAnswer.setVisibility(View.VISIBLE);
        sourceWord.setVisibility(View.VISIBLE);
        sourceWordPhonetics.setVisibility(View.VISIBLE);
        // 设置单词原文
        Optional.ofNullable(structureWordMap.get(EnglishStructure.WORD_ORIGIN.getWordStructureId()))
                .ifPresent(wordDTOS -> sourceWord
                        .setText(wordDTOS.size() > 0 && wordDTOS.get(0).getValue() != null ? wordDTOS.get(0).getValue() : ""));
        // 设置单词音标
        Optional.ofNullable(structureWordMap.get(EnglishStructure.UK_PHONETIC.getWordStructureId()))
                .ifPresent(wordDTOS -> sourceWordPhonetics
                        .setText(wordDTOS.size() > 0 && wordDTOS.get(0).getValue() != null ? wordDTOS.get(0).getValue() : ""));
        // 设置右侧展开列表单词的原文
        Optional.ofNullable(structureWordMap.get(EnglishStructure.WORD_ORIGIN.getWordStructureId()))
                .ifPresent(wordDTOS -> sourceWordDrawer
                        .setText(wordDTOS.size() > 0 && wordDTOS.get(0).getValue() != null ? wordDTOS.get(0).getValue() : ""));
        // 设置右侧展开列表单词的音标
        Optional.ofNullable(structureWordMap.get(EnglishStructure.UK_PHONETIC.getWordStructureId()))
                .ifPresent(wordDTOS -> sourceWordPhoneticsDrawer
                        .setText(wordDTOS.size() > 0 && wordDTOS.get(0).getValue() != null ? wordDTOS.get(0).getValue() : ""));
        // 设置短语
        String phraseTranslation = Optional.ofNullable(structureWordMap.get(EnglishStructure.PHRASE_TRANSLATION.getWordStructureId()))
                .map(phraseWord -> phraseWord.size() > 0 ? phraseWord.get(0).getValue() : "")
                .orElse("");
        Optional.ofNullable(structureWordMap.get(EnglishStructure.PHRASE.getWordStructureId()))
                .ifPresentOrElse(wordDTOS -> {
                    drawerPhraseAnswer.setText(wordDTOS.size() > 0 && wordDTOS.get(0).getValue() != null ? wordDTOS.get(0).getValue() : "" +
                            " " +
                            phraseTranslation);
                    drawerPhraseHint.setVisibility(View.VISIBLE);
                    drawerPhraseAnswer.setVisibility(View.VISIBLE);
                }, () -> {
                    drawerPhraseHint.setVisibility(View.GONE);
                    drawerPhraseAnswer.setVisibility(View.GONE);
                });
        // 设置收藏夹翻译的中文
        this.chineseAnswerAdapterDrawer = new StartChineseAnswerRecyclerViewAdapter(getContext(), structureWordMap);
        this.chineseAnswerDrawer.setAdapter(chineseAnswerAdapterDrawer);
        // 设置中文翻译列表
        chineseAnswerAdapter.showWordChineseMessage(structureWordMap);
        // todo 在这里设置recycleView的宽度
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
     * 获得具体的查询函数
     */
    private TimerTask getQueryTask() {
        return new TimerTask() {
            @Override
            public void run() {
                SearchServiceRequestFactory.getInstance()
                        .wordSearchRequest()
                        .searchWord(searchWordEvent)
                        .success(data -> {
                            DataPage<WordESDTO> wordESDTODataPage = data.getData();
                            updateUIHandler.post(() -> {
                                // 显示单词搜索列表,隐藏单词详情列表
                                selectWordList.setVisibility(View.VISIBLE);
                                chineseAnswer.setVisibility(View.GONE);
                                sourceWord.setVisibility(View.GONE);
                                sourceWordPhonetics.setVisibility(View.GONE);
                                lastList = wordESDTODataPage.isLast();
                                if (wordESDTODataPage.isFirst()) {
                                    // 如果是第一页就替换
                                    selectWordListAdapter.replaceAllWithDataPage(wordESDTODataPage);
                                } else {
                                    // 否则就添加所有数据到集合中
                                    selectWordListAdapter.addAllWithDataPage(wordESDTODataPage);
                                }
                            });
                        })
                        .execute();
                isRunning = false;
            }
        };

    }

    private void bindView() {
        this.backToTrace = rootView.findViewById(R.id.toolbar_back_to_trace);
        this.title = rootView.findViewById(R.id.toolbar_title);
        this.sourceWord = rootView.findViewById(R.id.fragment_search_word_source_word);
        this.sourceWordPhonetics = rootView.findViewById(R.id.fragment_search_word_phonetics);
        this.chineseAnswer = rootView.findViewById(R.id.fragment_search_word_chinese_answer);
        this.analysisWord = rootView.findViewById(R.id.fragment_search_word_click_analysis_word);
        this.startDrawer = rootView.findViewById(R.id.fragment_search_word_start_drawer);
        this.openStarDrawer = rootView.findViewById(R.id.fragment_search_word_click_star);
        this.chineseAnswerDrawer = rootView.findViewById(R.id.drawer_star_chinese_answer_recycler_view);
        this.starSingleCategory = rootView.findViewById(R.id.start_category_recycler);
        this.sourceWordDrawer = rootView.findViewById(R.id.fragment_word_credit_drawer_word_origin);
        this.sourceWordPhoneticsDrawer = rootView.findViewById(R.id.fragment_word_credit_drawer_word_phonetics);
        this.drawerPhraseHint = rootView.findViewById(R.id.drawer_star_phrase_hint);
        this.drawerPhraseAnswer = rootView.findViewById(R.id.drawer_star_phrase_answer);
        this.addNewCategory = rootView.findViewById(R.id.drawer_start_add_category);
        this.searchInput = rootView.findViewById(R.id.fragment_search_word_search_view);
        this.selectWordList = rootView.findViewById(R.id.fragment_search_word_select_recycler_view);

        this.searchInput.setOnClickListener(this);
        this.searchInput.setOnQueryTextListener(this);
        this.backToTrace.setOnClickListener(this);
        this.analysisWord.setOnClickListener(this);
        this.openStarDrawer.setOnClickListener(this);
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