package com.gitee.cnsukidayo.anylanguageword.handler.impl;

import android.text.TextUtils;

import com.gitee.cnsukidayo.anylanguageword.entity.Word;
import com.gitee.cnsukidayo.anylanguageword.entity.WordCategory;
import com.gitee.cnsukidayo.anylanguageword.handler.CategoryFunctionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cnsukidayo
 * @date 2023/2/9 16:26
 */
public abstract class AbstractCategoryFunctionHandler implements CategoryFunctionHandler {

    // 用于存储单词分类的列表
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
        WordCategory wordCategory = getWordCategoryByPosition(position);
        if (wordCategory.isDefaultTitleRule() && TextUtils.isEmpty(wordCategory.getTitle())) {
            StringBuilder defaultNameRule = new StringBuilder();
            for (int i = 0; i < 3 && i < wordCategory.getWords().size(); i++) {
                defaultNameRule.append(wordCategory.getWords().get(i).getWordOrigin()).append("、");
            }
            if (defaultNameRule.length() > 1) {
                return defaultNameRule.substring(0, defaultNameRule.length() - 1);
            } else {
                return defaultNameRule.toString();
            }
        }
        return wordCategory.getTitle();
    }

    @Override
    public String calculationDescribe(int position) {
        WordCategory wordCategory = getWordCategoryByPosition(position);
        if (wordCategory.isDefaultDescribeRule() && TextUtils.isEmpty(wordCategory.getDescribe())) {
            StringBuilder defaultNameRule = new StringBuilder();
            for (int i = 0; i < 3 && i < wordCategory.getWords().size(); i++) {
                defaultNameRule.append(wordCategory.getWords().get(i).getWordOrigin()).append("、");
            }
            if (defaultNameRule.length() > 1) {
                return defaultNameRule.substring(0, defaultNameRule.length() - 1);
            } else {
                return defaultNameRule.toString();
            }
        }
        return wordCategory.getDescribe();
    }

    @Override
    public void categoryRemove(int fromPosition, int toPosition) {
        Collections.swap(wordCategoryList, fromPosition, toPosition);
    }

    @Override
    public int currentCategorySize(int categoryID) {
        return getWordCategoryByPosition(categoryID).getWords().size();
    }

    @Override
    public void addWordToCategory(int categoryID, Word addWord) {
        getWordCategoryByPosition(categoryID).getWords().add(addWord);
    }

    @Override
    public void removeWordFromCategory(int categoryID, int position) {
        getWordCategoryByPosition(categoryID).getWords().remove(position);
    }

    @Override
    public Word getWordFromCategory(int categoryID, int position) {
        return getWordCategoryByPosition(categoryID).getWords().get(position);
    }

    @Override
    public void moveCategoryWord(int categoryID, int sourcePosition, int targetPosition) {
        Collections.swap(getWordCategoryByPosition(categoryID).getWords(), sourcePosition, targetPosition);
    }
}
