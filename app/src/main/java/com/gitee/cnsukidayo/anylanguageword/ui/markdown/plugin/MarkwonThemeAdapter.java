package com.gitee.cnsukidayo.anylanguageword.ui.markdown.plugin;

import android.graphics.Paint;

import androidx.annotation.NonNull;

import io.noties.markwon.core.MarkwonTheme;

/**
 * @author cnsukidayo
 * @date 2023/2/7 11:26
 */
public class MarkwonThemeAdapter extends MarkwonTheme {

    private static MarkwonTheme markwonTheme;

    /**
     * 注意如果要使用主题适配器则必须要调用该方法,即创建该适配器必须调用该静态方法.<br>
     * 否则适配器的各种功能是无法使用的,注意该适配器不支持动态修改MarkwonTheme,也就是主题的内的各种设置在第一次调用该静态方法就会确定<br>
     *
     * @param copyTheme 拷贝的主题
     * @return 返回的是一个单利Bean
     */
    public static MarkwonTheme createAdapterInstance(final MarkwonTheme copyTheme) {
        if (markwonTheme == null) {
            markwonTheme = new MarkwonThemeAdapter(copyTheme);
        }
        return markwonTheme;
    }

    private MarkwonThemeAdapter(final MarkwonTheme markwonTheme) {
        super(MarkwonTheme.builder(markwonTheme));
    }

    @Override
    public void applyHeadingTextStyle(@NonNull Paint paint, int level) {
        if (headingTypeface == null) {
            paint.setFakeBoldText(true);
        } else {
            paint.setTypeface(headingTypeface);
        }
        if (headingTextSizeMultipliers != null && headingTextSizeMultipliers.length >= level) {
            paint.setTextSize(headingTextSizeMultipliers[level - 1]);
        }
    }


}
