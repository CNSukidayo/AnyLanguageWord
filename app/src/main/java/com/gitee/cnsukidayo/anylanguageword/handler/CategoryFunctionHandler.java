package com.gitee.cnsukidayo.anylanguageword.handler;

import com.gitee.cnsukidayo.anylanguageword.entity.WordCategory;

import java.util.List;
import java.util.Map;

import io.github.cnsukidayo.wword.model.dto.WordDTO;

/**
 * @author cnsukidayo
 * @date 2023/1/8 19:48
 */
public interface CategoryFunctionHandler extends CategoryWordFunctionHandler {

    /**
     * 得到当前停留的单词(得到当前正在背诵的单词)
     * 该方法返回的单词是用户可能想要将其收藏到某个收藏夹内的单词.
     *
     * @return 返回当前正在背诵的单词引用
     */
    List<WordDTO> getCurrentWord();

    /**
     * 得到当前停留的单词(得到当前正在背诵的单词)<br>
     * 该方法返回的单词是用户可能想要将其收藏到某个收藏夹内的单词.<br>
     * 并转换成以单词结构id为Key的集合
     *
     * @return 返回当前正在背诵的单词引用
     */
    Map<Long, List<WordDTO>> getCurrentStructureWordMap();


    /**
     * 添加一个单词分类
     *
     * @param wordCategory 单词类别实例对象
     */
    void addNewCategory(WordCategory wordCategory);

    /**
     * 删除一个单词分类
     *
     * @param position 单词分类对象在列表中对应的位置
     */
    void removeCategory(int position);

    /**
     * 得到当前分类列表的长度
     *
     * @return 返回int值
     */
    int categoryListSize();

    /**
     * 根据单词分类在列表中的位置得到对应的WordCategory
     *
     * @param position 单词分类对象在列表中对应的位置
     * @return 返回WordCategory引用
     */
    WordCategory getWordCategoryByPosition(int position);

    /**
     * 根据规则计算出某个WordCategory的标题信息.<br>
     * 该方法的返回值会随着对应的WordCategory内容改变而改变.
     *
     * @param position 单词分类对象在列表中对应的位置
     * @return 返回计算出的标题
     */
    String calculationTitle(int position);

    /**
     * 根据规则计算出某个WordCategory的描述信息.<br>
     * 该方法的返回值会随着对应的WordCategory内容改变而改变.
     *
     * @param position 单词分类对象在列表中对应的位置
     * @return 返回计算出的描述信息
     */
    String calculationDescribe(int position);

    /**
     * 交换收藏夹列表中的两个收藏夹位置
     *
     * @param fromPosition 源收藏夹位置
     * @param toPosition   目标收藏夹位置
     */
    void moveCategory(int fromPosition, int toPosition);

}
