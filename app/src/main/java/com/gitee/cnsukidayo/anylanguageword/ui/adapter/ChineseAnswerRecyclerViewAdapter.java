package com.gitee.cnsukidayo.anylanguageword.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;
import com.gitee.cnsukidayo.anylanguageword.ui.adapter.support.answer.AnswerElement;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import io.github.cnsukidayo.wword.model.dto.WordDTO;
import io.github.cnsukidayo.wword.model.dto.WordStructureDTO;

public class ChineseAnswerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;

    // 当前的RecyclerViewAdapter是可以复用的,根据不同的界面,组件有不同的样式
    private RecyclerViewState recyclerViewState = RecyclerViewState.MAIN;
    /**
     * 当前单词详细信息的map
     */
    private Map<Long, List<WordDTO>> currentWordMap = new HashMap<>();

    /**
     * 当前单词的结构体
     */
    private final Map<Long, WordStructureDTO> currentWordStructure;
    /**
     * 自定义的单词结构体
     */
    private Map<Long, EnglishStructure> englishStructureMap;
    /**
     * 存储所有单词组装信息的集合
     */
    private List<AnswerElement> answerElementList = new ArrayList<>();

    /**
     * 计算文本宽度的工具
     */
    private final Paint textWidthHandler = new Paint();

    /**
     * 字体的大小
     */
    private final float textSize;

    /**
     * 文本的最大宽度
     */
    private float maxTextWidth;

    /**
     * 暂存的文本长度
     */
    private float previousTextWidth;

    /**
     * key:组件的position<br>
     * value:单词的结构id
     */
    private final Map<Integer, EnglishStructure> positionMap = new HashMap<>(13) {{
        put(0, EnglishStructure.ADJ);
        put(1, EnglishStructure.ADV);
        put(2, EnglishStructure.V);
        put(3, EnglishStructure.VI);
        put(4, EnglishStructure.VT);
        put(5, EnglishStructure.N);
        put(6, EnglishStructure.CONJ);
        put(7, EnglishStructure.PRON);
        put(8, EnglishStructure.NUM);
        put(9, EnglishStructure.ART);
        put(10, EnglishStructure.PREP);
        put(11, EnglishStructure.INT);
        put(12, EnglishStructure.AUX);
    }};

    public ChineseAnswerRecyclerViewAdapter(Context context, List<WordStructureDTO> currentWordStructure) {
        this.context = context;
        this.currentWordStructure = currentWordStructure.stream()
                .collect(Collectors.toMap(WordStructureDTO::getId, wordStructureDTO -> wordStructureDTO));
        this.englishStructureMap = Arrays.stream(EnglishStructure.values())
                .collect(Collectors.toMap(EnglishStructure::getWordStructureId, englishStructure -> englishStructure));
        TextView textView = LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_drawer_chinese_answer_element, null)
                .findViewById(R.id.fragment_word_credit_meaning_category_hint);
        textSize = textView.getTextSize();
        textWidthHandler.setTextSize(textSize);
    }

    /**
     * 展示一个单词的中文意思,该方法只负责将组件展示出来.至于哪些组件要动态地隐藏,由调用者决定.
     *
     * @param currentWordMap 待展示的单词
     */
    public void showWordChineseMessage(Map<Long, List<WordDTO>> currentWordMap) {
        int preCount = answerElementList.size();
        this.currentWordMap = currentWordMap;
        this.removeWordStructure(currentWordMap,
                EnglishStructure.DEFAULT,
                EnglishStructure.US_PHONETIC,
                EnglishStructure.UK_PHONETIC,
                EnglishStructure.WORD_ORIGIN);
        this.answerElementList = viewResolve(currentWordMap);
        notifyItemRangeChanged(0, Math.max(getItemCount(), preCount));
    }

    public void setRecyclerViewState(RecyclerViewState recyclerViewState) {
        this.recyclerViewState = recyclerViewState;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 根据不同的状态,解析不同的页面
        if (viewType == RecyclerViewState.DRAWER.getViewType()) {
            return new AnswerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_drawer_chinese_answer_element, parent, false));
        }
        return new AnswerViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_word_credit_chinese_answer_element, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof AnswerViewHolder) {
            AnswerViewHolder answerViewHolder = (AnswerViewHolder) holder;
            AnswerElement answerElement = answerElementList.get(position);
            answerViewHolder.meaningCategoryHint.setText(answerElement.getKey());
            answerViewHolder.meaningCategoryHint.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = answerViewHolder.itemView.getLayoutParams();
            if (layoutParams instanceof FlexboxLayoutManager.LayoutParams) {
                FlexboxLayoutManager.LayoutParams flexLayoutParams = (FlexboxLayoutManager.LayoutParams) layoutParams;
                // 重置缓存
                flexLayoutParams.setWrapBefore(answerElement.hasBreak());
            }
        } else if (holder instanceof DrawerAnswerViewHolder) {
        }
    }

    @Override
    public int getItemCount() {
        return answerElementList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return recyclerViewState.getViewType();
    }

    /**
     * 得到当前最长的文本宽度
     *
     * @return 返回文本宽度值, 用于动态设置recycleView的宽度
     */
    public float getMaxTextWidth() {
        return maxTextWidth;
    }


    /**
     * 解析视图
     *
     * @param currentWordMap 单词结构体
     */
    private List<AnswerElement> viewResolve(Map<Long, List<WordDTO>> currentWordMap) {
        // 根据结构的key获取到它对应的sort
        List<Long> wordStructureId = currentWordMap.keySet()
                .stream()
                .sorted((o1, o2) -> Optional.ofNullable(englishStructureMap.get(o1))
                        .orElse(EnglishStructure.DEFAULT)
                        .getOrder() - Optional.ofNullable(englishStructureMap.get(o2))
                        .orElse(EnglishStructure.DEFAULT)
                        .getOrder())
                .collect(Collectors.toList());
        // 最终渲染的集合
        List<AnswerElement> renderingCache = new ArrayList<>();
        List<AnswerElement> renderingResult = new ArrayList<>();
        // 用于子节点快速找到父节点
        Map<Long, AnswerElement> nodeCache = new HashMap<>();
        Queue<AnswerElement> bfsQueue = new ArrayDeque<>();
        for (Long structureId : wordStructureId) {
            // 父亲主动找孩子,如果当前找到的节点有groupId则放弃构造,因为会有父节点找到该子节点进行构造
            List<WordDTO> currentWordDTOList = Optional.ofNullable(currentWordMap.get(structureId)).orElse(new ArrayList<>());
            // 得到当前节点对应的结构信息
            EnglishStructure currentEnglishStructure = Optional.ofNullable(englishStructureMap.get(structureId)).orElse(EnglishStructure.DEFAULT);
            // 查看当前结构是否需要添加统一标题
            if (currentEnglishStructure.isAppendTitle()) {
                AnswerElement groupTitle = new AnswerElement.Builder()
                        .key(context.getResources().getString(currentEnglishStructure.getTitleHint()))
                        .order(currentEnglishStructure.getOrder())
                        .answerElementGroup(new ArrayList<>())
                        .hasBreak(currentEnglishStructure.isTitleBreak())
                        .build();
                renderingCache.add(groupTitle);
            }
            // 递归构造树
            for (WordDTO wordDTO : currentWordDTOList) {
                // 获取当前的key提示词
                int hint = Optional.ofNullable(englishStructureMap.get(wordDTO.getWordStructureId()))
                        .orElse(EnglishStructure.DEFAULT)
                        .getTitleHint();
                // 获取当前结构的顺序,保障每个节点展示的顺序
                int order = Optional.ofNullable(englishStructureMap.get(wordDTO.getWordStructureId()))
                        .orElse(EnglishStructure.DEFAULT)
                        .getOrder();
                // 先构造出node,但不一定添加到最终渲染的集合中
                AnswerElement answerElement = new AnswerElement.Builder()
                        .key(wordDTO.getValue())
                        .groupFlag(wordDTO.getGroupFlag())
                        .groupId(wordDTO.getGroupId())
                        .order(order)
                        .hasBreak(currentEnglishStructure.isValueBreak())
                        .answerElementGroup(new ArrayList<>())
                        .build();
                // 如果key值为空则不构造该节点
                if (TextUtils.isEmpty(answerElement.getKey())) continue;

                if (wordDTO.getGroupId() == null) {
                    renderingCache.add(answerElement);
                    nodeCache.put(answerElement.getGroupFlag(), answerElement);
                } else {
                    bfsQueue.add(answerElement);
                }
            }
        }
        int count = bfsQueue.size();
        // 反序排序,因为插入的时候是后来的会插到前面
        bfsQueue = bfsQueue.stream()
                .sorted((o1, o2) -> o2.getOrder() - o1.getOrder())
                .collect(Collectors.toCollection(ArrayDeque::new));
        do {
            AnswerElement answerElement = bfsQueue.poll();
            // 得到当前组id和组对应的节点
            Long groupId = answerElement.getGroupId();
            AnswerElement groupElement = nodeCache.get(groupId);
            if (groupElement != null) {
                groupElement.addChildElement(answerElement);
                nodeCache.put(answerElement.getGroupFlag(), answerElement);
            } else {
                bfsQueue.add(answerElement);
            }
            count--;
            if (count == 0) {
                count = bfsQueue.size();
                bfsQueue = bfsQueue.stream()
                        .sorted((o1, o2) -> o2.getOrder() - o1.getOrder())
                        .collect(Collectors.toCollection(ArrayDeque::new));
            }
        } while (count != 0);
        // 构造渲染集合,渲染集合的顺序就是最终展示的顺序,可以通过渲染集合来获取当前最长的文本
        for (AnswerElement answerElement : renderingCache) {
            dfsAddRenderResult(renderingResult, answerElement);
        }
        // 计算最长文本宽度
        for (AnswerElement computeNode : renderingResult) {
            previousTextWidth += textWidthHandler.measureText(computeNode.getKey());
            if (computeNode.hasBreak()) {
                previousTextWidth = 0;
                maxTextWidth = Math.max(previousTextWidth, maxTextWidth);
            }
        }
        return renderingResult;
    }

    /**
     * 递归添加渲染结果
     *
     * @param renderingResult 渲染的目标集合,参数不为null
     * @param answerElement   渲染的根节点
     */
    private void dfsAddRenderResult(List<AnswerElement> renderingResult, AnswerElement answerElement) {
        renderingResult.add(answerElement);
        for (AnswerElement child : answerElement.getAnswerElementGroup()) {
            dfsAddRenderResult(renderingResult, child);
        }
    }

    /**
     * 移除单词结构体
     *
     * @param removeArray    移除的结构数组
     * @param currentWordMap 当前结构体集合
     */
    private void removeWordStructure(Map<Long, List<WordDTO>> currentWordMap, EnglishStructure... removeArray) {
        for (EnglishStructure englishStructure : removeArray) {
            currentWordMap.remove(englishStructure.getWordStructureId());
        }
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private final TextView meaningCategoryHint, meaningCategoryAnswer;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            meaningCategoryHint = this.itemView.findViewById(R.id.fragment_word_credit_meaning_category_hint);
            meaningCategoryAnswer = this.itemView.findViewById(R.id.fragment_word_credit_textview_meaning_category_answer);
        }
    }

    public static class DrawerAnswerViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private final TextView meaningCategoryHint, meaningCategoryAnswer;

        public DrawerAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            meaningCategoryHint = this.itemView.findViewById(R.id.fragment_word_credit_meaning_category_hint);
            meaningCategoryAnswer = this.itemView.findViewById(R.id.fragment_word_credit_textview_meaning_category_answer);
        }
    }


    public enum RecyclerViewState {
        MAIN(0),
        DRAWER(2);

        private final int viewType;

        RecyclerViewState(int viewType) {
            this.viewType = viewType;
        }

        public int getViewType() {
            return viewType;
        }
    }

}
