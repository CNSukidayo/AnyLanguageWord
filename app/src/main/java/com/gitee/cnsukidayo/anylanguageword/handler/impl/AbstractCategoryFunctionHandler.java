package com.gitee.cnsukidayo.anylanguageword.handler.impl;

import android.text.TextUtils;

import com.gitee.cnsukidayo.anylanguageword.entity.WordCategory;
import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;
import com.gitee.cnsukidayo.anylanguageword.handler.CategoryFunctionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.cnsukidayo.wword.model.dto.WordDTO;

/**
 * @author cnsukidayo
 * @date 2023/2/9 16:26
 */
public abstract class AbstractCategoryFunctionHandler implements CategoryFunctionHandler {

    /**
     * 用于存储单词分类的列表
     */
    private final List<WordCategory> wordCategoryList = new ArrayList<>();

    @Override
    public void addNewCategory(WordCategory wordCategory) {
        wordCategoryList.add(wordCategory);
        wordCategory.setOrder(wordCategoryList.size() - 1);
    }

    @Override
    public void removeCategory(int position) {
        wordCategoryList.remove(position);
    }

    @Override
    public int categoryListSize() {
        return wordCategoryList.size();
    }

    @Override
    public WordCategory getWordCategoryByPosition(int position) {
        return wordCategoryList.get(position);
    }

    @Override
    public String calculationTitle(int position) {
        return calculation(position, EnglishStructure.WORD_ORIGIN.getWordStructureId());
    }

    @Override
    public String calculationDescribe(int position) {
        return calculation(position, EnglishStructure.UK_PHONETIC.getWordStructureId());
    }

    @Override
    public Map<Long, List<WordDTO>> getCurrentStructureWordMap() {
        return getCurrentWord().stream()
                .collect(Collectors.groupingBy(WordDTO::getWordStructureId, Collectors.toList()));
    }

    @Override
    public void moveCategory(int fromPosition, int toPosition) {
        Collections.swap(wordCategoryList, fromPosition, toPosition);
    }

    @Override
    public int currentCategorySize(int categoryID) {
        return getWordCategoryByPosition(categoryID).getWords().size();
    }

    @Override
    public boolean addWordToCategory(int categoryID, Map<Long, List<WordDTO>> addWord) {
        Set<Long> uniqueSet = getWordCategoryByPosition(categoryID).getUniqueSet();
        // 如果当前单词还没有添加到当前收藏夹中则允许添加
        Long currentWordId = addWord.values().iterator().next().get(0).getWordId();
        if (!uniqueSet.contains(currentWordId)) {
            getWordCategoryByPosition(categoryID).getWords().add(addWord);
            uniqueSet.add(currentWordId);
            return true;
        }
        return false;
    }

    @Override
    public void removeWordFromCategory(int categoryID, int position) {
        // 得到当前要删除的单词,并且将其id从uniqueSet中移除
        Map<Long, List<WordDTO>> wordStructureMap = getWordFromCategory(categoryID, position);
        Long currentWordId = wordStructureMap.values().iterator().next().get(0).getWordId();
        getWordCategoryByPosition(categoryID).getUniqueSet().remove(currentWordId);
        getWordCategoryByPosition(categoryID).getWords().remove(position);
    }

    @Override
    public Map<Long, List<WordDTO>> getWordFromCategory(int categoryID, int position) {
        return getWordCategoryByPosition(categoryID).getWords().get(position);
    }

    @Override
    public void moveCategoryWord(int categoryID, int sourcePosition, int targetPosition) {
        Collections.swap(getWordCategoryByPosition(categoryID).getWords(), sourcePosition, targetPosition);
    }

    /**
     * 计算标题和计算描述信息的方法复用
     *
     * @param position        单词分类对象在列表中对应的位置
     * @param wordStructureId 要对那个结构字段进行计算(传入结构字段的id)
     * @return 返回计算出的描述信息, 返回值不为null
     * @see AbstractCategoryFunctionHandler#calculationDescribe(int)
     * @see AbstractCategoryFunctionHandler#calculationTitle(int)
     */
    private String calculation(int position, Long wordStructureId) {
        WordCategory wordCategory = getWordCategoryByPosition(position);
        if (wordCategory.isDefaultDescribeRule() && TextUtils.isEmpty(wordCategory.getDescribe())) {
            StringBuilder defaultNameRule = new StringBuilder();
            for (int i = 0; i < 3 && i < wordCategory.getWords().size(); i++) {
                Optional.ofNullable(wordCategory.getWords()
                                .get(i)
                                .get(wordStructureId))
                        .ifPresent(wordDTOS -> defaultNameRule
                                .append(wordDTOS.size() > 0 && wordDTOS.get(0).getValue() != null ? wordDTOS.get(0).getValue() : "")
                                .append("、"));
            }
            if (defaultNameRule.length() > 1) {
                return defaultNameRule.substring(0, defaultNameRule.length() - 1);
            } else {
                return defaultNameRule.toString();
            }
        }
        return wordCategory.getDescribe();
    }

}
