package com.gitee.cnsukidayo.anylanguageword.handler;

import com.gitee.cnsukidayo.anylanguageword.context.KeyValueMap;
import com.gitee.cnsukidayo.anylanguageword.entity.Word;
import com.gitee.cnsukidayo.anylanguageword.enums.MeaningCategory;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2023/1/6 21:13
 */
public interface WordMeaningConvertHandler {
    /**
     * 获得一个单词其对应的所有MeaningCategory,如果单词的某个意思不为null并且不为空白串,则会被添加到返回值中.<br>
     * {@link com.gitee.cnsukidayo.anylanguageword.utils.Strings#notEmpty(String)}
     *
     * @param word 待转换单词的引用
     * @return 返回一个Map, 该列表存储当前单词有哪些意思,返回的集合应该是不可变集合
     */
    List<KeyValueMap<MeaningCategory, String>> convertWordMeaning(Word word);

}
