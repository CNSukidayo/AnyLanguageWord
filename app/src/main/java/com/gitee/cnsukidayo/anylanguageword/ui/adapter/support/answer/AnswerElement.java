package com.gitee.cnsukidayo.anylanguageword.ui.adapter.support.answer;

import java.util.List;

/**
 * answer-element答案的每个元素
 *
 * @author cnsukidayo
 * @date 2023/11/18 15:34
 */
public class AnswerElement {

    /**
     * 显示的结果key
     */
    private String key;

    /**
     * 显示的结果value
     */
    private String value;

    /**
     * 显示key的颜色
     */
    private int keyColor;

    /**
     * 显示value的颜色
     */
    private int valueColor;

    /**
     * key和value之间是否有断行,默认是没有断行的
     */
    private boolean hasBreak;

    /**
     * 组关系
     */
    private List<AnswerElement> answerElementGroup;

    private AnswerElement(String key, String value, int keyColor, int valueColor, boolean hasBreak, List<AnswerElement> answerElementGroup) {
        this.key = key;
        this.value = value;
        this.keyColor = keyColor;
        this.valueColor = valueColor;
        this.hasBreak = hasBreak;
        this.answerElementGroup = answerElementGroup;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getKeyColor() {
        return keyColor;
    }

    public void setKeyColor(int keyColor) {
        this.keyColor = keyColor;
    }

    public int getValueColor() {
        return valueColor;
    }

    public void setValueColor(int valueColor) {
        this.valueColor = valueColor;
    }

    public boolean isHasBreak() {
        return hasBreak;
    }

    public void setHasBreak(boolean hasBreak) {
        this.hasBreak = hasBreak;
    }

    public List<AnswerElement> getAnswerElementGroup() {
        return answerElementGroup;
    }

    public void setAnswerElementGroup(List<AnswerElement> answerElementGroup) {
        this.answerElementGroup = answerElementGroup;
    }

    public static class Builder {
        private String key;
        private String value;
        private int keyColor;
        private int valueColor;
        private boolean hasBreak;
        private List<AnswerElement> answerElementGroup;

        public AnswerElement build() {
            return new AnswerElement(key, value, keyColor, valueColor, hasBreak, answerElementGroup);
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder keyColor(int keyColor) {
            this.keyColor = keyColor;
            return this;
        }

        public Builder keyValue(int valueColor) {
            this.valueColor = valueColor;
            return this;
        }

        public Builder hasBreak(boolean hasBreak) {
            this.hasBreak = hasBreak;
            return this;
        }

        public Builder answerElementGroup(List<AnswerElement> answerElementGroup) {
            this.answerElementGroup = answerElementGroup;
            return this;
        }

    }

}
