package com.gitee.cnsukidayo.anylanguageword.handler.impl;

import com.gitee.cnsukidayo.anylanguageword.context.KeyValueMap;
import com.gitee.cnsukidayo.anylanguageword.enums.MeaningCategory;
import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;
import com.gitee.cnsukidayo.anylanguageword.handler.WordMeaningConvertHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.cnsukidayo.wword.model.dto.WordDTO;

/**
 * @author cnsukidayo
 * @date 2023/1/6 21:18
 */
@Deprecated
public class WordMeaningConvertHandlerImpl implements WordMeaningConvertHandler {
    @Override
    public List<KeyValueMap<EnglishStructure, String>> convertWordMeaning(List<WordDTO> wordDTOS) {
        List<KeyValueMap<EnglishStructure, String>> result = new ArrayList<>(MeaningCategory.values().length);
        Map<Long, List<WordDTO>> structureWordMap = wordDTOS.stream()
                .collect(Collectors.groupingBy(WordDTO::getWordStructureId, Collectors.toList()));
        addToList(EnglishStructure.ADJ, result, structureWordMap);
        addToList(EnglishStructure.ADV, result, structureWordMap);
        addToList(EnglishStructure.V, result, structureWordMap);
        addToList(EnglishStructure.VI, result, structureWordMap);
        addToList(EnglishStructure.VT, result, structureWordMap);
        addToList(EnglishStructure.N, result, structureWordMap);
        addToList(EnglishStructure.CONJ, result, structureWordMap);
        addToList(EnglishStructure.PRON, result, structureWordMap);
        addToList(EnglishStructure.NUM, result, structureWordMap);
        addToList(EnglishStructure.ART, result, structureWordMap);
        addToList(EnglishStructure.PREP, result, structureWordMap);
        addToList(EnglishStructure.INT, result, structureWordMap);
        addToList(EnglishStructure.AUX, result, structureWordMap);
        return Collections.unmodifiableList(result);
    }

    private void addToList(EnglishStructure englishStructure,
                           List<KeyValueMap<EnglishStructure, String>> result,
                           Map<Long, List<WordDTO>> structureWordMap) {
        Optional.ofNullable(structureWordMap.get(englishStructure.getWordStructureId()))
                .ifPresent(wordDTO -> {
                    if (wordDTO.size() > 0)
                        result.add(new KeyValueMap<>(englishStructure,
                                Optional.ofNullable(wordDTO.get(0).getValue()).orElse("")));
                });
    }

}
