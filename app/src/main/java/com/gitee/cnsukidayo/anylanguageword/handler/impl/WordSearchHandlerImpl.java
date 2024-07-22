package com.gitee.cnsukidayo.anylanguageword.handler.impl;

import com.gitee.cnsukidayo.anylanguageword.entity.local.WordDTOLocal;
import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;
import com.gitee.cnsukidayo.anylanguageword.handler.WordSearchHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author cnsukidayo
 * @date 2024/7/21 10:49
 */
public class WordSearchHandlerImpl implements WordSearchHandler {

    private final List<WordDTOLocal> dict;

    public WordSearchHandlerImpl(Map<Long, WordDTOLocal> dict) {
        this.dict = new ArrayList<>(dict.values());
    }

    @Override
    public List<WordDTOLocal> searchWord(String key) {
        return dict.stream()
                .filter(wordDTOLocal -> Optional.ofNullable(wordDTOLocal.getValue().get(EnglishStructure.WORD_ORIGIN))
                        .orElse("")
                        .contains(key))
                .collect(Collectors.toList());
    }
}
