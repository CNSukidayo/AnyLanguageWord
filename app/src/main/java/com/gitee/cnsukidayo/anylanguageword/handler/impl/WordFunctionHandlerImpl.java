package com.gitee.cnsukidayo.anylanguageword.handler.impl;

import com.gitee.cnsukidayo.anylanguageword.context.support.word.StructureWord;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditState;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;
import com.gitee.cnsukidayo.anylanguageword.enums.WordFunctionState;
import com.gitee.cnsukidayo.anylanguageword.context.support.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.handler.CategoryFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.WordFunctionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.cnsukidayo.wword.model.dto.DivideWordDTO;
import io.github.cnsukidayo.wword.model.dto.WordCategoryWordDTO;
import io.github.cnsukidayo.wword.model.dto.WordDTO;

/**
 * 明确一点,currentIndex是不能随意更改的,每逢currentIndex更改势必是由currentOrder的更改而更改的.<br>
 * 每个单词都是有棕色的,棕色是不可变的颜色,也就是说用户不可以取消单词的棕色标记.<br>
 * 变色龙的每一种状态都是可以进入的,不管当前单词列表中是否有该颜色对应的单词<br>
 */
public class WordFunctionHandlerImpl extends AbstractCategoryFunctionHandler implements WordFunctionHandler, CategoryFunctionHandler {
    /**
     * 单词的摘要信息
     */
    private List<DivideWordDTO> allDivideWordList;
    /**
     * 单词的标记信息,List中存放Set存放的是每一个单词的标记信息
     */
    private final List<Set<FlagColor>> wordsFlagList;

    /**
     * 当前背诵的区间
     */
    private int currentOrder = 0, currentIndex = 0;

    /**
     * 当前变色龙的颜色
     */
    private FlagColor currentChameleon = FlagColor.GREEN;
    private int nowSelectChameleonSize = -0x3f3f3f;

    /**
     * 默认的单词功能为空
     */
    private WordFunctionState wordFunctionState = WordFunctionState.NONE;

    // todo 当前的背诵风格功能
    private CreditState creditState = CreditState.ENGLISH_TRANSLATION_CHINESE;

    /**
     * 这是一个临时的集合,它指向allWordList,用于保存由按色打乱、区间重背功能被重置的allWordList引用
     */
    private List<DivideWordDTO> dummyWordList;

    /**
     * 现在正在背诵的区间
     */
    private int start = 0, end;

    public WordFunctionHandlerImpl(List<DivideWordDTO> initWordList,
                                   Map<Long, List<WordDTO>> dict) {
        this.allDivideWordList = new ArrayList<>(initWordList.size());
        // 和收藏夹共享本地查询缓存
        Map<Long, Map<Long, List<WordDTO>>> structureWord = new HashMap<>(dict.size());
        for (Map.Entry<Long, List<WordDTO>> entry : dict.entrySet()) {
            Map<Long, List<WordDTO>> convert = StructureWord.convert(entry.getValue());
            structureWord.put(entry.getKey(), convert);
        }
        super.addWordQueryCache(structureWord);
        this.allDivideWordList.addAll(initWordList);
        this.wordsFlagList = new ArrayList<>(initWordList.size());
        for (int i = 0; i < allDivideWordList.size(); i++) {
            wordsFlagList.add(new HashSet<>(List.of(FlagColor.GREEN, FlagColor.BROWN)));
        }
        this.end = allDivideWordList.size() - 1;
    }


    @Override
    public Map<Long, List<WordDTO>> getWordByOrder(int order) {
        if (nowSelectChameleonSize == 0) {
            currentOrder = 0;
            currentIndex = 0;
            return StaticFactory.getEmptyWord();
        }
        int i = 0;
        for (; i < wordsFlagList.size() && order >= 0; i++) {
            if (wordsFlagList.get(i).contains(currentChameleon)) {
                order--;
            }
        }
        this.currentIndex = i - 1;
        return queryCache.get(allDivideWordList.get(currentIndex).getWordId());
    }

    @Override
    public WordCategoryWordDTO getCurrentViewWord() {
        if (nowSelectChameleonSize == 0) {
            currentOrder = 0;
            currentIndex = 0;
            return null;
        }
        WordCategoryWordDTO wordCategoryWordDTO = new WordCategoryWordDTO();
        wordCategoryWordDTO.setWordId(allDivideWordList.get(currentIndex).getWordId());
        return wordCategoryWordDTO;
    }

    @Override
    public Map<Long, List<WordDTO>> jumpPreviousWord() {
        if (currentOrder <= 0) {
            currentOrder = nowSelectChameleonSize;
        }
        return getWordByOrder(--currentOrder);
    }

    @Override
    public Map<Long, List<WordDTO>> jumpNextWord() {
        if (currentOrder >= nowSelectChameleonSize - 1) {
            currentOrder = -1;
        }
        return getWordByOrder(++currentOrder);
    }

    @Override
    public void setCurrentOrder(int currentOrder) {
        this.currentOrder = currentOrder;
    }

    @Override
    public Map<Long, List<WordDTO>> jumpToWord(int jumpOrder) {
        this.currentOrder = jumpOrder;
        return getWordByOrder(currentOrder);
    }

    @Override
    public int getCurrentOrder() {
        return this.currentOrder;
    }

    @Override
    public int size() {
        if (nowSelectChameleonSize == -0x3f3f3f) {
            nowSelectChameleonSize = 0;
            for (int i = start; i <= end; i++) {
                if (this.wordsFlagList.get(i).contains(currentChameleon)) {
                    nowSelectChameleonSize++;
                }
            }
        }
        return nowSelectChameleonSize;
    }

    @Override
    public Set<FlagColor> getCurrentWordFlagColor() {
        return Collections.unmodifiableSet(wordsFlagList.get(currentIndex));
    }

    @Override
    public boolean addFlagToCurrentWord(FlagColor tobeAddFlag) {
        if (nowSelectChameleonSize == 0) {
            return false;
        }
        if (tobeAddFlag == currentChameleon) {
            nowSelectChameleonSize++;
        }
        return wordsFlagList.get(currentIndex).add(tobeAddFlag);
    }

    @Override
    public boolean removeFlagToCurrentWord(FlagColor tobeAddFlag) {
        if (nowSelectChameleonSize == 0) {
            return false;
        }
        if (tobeAddFlag == currentChameleon) {
            nowSelectChameleonSize--;
        }
        return wordsFlagList.get(currentIndex).remove(tobeAddFlag);
    }

    @Override
    public FlagColor getChameleon() {
        return this.currentChameleon;
    }

    @Override
    public void setChameleon(FlagColor chameleonColor) {
        this.nowSelectChameleonSize = -0x3f3f3f;
        this.currentChameleon = chameleonColor;
        this.currentOrder = 0;
        size();
    }

    @Override
    public void shuffle() {
        this.wordFunctionState = WordFunctionState.SHUFFLE;
        this.dummyWordList = new ArrayList<>(allDivideWordList.size());
        for (int i = 0; i < wordsFlagList.size(); i++) {
            if (wordsFlagList.get(i).contains(currentChameleon)) {
                dummyWordList.add(allDivideWordList.get(i));
            }
        }
        List<DivideWordDTO> temp = allDivideWordList;
        this.allDivideWordList = this.dummyWordList;
        this.dummyWordList = temp;
        Collections.shuffle(allDivideWordList);
    }

    @Override
    public void restoreWordList() {
        this.wordFunctionState = WordFunctionState.NONE;
        this.allDivideWordList = this.dummyWordList;
        this.start = 0;
        this.end = allDivideWordList.size() - 1;
        setChameleon(currentChameleon);
    }

    @Override
    public WordFunctionState getWordFunctionState() {
        return this.wordFunctionState;
    }

    @Override
    public void shuffleRange(int start, int end) {
        this.wordFunctionState = WordFunctionState.RANGE;
        this.start = start;
        this.end = end;
        this.dummyWordList = new ArrayList<>(end - start + 1);
        for (int i = start; i <= end; i++) {
            dummyWordList.add(allDivideWordList.get(i));
        }
        List<DivideWordDTO> temp = allDivideWordList;
        this.allDivideWordList = this.dummyWordList;
        this.dummyWordList = temp;
        Collections.shuffle(allDivideWordList);
        setChameleon(currentChameleon);
    }

    @Override
    public void setCurrentCreditState(CreditState creditState) {
        this.creditState = creditState;
    }

    @Override
    public CreditState getCurrentCreditState() {
        return creditState;
    }

}
