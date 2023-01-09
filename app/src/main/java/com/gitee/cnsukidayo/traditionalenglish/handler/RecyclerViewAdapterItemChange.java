package com.gitee.cnsukidayo.traditionalenglish.handler;

/**
 * 如果一个 {@link androidx.recyclerview.widget.RecyclerView.Adapter}实现了该接口,表明拥有该接口实例的引用的对象.<br>
 * 可以动态地调用该接口内的方法,动态地改变当前RecyclerView中的Item内容
 *
 * @author cnsukidayo
 * @date 2023/1/9 15:56
 */
public interface RecyclerViewAdapterItemChange<T> {

    void addItem(T t);

    void removeItem(T t);

}
