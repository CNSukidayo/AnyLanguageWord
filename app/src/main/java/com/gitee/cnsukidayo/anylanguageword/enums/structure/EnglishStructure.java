package com.gitee.cnsukidayo.anylanguageword.enums.structure;

import android.view.View;

import com.gitee.cnsukidayo.anylanguageword.R;

/**
 * 英语单词结构
 *
 * @author cnsukidayo
 * @date 2023/11/4 12:58
 */
public enum EnglishStructure implements BaseStructure {

    DEFAULT(0L, R.string.default_value),
    /**
     * 形容词
     */
    ADJ(1L, R.string.adjective),
    /**
     * 副词
     */
    ADV(2L, R.string.adverb),
    /**
     * 动词
     */
    V(3L, R.string.verb),
    /**
     * 不及物动词
     */
    VI(4L, R.string.intransitive_verb),
    /**
     * 及物动词
     */
    VT(5L, R.string.transitive_verb),
    /**
     * 名词
     */
    N(6L, R.string.noun),
    /**
     * 连词
     */
    CONJ(7L, R.string.conjunction),
    /**
     * 代词
     */
    PRON(8L, R.string.pronoun),
    /**
     * 数次
     */
    NUM(9L, R.string.number),
    /**
     * 冠词
     */
    ART(10L, R.string.article),
    /**
     * 介词
     */
    PREP(11L, R.string.preposition),
    /**
     * 感叹词
     */
    INT(12L, R.string.int_word),
    /**
     * 助动词
     */
    AUX(13L, R.string.auxiliary),
    /**
     * 单词原文
     */
    WORD_ORIGIN(27L, R.string.word_origin),
    /**
     * 英式音标
     */
    UK_PHONETIC(17L, R.string.uk_phonetic),
    /**
     * 介词短语
     */
    PHRASE(15L, R.string.phrase),
    /**
     * 介词短语翻译
     */
    PHRASE_TRANSLATION(24L, R.string.phrase),
    ;

    private final Long wordStructureId;

    private final int hint;

    private int visibility;

    EnglishStructure(Long wordStructureId, int hint) {
        this(wordStructureId, hint, View.GONE);
    }

    EnglishStructure(Long wordStructureId, int hint, int visibility) {
        this.wordStructureId = wordStructureId;
        this.hint = hint;
        this.visibility = visibility;
    }


    @Override
    public Long getWordStructureId() {
        return wordStructureId;
    }

    @Override
    public int getHint() {
        return hint;
    }

    @Override
    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @Override
    public int getVisibility() {
        return this.visibility;
    }
}
