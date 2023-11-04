package com.gitee.cnsukidayo.anylanguageword.enums.structure;

/**
 * 语种基础结构
 *
 * @author cnsukidayo
 * @date 2023/11/4 12:59
 */
public interface BaseStructure {

    /**
     * 得到当前单词结构对应的id
     *
     * @return 单词结构的id
     */
    Long getWordStructureId();

    /**
     * 得到当前单词结构对应的提示
     *
     * @return 返回提示信息在String资源中的编号
     */
    int getHint();

    /**
     * 设置当前结构是否可见
     *
     * @param visibility 可见性,详情见<br>
     *                   {@link android.view.View#GONE}<br>
     *                   {@link android.view.View#VISIBLE}<br>
     *                   {@link android.view.View#INVISIBLE}
     */
    void setVisibility(int visibility);

    /**
     * 获取当前结构是否可见
     *
     * @return 可见性,详情见<br>
     * {@link android.view.View#GONE}<br>
     * {@link android.view.View#VISIBLE}<br>
     * {@link android.view.View#INVISIBLE}
     */
    int getVisibility();

}
