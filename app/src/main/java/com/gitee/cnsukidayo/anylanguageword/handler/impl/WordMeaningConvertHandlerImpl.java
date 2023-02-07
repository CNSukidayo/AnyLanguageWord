package com.gitee.cnsukidayo.anylanguageword.handler.impl;

import android.text.TextUtils;

import com.gitee.cnsukidayo.anylanguageword.context.KeyValueMap;
import com.gitee.cnsukidayo.anylanguageword.entity.Word;
import com.gitee.cnsukidayo.anylanguageword.enums.MeaningCategory;
import com.gitee.cnsukidayo.anylanguageword.handler.WordMeaningConvertHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cnsukidayo
 * @date 2023/1/6 21:18
 */
public class WordMeaningConvertHandlerImpl implements WordMeaningConvertHandler {
    @Override
    public List<KeyValueMap<MeaningCategory, String>> convertWordMeaning(Word word) {
        List<KeyValueMap<MeaningCategory, String>> result = new ArrayList<>(MeaningCategory.values().length);
        if (!TextUtils.isEmpty(word.getADJ()))
            result.add(new KeyValueMap<>(MeaningCategory.ADJECTIVE, word.getADJ()));
        if (!TextUtils.isEmpty(word.getADV()))
            result.add(new KeyValueMap<>(MeaningCategory.ADVERB, word.getADV()));
        if (!TextUtils.isEmpty(word.getV()))
            result.add(new KeyValueMap<>(MeaningCategory.VERB, word.getV()));
        if (!TextUtils.isEmpty(word.getVI()))
            result.add(new KeyValueMap<>(MeaningCategory.INTRANSITIVE_VERB, word.getVI()));
        if (!TextUtils.isEmpty(word.getVT()))
            result.add(new KeyValueMap<>(MeaningCategory.TRANSITIVE_VERB, word.getVT()));
        if (!TextUtils.isEmpty(word.getN()))
            result.add(new KeyValueMap<>(MeaningCategory.NOUN, word.getN()));
        if (!TextUtils.isEmpty(word.getCONJ()))
            result.add(new KeyValueMap<>(MeaningCategory.CONJUNCTION, word.getCONJ()));
        if (!TextUtils.isEmpty(word.getPRON()))
            result.add(new KeyValueMap<>(MeaningCategory.PRONOUN, word.getPRON()));
        if (!TextUtils.isEmpty(word.getNUM()))
            result.add(new KeyValueMap<>(MeaningCategory.NUMBER, word.getNUM()));
        if (!TextUtils.isEmpty(word.getART()))
            result.add(new KeyValueMap<>(MeaningCategory.ARTICLE, word.getART()));
        if (!TextUtils.isEmpty(word.getPREP()))
            result.add(new KeyValueMap<>(MeaningCategory.PREPOSITION, word.getPREP()));
        if (!TextUtils.isEmpty(word.getINT()))
            result.add(new KeyValueMap<>(MeaningCategory.INT, word.getINT()));
        if (!TextUtils.isEmpty(word.getAUX()))
            result.add(new KeyValueMap<>(MeaningCategory.AUXILIARY, word.getAUX()));
        return Collections.unmodifiableList(result);
    }

}
