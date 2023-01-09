package com.gitee.cnsukidayo.traditionalenglish.handler;

import com.gitee.cnsukidayo.traditionalenglish.entity.Word;

/**
 * 单个分类单词功能的接口
 *
 * @author cnsukidayo
 * @date 2023/1/9 16:45
 */
public interface CategoryWordFunctionHandler {

    /**
     * 得到某个具体分类(收藏夹)中单词的数量
     *
     * @return 返回int类型
     */
    int currentCategorySize(int categoryID);


    /**
     * 将某个单词添加到某个分类中
     *
     * @param categoryID 分类ID
     * @param addWord    待添加的单词
     */
    void addWordToCategory(int categoryID, Word addWord);

    /**
     * 将某个单词从某个分类中移除
     *
     * @param categoryID 分类ID
     * @param position   单词在category中的位置
     */
    void removeWordFromCategory(int categoryID, int position);

    /**
     * 从某个分类中获取某个单词
     *
     * @param categoryID 分类ID
     * @param position   单词在category中的位置
     * @return 返回Word引用
     */
    Word getWordFromCategory(int categoryID, int position);

    /**
     * 交换某个分类中两个单词的位置
     *
     * @param categoryID     分类ID
     * @param sourcePosition 源单词的位置
     * @param targetPosition 目标单词的位置
     */
    void moveCategoryWord(int categoryID, int sourcePosition, int targetPosition);
    /*
    展示某个单词
    void showWord(int categoryID, int position);
     */

}
