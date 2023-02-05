package com.gitee.cnsukidayo.anylanguageword.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 单词的分类实体映射对象
 *
 * @author cnsukidayo
 * @date 2023/1/8 19:32
 */
public class WordCategory {

    private String title;
    private String describe;
    private boolean defaultTitleRule;
    private boolean defaultDescribeRule;
    private int order;

    // todo 这里是模拟测试,暂时使用一个List存储当前分类下的所有单词,集合中单词的顺序就是单词的order
    private List<Word> words = new ArrayList<>();

    public WordCategory() {
    }

    public WordCategory(String title, String describe, boolean defaultTitleRule, boolean defaultDescribeRule) {
        this.title = title;
        this.describe = describe;
        this.defaultTitleRule = defaultTitleRule;
        this.defaultDescribeRule = defaultDescribeRule;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public boolean isDefaultTitleRule() {
        return defaultTitleRule;
    }

    public void setDefaultTitleRule(boolean defaultTitleRule) {
        this.defaultTitleRule = defaultTitleRule;
    }

    public boolean isDefaultDescribeRule() {
        return defaultDescribeRule;
    }

    public void setDefaultDescribeRule(boolean defaultDescribeRule) {
        this.defaultDescribeRule = defaultDescribeRule;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }
}
