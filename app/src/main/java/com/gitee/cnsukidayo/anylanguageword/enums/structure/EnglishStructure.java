package com.gitee.cnsukidayo.anylanguageword.enums.structure;

import com.gitee.cnsukidayo.anylanguageword.R;

/**
 * 英语单词结构
 *
 * @author cnsukidayo
 * @date 2023/11/4 12:58
 */
public enum EnglishStructure implements BaseStructure {

    DEFAULT(0L, -1, Integer.MAX_VALUE),
    /**
     * 单词原文
     */
    WORD_ORIGIN(27L, -1, -1),
    /**
     * 美式音标
     */
    US_PHONETIC(16L, -1, -1),
    /**
     * 英式音标
     */
    UK_PHONETIC(17L, -1, -1),
    /**
     * 形容词
     */
    ADJ(1L, R.string.adjective, 1),
    /**
     * 副词
     */
    ADV(2L, R.string.adverb, 2),
    /**
     * 动词
     */
    V(3L, R.string.verb, 3),
    /**
     * 不及物动词
     */
    VI(4L, R.string.intransitive_verb, 4),
    /**
     * 及物动词
     */
    VT(5L, R.string.transitive_verb, 5),
    /**
     * 名词
     */
    N(6L, R.string.noun, 6),
    /**
     * 连词
     */
    CONJ(7L, R.string.conjunction, 7),
    /**
     * 代词
     */
    PRON(8L, R.string.pronoun, 8),
    /**
     * 数次
     */
    NUM(9L, R.string.number, 9),
    /**
     * 冠词
     */
    ART(10L, R.string.article, 10),
    /**
     * 介词
     */
    PREP(11L, R.string.preposition, 11),
    /**
     * 感叹词
     */
    INT(12L, R.string.int_word, 12),
    /**
     * 助动词
     */
    AUX(13L, R.string.auxiliary, 13),
    /**
     * 扩展
     */
    EXPAND(14L, -1, 14),
    /**
     * 介词短语
     */
    PHRASE(15L, R.string.phrase, 15),
    /**
     * 介词短语英文翻译
     */
    PHRASE_TRANSLATION(24L, R.string.phrase_translation, 16),
    /**
     * 单词记忆的方法
     */
    REMEMBER_METHOD(34L, R.string.remember_method, 17),
    /**
     * 例句
     */
    SENTENCE(22L, R.string.example_sentence, 18),
    /**
     * 例句翻译英文翻译
     */
    SENTENCE_TRANSLATION(23L, R.string.sentence_translation, 19),
    /**
     * 例题:
     */
    QUESTION(18L, R.string.question, 20),
    /**
     * 问题选项的索引
     */
    CHOICE_INDEX(21L, R.string.choice_index, 21),
    /**
     * 问题选项的内容
     */
    CHOICE_VALUE(26L, R.string.choice_value, 22),
    /**
     * 正确的答案索引
     */
    RIGHT_INDEX(20L, R.string.right_index, 23),
    /**
     * 问题的答案解释
     */
    QUESTION_EXPLAIN(19L, R.string.question_explain, 24),
    /**
     * 单词的英文解释
     */
    DESCRIBE_ENGLISH(25L, R.string.describe_english, 25),

    /**
     * 真题例句(英文)
     */
    REAL_EXAM_SENTENCE(29L, R.string.real_exam_sentence, 26),
    /**
     * 真题出自哪一套
     */
    SOURCE_PAPER(30L, R.string.source_paper, 27),
    /**
     * 真题等级(四级还是六级)
     */
    SOURCE_LEVEL(31L, R.string.source_level, 28),
    /**
     * 真题的年份
     */
    SOURCE_YEAR(32L, R.string.source_year, 29),
    /**
     * 真题的类型
     */
    SOURCE_TYPE(33L, R.string.source_type, 30),

    /**
     * 联想词
     */
    SYNO_WORD(38L, R.string.syno_word, 31),
    /**
     * 联想词的词性
     */
    SYNO_POS(35L, R.string.syno_pos, 32),
    /**
     * 联想词的翻译
     */
    SYNO_TRANSLATION(36L, R.string.syno_translation, 33),
    /**
     * 联想词对应的详细信息的id
     */
    SYNO_WORD_ID(37L, R.string.syno_word_id, 34),

    /**
     * 同根词的单词
     */
    REL_WORD(40L, R.string.rel_word, 35),
    /**
     * 同根词的词性
     */
    REL_POS(39L, R.string.rel_pos, 36),
    /**
     * 同根词的翻译
     */
    REL_WORD_TRANSLATION(41L, R.string.rel_word_translation, 37),
    /**
     * 同根词对应的单词id
     */
    REL_WORD_ID(42L, R.string.rel_word_id, 38),

    /**
     * 单词的图片
     */
    PICTURE(28L, R.string.picture, 39);

    /**
     * 单词对应的结构id
     */
    private final Long wordStructureId;
    /**
     * 单词的提示
     */
    private final int hint;
    /**
     * 单词项在界面中展示的顺序
     */
    private final int order;

    EnglishStructure(Long wordStructureId, int hint, int order) {
        this.wordStructureId = wordStructureId;
        this.hint = hint;
        this.order = order;
    }

    @Override
    public Long getWordStructureId() {
        return wordStructureId;
    }

    @Override
    public int getHint() {
        return hint;
    }

    public int getOrder() {
        return order;
    }
}
