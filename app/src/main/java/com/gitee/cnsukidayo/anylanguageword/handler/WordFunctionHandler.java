package com.gitee.cnsukidayo.anylanguageword.handler;

import com.gitee.cnsukidayo.anylanguageword.entity.Word;
import com.gitee.cnsukidayo.anylanguageword.enums.CreditState;
import com.gitee.cnsukidayo.anylanguageword.enums.WordFunctionState;
import com.gitee.cnsukidayo.anylanguageword.enums.FlagColor;

import java.util.Set;

public interface WordFunctionHandler extends CategoryFunctionHandler {
    /**
     * 根据单词在列表中的位序获取单词,该方法的返回值会受到Chameleon的改变而改变.
     *
     * @param order 单词在列表中的位序,注意order的顺序是从0开始的.
     * @return 返回单词的引用
     */
    Word getWordByOrder(int order);

    /**
     * 获取当前指针指向的单词
     *
     * @return 返回单词引用
     */
    Word getCurrentWord();

    /**
     * 跳转到上一个单词,调用该方法会将指针指向传入的索引位置
     *
     * @return 返回单词引用
     */
    Word jumpPreviousWord();

    /**
     * 跳转到下一个单词,调用该方法会将指针指向传入的索引位置
     *
     * @return 返回单词引用
     */
    Word jumpNextWord();

    /**
     * 设置当前指针指向的位序
     *
     * @param currentOrder 设置位序
     */
    void setCurrentOrder(int currentOrder);

    /**
     * 跳转到某个单词,调用该方法会将指针指向传入的索引位置
     *
     * @param jumpOrder 跳转的目标位序
     * @return 返回单词引用
     */
    Word jumpToWord(int jumpOrder);

    /**
     * 得到当前指针指向的位序,currentOrder是对外显示的方法.
     * 实现类内部的currentIndex对外是不可见的.
     *
     * @return 返回order
     */
    int getCurrentOrder();

    /**
     * 得到单词列表长度
     *
     * @return 返回长度
     */
    int size();

    /**
     * 获得当前指针指向的单词的标记列表<br>
     * 根据索引获得当前单词所标记的颜色,颜色标记功能是不具备序列化能力的,只有一种情况单词的标记会被序列化<br>
     * 就是保存现场功能.<br>
     * 返回的集合只能用于查阅操作,所以返回的集合是不可变集合.<br>
     * 如果想要修改当前单词的标记,请使用:addFlagToCurrentWord(FlagColor)方法和removeFlagToCurrentWord(FlagColor)方法
     *
     * @return 返回当前单词所有的标记颜色集合
     * @see WordFunctionHandler#addFlagToCurrentWord(FlagColor)
     * @see WordFunctionHandler#removeFlagToCurrentWord(FlagColor)
     */
    Set<FlagColor> getCurrentWordFlagColor();

    /**
     * 为当前单词添加一个标记
     *
     * @param tobeAddFlag 待添加的标记颜色
     * @return {@code true} if this set did not already contain the specified
     * element
     */
    boolean addFlagToCurrentWord(FlagColor tobeAddFlag);

    /**
     * 为当前单词删除一个标记d
     *
     * @param tobeAddFlag 待删除的标记颜色
     * @return {@code true} if this set contained the specified element
     */
    boolean removeFlagToCurrentWord(FlagColor tobeAddFlag);

    /**
     * 得到当前的变色龙状态,默认状态为FlagColor.GREEN
     *
     * @return {@link FlagColor} 返回代表变色龙的颜色.
     * @see FlagColor
     */
    FlagColor getChameleon();

    /**
     * 设置变色龙颜色,此时函数的各个方法的返回值都会因为FlagColor的改变而改变.
     * 每次更改变色龙颜色之后,单词的索引都会从0开始
     *
     * @param chameleonColor FlagColor
     */
    void setChameleon(FlagColor chameleonColor);

    /**
     * 根据当前的chameleonColor进行打乱,也就是按颜色打乱.
     * 当前在打乱的状态下是不能使用变色龙模式的,要想使用变色龙模式必须先退出打乱模式.
     */
    void shuffle();

    /**
     * 还原单词列表为初始列表,该方法可以还原由 {@link WordFunctionHandler#shuffle()}方法和<br>
     * {@link WordFunctionHandler#shuffleRange(int, int)}方法改变的单词列表顺序.
     */
    void restoreWordList();

    /**
     * 得到当前单词功能的状态
     *
     * @return {@link WordFunctionState}代表返回的状态
     */
    WordFunctionState getWordFunctionState();

    /**
     * 根据左右区间打乱列表,注意这里的左右区间是闭区间.<br>
     * 一旦打乱则当前单词功能的状态会切换为{@link WordFunctionState#RANGE}
     *
     * @param start 左区间的值,下标从0开始
     * @param end   右区间的值,该值不应该超过列表的{@link WordFunctionHandler#size()}-1
     */
    void shuffleRange(int start, int end);

    /**
     *
     * @param creditState
     */
    void setCurrentCreditState(CreditState creditState);

    CreditState getCurrentCreditState();

}
