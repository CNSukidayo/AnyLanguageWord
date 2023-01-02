package com.gitee.cnsukidayo.traditionalenglish.utils;

public class Strings {

    /**
     * 判断字符串不为空
     *
     * @param target 待被检查是否不为空的引用
     * @return 如果不为空返回true, 否则返回true
     */
    public static boolean notEmpty(String target) {
        return target != null && target.length() != 0;
    }

}
