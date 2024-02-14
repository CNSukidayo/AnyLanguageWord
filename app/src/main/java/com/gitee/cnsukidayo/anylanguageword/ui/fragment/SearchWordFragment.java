package com.gitee.cnsukidayo.anylanguageword.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
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

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.entity.WordCategory;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.handler.CategoryFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.impl.AbstractCategoryFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.ui.MainActivity;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.ChineseAnswerRecyclerViewAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.SimpleItemTouchHelperCallback;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.StartSingleCategoryAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.wordsearch.SelectWordListAdapter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;

import io.github.cnsukidayo.wword.common.request.factory.CoreServiceRequestFactory;
import io.github.cnsukidayo.wword.common.request.factory.SearchServiceRequestFactory;
import io.github.cnsukidayo.wword.model.dto.WordDTO;
import io.github.cnsukidayo.wword.model.dto.WordStructureDTO;
import io.github.cnsukidayo.wword.model.dto.es.WordESDTO;
import io.github.cnsukidayo.wword.model.dto.support.DataPage;
import io.github.cnsukidayo.wword.model.params.SearchWordParam;

/**
 * @author sukidayo
 * @date Thursday, February 09, 2023
 */
public class SearchWordFragment extends Fragment implements View.OnClickListener, KeyEvent.Callback {

    private View rootView;
    // 界面标题
    private TextView title;
    private ImageButton backToTrace;
    private AlertDialog loadingDialog = null;
    private RecyclerView chineseAnswer, chineseAnswerDrawer, starSingleCategory;
    private ChineseAnswerRecyclerViewAdapter chineseAnswerAdapter;
    private ChineseAnswerRecyclerViewAdapter chineseAnswerAdapterDrawer;
    private StartSingleCategoryAdapter startSingleCategoryAdapter;
    private Handler updateUIHandler;
    private LinearLayout analysisWord, openStartDrawer;
    private TextView sourceWord, sourceWordPhonetics, drawerSourceWord, drawerSourceWordPhonetics;
    private TextView drawerPhraseHint, drawerPhraseAnswer, addNewCategory;
    // 收藏界抽屉布局
    private DrawerLayout startDrawer;
    private final CategoryFunctionHandler categoryFunctionHandler = new AbstractCategoryFunctionHandler() {
        @Override
        public List<WordDTO> getCurrentWord() {
            // todo 错误 查询单词的逻辑要推翻重做
            return null;
        }
    };
    // 对于本类来说,如果当前的格式是经典模式,则代表当前展示的功能是查词功能,否则当前是联想模式,不包含查词的功能
    private SearchView searchInput;
    // 当前查询单词的结构
    private List<WordStructureDTO> currentWordStructure;
    // 上一次查询的文本
    private String preQueryText;
    // 每隔1s执行单词查询
    private Timer queryTimer;
    // 单词搜索列表
    private RecyclerView selectWordList;
    // 单词搜索列表的adapter
    private SelectWordListAdapter selectWordListAdapter;
    // 用户搜索单词事件
    private final ConcurrentLinkedDeque<SearchWordParam> eventQueue = new ConcurrentLinkedDeque<>();
    // 当前正在使用的单词搜索事件
    private SearchWordParam searchWordEvent = null;

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
        queryTimer.cancel();
        queryTimer = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        if (itemId == R.id.toolbar_back_to_trace) {
            Navigation.findNavController(getView()).popBackStack();
        } else if (itemId == R.id.fragment_search_word_click_analysis_word) {

        } else if (itemId == R.id.fragment_search_word_start_add) {
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
        } else if (itemId == R.id.fragment_search_word_search_view) {
            searchInput.setIconified(false);
            searchInput.requestFocus();
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

    private void initView() {
        this.title.setText(R.string.search_word);
        this.queryTimer = new Timer();
        ((MainActivity) requireActivity()).setFragmentWindowSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        updateUIHandler = new Handler();
        startDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // 隐藏单词的附加显示内容、隐藏收藏界面的答案信息
        hideLinearLayoutTree(rootView.findViewById(R.id.fragment_search_word_answer_area));
        sourceWord.setText("");
        sourceWordPhonetics.setText("");
        drawerSourceWord.setText("");
        drawerSourceWordPhonetics.setText("");
        drawerPhraseHint.setVisibility(View.GONE);
        drawerPhraseAnswer.setVisibility(View.GONE);
        loadingDialog = new AlertDialog.Builder(getContext()).setView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading, null)).setCancelable(false).show();
        // 各种recycleView
        this.chineseAnswer.setLayoutManager(new LinearLayoutManager(getContext()));
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
            this.chineseAnswerAdapterDrawer = new ChineseAnswerRecyclerViewAdapter(getContext(), currentWordStructure);
            this.startSingleCategoryAdapter = new StartSingleCategoryAdapter(getContext());
            // 初始化单词列表的adapter
            this.selectWordListAdapter = new SelectWordListAdapter(getContext(), currentWordStructure);

            // 绑定ItemTouchHelper,实现单个列表的编辑删除等功能
            ItemTouchHelper touchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(startSingleCategoryAdapter));
            startSingleCategoryAdapter.setStartDragListener(touchHelper::startDrag);
            startSingleCategoryAdapter.setStartFunctionHandler(categoryFunctionHandler);
            updateUIHandler.post(() -> {
                this.chineseAnswer.setAdapter(chineseAnswerAdapter);
                this.chineseAnswerDrawer.setAdapter(chineseAnswerAdapterDrawer);
                this.starSingleCategory.setAdapter(startSingleCategoryAdapter);
                // 设置单词选择列表的adapter
                this.selectWordList.setAdapter(selectWordListAdapter);
                touchHelper.attachToRecyclerView(starSingleCategory);
                this.chineseAnswer.setVisibility(View.GONE);
                this.chineseAnswerDrawer.setVisibility(View.GONE);
                loadingDialog.dismiss();
            });
        });
        // 每隔1s查询单词
        this.queryTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(searchInput.getQuery()) &&
                        !searchInput.getQuery().toString().equals(preQueryText)) {
                    // 保存本次查询的字符串
                    preQueryText = searchInput.getQuery().toString();
                    // 查询单词
                    SearchWordParam searchWordParam = new SearchWordParam();
                    searchWordParam.setWord(preQueryText);
                    searchWordParam.setSize(20);
                    searchWordParam.setCurrent(1);
                    searchWordParam.setLanguageId(2L);
                    eventQueue.add(searchWordParam);
                }
                // 使用阻塞队列保证最近的一个操作执行
                SearchWordParam lastSearchWordEvent = eventQueue.pollLast();
                eventQueue.clear();
                if (lastSearchWordEvent != null) {
                    // 更新搜索列表
                    searchWordEvent = lastSearchWordEvent;
                    SearchServiceRequestFactory.getInstance()
                            .wordSearchRequest()
                            .searchWord(lastSearchWordEvent)
                            .success(data -> {
                                DataPage<WordESDTO> wordESDTODataPage = data.getData();
                                updateUIHandler.post(() -> {
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
                }
            }
        }, 0, 2000);
        // 检测当前单词搜索列表是否滑动到底部需要加载更多的单词
        selectWordList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    // 滑动到底部
                    SearchWordParam searchWordParam = new SearchWordParam();
                    // 因为不对searchWordEvent进行修改所以不存在线程安全问题
                    searchWordParam.setLanguageId(searchWordEvent.getLanguageId());
                    searchWordParam.setWord(searchWordEvent.getWord());
                    searchWordParam.setSize(searchWordEvent.getSize());
                    searchWordParam.setCurrent(searchWordEvent.getCurrent() + 1);
                    eventQueue.add(searchWordParam);
                }
            }
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
        this.title = rootView.findViewById(R.id.toolbar_title);
        this.sourceWord = rootView.findViewById(R.id.fragment_search_word_source_word);
        this.sourceWordPhonetics = rootView.findViewById(R.id.fragment_search_word_phonetics);
        this.chineseAnswer = rootView.findViewById(R.id.fragment_search_word_chinese_answer);
        this.analysisWord = rootView.findViewById(R.id.fragment_search_word_click_analysis_word);
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
        this.searchInput = rootView.findViewById(R.id.fragment_search_word_search_view);
        this.selectWordList = rootView.findViewById(R.id.fragment_search_word_select_recycler_view);

        this.searchInput.setOnClickListener(this);
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