package com.gitee.cnsukidayo.traditionalenglish.handler;

import com.gitee.cnsukidayo.traditionalenglish.entity.Word;
import com.gitee.cnsukidayo.traditionalenglish.enums.MeaningCategory;
import com.gitee.cnsukidayo.traditionalenglish.utils.Strings;

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
        if (Strings.notEmpty(word.getADJ()))
            result.add(new KeyValueMap<>(MeaningCategory.ADJECTIVE, word.getADJ()));
        if (Strings.notEmpty(word.getADV()))
            result.add(new KeyValueMap<>(MeaningCategory.ADVERB, word.getADV()));
        if (Strings.notEmpty(word.getV()))
            result.add(new KeyValueMap<>(MeaningCategory.VERB, word.getV()));
        if (Strings.notEmpty(word.getVI()))
            result.add(new KeyValueMap<>(MeaningCategory.INTRANSITIVE_VERB, word.getVI()));
        if (Strings.notEmpty(word.getVT()))
            result.add(new KeyValueMap<>(MeaningCategory.TRANSITIVE_VERB, word.getVT()));
        if (Strings.notEmpty(word.getN()))
            result.add(new KeyValueMap<>(MeaningCategory.NOUN, word.getN()));
        if (Strings.notEmpty(word.getCONJ()))
            result.add(new KeyValueMap<>(MeaningCategory.CONJUNCTION, word.getCONJ()));
        if (Strings.notEmpty(word.getPRON()))
            result.add(new KeyValueMap<>(MeaningCategory.PRONOUN, word.getPRON()));
        if (Strings.notEmpty(word.getNUM()))
            result.add(new KeyValueMap<>(MeaningCategory.NUMBER, word.getNUM()));
        if (Strings.notEmpty(word.getART()))
            result.add(new KeyValueMap<>(MeaningCategory.ARTICLE, word.getART()));
        if (Strings.notEmpty(word.getPREP()))
            result.add(new KeyValueMap<>(MeaningCategory.PREPOSITION, word.getPREP()));
        if (Strings.notEmpty(word.getINT()))
            result.add(new KeyValueMap<>(MeaningCategory.INT, word.getINT()));
        if (Strings.notEmpty(word.getAUX()))
            result.add(new KeyValueMap<>(MeaningCategory.AUXILIARY, word.getAUX()));
        return Collections.unmodifiableList(result);
    }

    public static class KeyValueMap<K, V> {
        private K key;
        private V value;

        public KeyValueMap(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }

}
