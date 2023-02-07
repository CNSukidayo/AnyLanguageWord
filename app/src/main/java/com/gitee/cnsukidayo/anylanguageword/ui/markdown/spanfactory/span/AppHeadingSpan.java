package com.gitee.cnsukidayo.anylanguageword.ui.markdown.spanfactory.span;

import androidx.annotation.NonNull;

import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.core.spans.HeadingSpan;

/**
 * @author cnsukidayo
 * @date 2023/2/7 10:40
 */
public class AppHeadingSpan extends HeadingSpan {
    private final int level;

    public AppHeadingSpan(@NonNull MarkwonTheme theme, int level, int[] absoluteHeadingSize) {
        super(theme, level);
        this.level = level;
    }

}
