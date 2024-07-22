package com.gitee.cnsukidayo.anylanguageword.handler;

import com.gitee.cnsukidayo.anylanguageword.entity.local.WordDTOLocal;

import java.util.List;

/**
 * @author cnsukidayo
 * @date 2024/7/21 10:38
 */
public interface WordSearchHandler {

    /**
     * 搜索单词
     *
     * @param key 关键词
     * @return 返回匹配的单词集合
     */
    List<WordDTOLocal> searchWord(String key);

}
