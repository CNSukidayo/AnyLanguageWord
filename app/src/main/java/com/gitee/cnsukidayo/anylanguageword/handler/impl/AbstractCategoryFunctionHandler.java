package com.gitee.cnsukidayo.anylanguageword.handler.impl;

import android.text.TextUtils;

import com.gitee.cnsukidayo.anylanguageword.context.word.StructureWord;
import com.gitee.cnsukidayo.anylanguageword.enums.structure.EnglishStructure;
import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;
import com.gitee.cnsukidayo.anylanguageword.handler.CategoryFunctionHandler;
import com.gitee.cnsukidayo.anylanguageword.utils.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.cnsukidayo.wword.common.request.factory.CoreServiceRequestFactory;
import io.github.cnsukidayo.wword.model.dto.WordCategoryDTO;
import io.github.cnsukidayo.wword.model.dto.WordCategoryWordDTO;
import io.github.cnsukidayo.wword.model.dto.WordDTO;
import io.github.cnsukidayo.wword.model.params.WordCategoryParam;
import io.github.cnsukidayo.wword.model.params.WordCategoryWordParam;
import io.github.cnsukidayo.wword.model.vo.WordCategoryDetailVO;

/**
 * @author cnsukidayo
 * @date 2023/2/9 16:26
 */
public abstract class AbstractCategoryFunctionHandler implements CategoryFunctionHandler {

    /**
     * 用于存储单词分类的列表
     */
    private final List<WordCategoryDetailVO> wordCategoryDetailVOList = new ArrayList<>(15);
    /**
     * 当前用户收藏的所有单词列表;第一个Map的Key单词的Id
     * 第二个Map的key是单词的结构id,value就是该结构下面的所有信息
     */
    private final Map<Long, Map<Long, List<WordDTO>>> allStatWordList = new HashMap<>(30);

    @Override
    public void batchAddCategory(List<WordCategoryDetailVO> wordCategoryDetailVOList) {
        this.wordCategoryDetailVOList.addAll(wordCategoryDetailVOList);
        // 本次添加的所有单词Id列表
        List<Long> wordIdList = wordCategoryDetailVOList.stream()
                .flatMap(wordCategoryDetailVO -> wordCategoryDetailVO.getWordCategoryWordList().stream())
                .map(WordCategoryWordDTO::getWordId)
                .collect(Collectors.toList());

        // 批量查询出所有的单词信息
        StaticFactory.getExecutorService().submit(() -> {
            CoreServiceRequestFactory.getInstance()
                    .wordRequest()
                    .batchSelectWordById(wordIdList)
                    .success(data -> {
                        List<WordDTO> wordDTOList = data.getData();
                        // 根据单词的Id进行分类
                        Map<Long, List<WordDTO>> wordDTOListGroupById = wordDTOList.stream()
                                .collect(Collectors.groupingBy(WordDTO::getWordId));
                        Map<Long, Map<Long, List<WordDTO>>> structureWordMap = new HashMap<>(wordDTOListGroupById.size());
                        for (Map.Entry<Long, List<WordDTO>> wordEntry : wordDTOListGroupById.entrySet()) {
                            // 转为结构单词并将所有的单词添加到查询缓存中!
                            Map<Long, List<WordDTO>> structureWord = StructureWord.convert(wordEntry.getValue());
                            structureWordMap.put(wordEntry.getKey(), structureWord);
                        }
                        // 批量添加
                        allStatWordList.putAll(structureWordMap);
                    })
                    .execute();
        });
    }

    @Override
    public void addNewCategory(WordCategoryDTO wordCategoryDTO) {
        WordCategoryDetailVO wordCategoryDetailVO = new WordCategoryDetailVO();
        wordCategoryDetailVO.setId(wordCategoryDTO.getId());
        wordCategoryDetailVO.setTitle(wordCategoryDTO.getTitle());
        wordCategoryDetailVO.setDescribeInfo(wordCategoryDTO.getDescribeInfo());
        wordCategoryDetailVO.setCategoryOrder(wordCategoryDTO.getCategoryOrder());
        wordCategoryDetailVO.setWordCategoryWordList(new ArrayList<>());
        wordCategoryDetailVOList.add(wordCategoryDetailVO);
    }

    @Override
    public void updateWordCategoryDto(int position, WordCategoryDTO wordCategoryDTO) {
        WordCategoryParam wordCategoryParam = new WordCategoryParam();
        WordCategoryDetailVO wordCategoryDetailVO = getWordCategoryByPosition(position);
        wordCategoryDTO.setCategoryOrder(wordCategoryDetailVO.getCategoryOrder());
        BeanUtils.copyPropertiesWithNonNullSourceFields(wordCategoryDTO, wordCategoryParam);
        List<WordCategoryParam> updateWordCategoryParamList = new ArrayList<>();
        updateWordCategoryParamList.add(wordCategoryParam);
        // 更新当前的收藏夹列表
        StaticFactory.getExecutorService().submit(() -> {
            CoreServiceRequestFactory.getInstance()
                    .wordCategoryRequest()
                    .update(updateWordCategoryParamList)
                    .execute();
        });
    }

    @Override
    public void updateWordCategoryList() {
        // 移动结束后需要根据List的顺序来设置order值
        int order = 0;
        for (WordCategoryDetailVO wordCategoryDetailVO : wordCategoryDetailVOList) {
            wordCategoryDetailVO.setCategoryOrder(order++);
        }
        // 更新当前的收藏夹列表
        StaticFactory.getExecutorService().submit(() -> {
            List<WordCategoryParam> wordCategoryParamList = BeanUtils.copyArrayProperties(WordCategoryParam.class, wordCategoryDetailVOList);
            CoreServiceRequestFactory.getInstance()
                    .wordCategoryRequest()
                    .update(wordCategoryParamList)
                    .execute();
        });
    }

    @Override
    public void removeCategory(int position) {
        WordCategoryDetailVO wordCategoryDetailVO = getWordCategoryByPosition(position);
        wordCategoryDetailVOList.remove(position);
        StaticFactory.getExecutorService().submit(() -> {
            CoreServiceRequestFactory.getInstance()
                    .wordCategoryRequest()
                    .remove(wordCategoryDetailVO.getId())
                    .execute();
        });
        // 重排序当前的收藏夹
        wordCategoryDetailVOList.sort((o1, o2) -> o1.getCategoryOrder() - o2.getCategoryOrder());
        wordCategoryDetailVOList.forEach(new Consumer<>() {
            int order = 0;

            @Override
            public void accept(WordCategoryDetailVO wordCategoryDetailVO) {
                wordCategoryDetailVO.setCategoryOrder(order++);
            }
        });
    }

    @Override
    public int categoryListSize() {
        return wordCategoryDetailVOList.size();
    }

    @Override
    public WordCategoryDetailVO getWordCategoryByPosition(int position) {
        return wordCategoryDetailVOList.get(position);
    }

    @Override
    public String calculationTitle(int position) {
        return TextUtils.isEmpty(wordCategoryDetailVOList.get(position).getTitle()) ?
                calculation(position, EnglishStructure.WORD_ORIGIN.getWordStructureId()) :
                wordCategoryDetailVOList.get(position).getTitle();
    }

    @Override
    public String calculationDescribe(int position) {
        return TextUtils.isEmpty(wordCategoryDetailVOList.get(position).getDescribeInfo()) ?
                calculation(position, EnglishStructure.UK_PHONETIC.getWordStructureId()) :
                wordCategoryDetailVOList.get(position).getDescribeInfo();
    }

    @Override
    public Map<Long, List<WordDTO>> getCurrentStructureWordMap() {
        WordCategoryWordDTO wordCategoryWordDTO = getCurrentViewWord();
        if (wordCategoryWordDTO == null) {
            return null;
        }
        return allStatWordList.get(wordCategoryWordDTO.getWordId());
    }

    @Override
    public void moveCategory(int fromPosition, int toPosition) {
        Collections.swap(wordCategoryDetailVOList, fromPosition, toPosition);
    }

    @Override
    public int currentCategorySize(int categoryPosition) {
        return getWordCategoryByPosition(categoryPosition).getWordCategoryWordList().size();
    }

    @Override
    public boolean addWordToCategory(int categoryPosition, WordCategoryWordDTO addWordCategoryWordDTO) {
        // 得到当前收藏夹
        WordCategoryDetailVO wordCategoryDetailVO = getWordCategoryByPosition(categoryPosition);
        List<WordCategoryWordDTO> wordCategoryWordList = wordCategoryDetailVO.getWordCategoryWordList();
        // 当前收藏夹不能已经存在当前单词
        for (WordCategoryWordDTO wordCategoryWordDTO : wordCategoryWordList) {
            if (wordCategoryWordDTO.getWordId().equals(addWordCategoryWordDTO.getWordId())) {
                return false;
            }
        }
        addWordCategoryWordDTO.setWordOrder(wordCategoryWordList.size());
        addWordCategoryWordDTO.setWordCategoryId(wordCategoryDetailVO.getId());
        wordCategoryWordList.add(addWordCategoryWordDTO);
        StaticFactory.getExecutorService().submit(() -> {
            CoreServiceRequestFactory.getInstance()
                    .wordCategoryRequest()
                    .addWord(wordCategoryDetailVO.getId(), addWordCategoryWordDTO.getWordId())
                    .execute();
        });
        return true;
    }

    @Override
    public void removeWordFromCategory(int categoryID, int position) {
        WordCategoryDetailVO wordCategoryDetailVO = getWordCategoryByPosition(categoryID);
        WordCategoryWordDTO wordCategoryWordDTO = wordCategoryDetailVO.getWordCategoryWordList().remove(position);
        // 重排序
        wordCategoryDetailVO.getWordCategoryWordList().sort((o1, o2) -> o1.getWordOrder() - o2.getWordOrder());
        wordCategoryDetailVO.getWordCategoryWordList().forEach(new Consumer<>() {

            int order = 0;

            @Override
            public void accept(WordCategoryWordDTO wordCategoryWordDTO) {
                wordCategoryWordDTO.setWordOrder(order++);
            }
        });
        StaticFactory.getExecutorService()
                .submit(() -> CoreServiceRequestFactory.getInstance()
                        .wordCategoryRequest()
                        .removeWord(wordCategoryWordDTO.getWordCategoryId(), wordCategoryWordDTO.getWordId())
                        .execute());
    }

    @Override
    public Map<Long, List<WordDTO>> getWordFromCategory(int categoryID, int position) {
        return allStatWordList.get(getWordCategoryByPosition(categoryID).getWordCategoryWordList()
                .get(position)
                .getWordId());
    }

    @Override
    public void addWordQueryCache(Map<Long, Map<Long, List<WordDTO>>> structureWord) {
        allStatWordList.putAll(structureWord);
    }

    @Override
    public void updateWordCategoryWordOrder(int categoryPosition) {
        List<WordCategoryWordDTO> wordCategoryWordList = wordCategoryDetailVOList.get(categoryPosition).getWordCategoryWordList();
        // 移动结束后需要根据List的顺序来设置order值
        List<WordCategoryWordParam> wordCategoryWordParamList = new ArrayList<>(wordCategoryWordList.size());
        int order = 0;
        for (WordCategoryWordDTO wordCategoryWordDTO : wordCategoryWordList) {
            WordCategoryWordParam wordCategoryWordParam = new WordCategoryWordParam();
            BeanUtils.copyPropertiesWithNonNullSourceFields(wordCategoryWordDTO, wordCategoryWordParam);
            wordCategoryWordParam.setWordOrder(order++);
            wordCategoryWordParamList.add(wordCategoryWordParam);
        }
        StaticFactory.getExecutorService()
                .submit(() -> CoreServiceRequestFactory.getInstance()
                        .wordCategoryRequest()
                        .updateWordOrder(wordCategoryWordParamList)
                        .execute());
    }

    @Override
    public void moveCategoryWord(int categoryPosition, int fromPosition, int toPosition) {
        Collections.swap(wordCategoryDetailVOList.get(categoryPosition).getWordCategoryWordList(), fromPosition, toPosition);
    }

    /**
     * 计算标题和计算描述信息的方法复用
     *
     * @param position        单词分类对象在列表中对应的位置
     * @param wordStructureId 要对那个结构字段进行计算(传入结构字段的id)
     * @return 返回计算出的描述信息, 返回值不为null
     * @see AbstractCategoryFunctionHandler#calculationDescribe(int)
     * @see AbstractCategoryFunctionHandler#calculationTitle(int)
     */
    private String calculation(int position, Long wordStructureId) {
        WordCategoryDetailVO wordCategoryDetailVO = getWordCategoryByPosition(position);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 3 && i < wordCategoryDetailVO.getWordCategoryWordList().size(); i++) {
            Optional.ofNullable(allStatWordList.get(wordCategoryDetailVO.getWordCategoryWordList()
                            .get(i)
                            .getWordId()))
                    .map((Function<Map<Long, List<WordDTO>>, List<WordDTO>>) wordStructureMap -> wordStructureMap.get(wordStructureId))
                    .ifPresent(new Consumer<List<WordDTO>>() {
                        @Override
                        public void accept(List<WordDTO> wordDTOS) {
                            result
                                    .append(wordDTOS.size() > 0 && wordDTOS.get(0).getValue() != null ? wordDTOS.get(0).getValue() : "")
                                    .append("、");
                        }
                    });
        }
        if (result.length() > 1) {
            return result.substring(0, result.length() - 1);
        } else {
            return result.toString();
        }
    }
}
