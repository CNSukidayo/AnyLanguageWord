package com.gitee.cnsukidayo.anylanguageword.ui.markdown.spanfactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gitee.cnsukidayo.anylanguageword.ui.markdown.plugin.MarkwonThemeAdapter;
import com.gitee.cnsukidayo.anylanguageword.ui.markdown.spanfactory.span.AppHeadingSpan;

import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.core.CoreProps;

/**
 * @author cnsukidayo
 * @date 2023/2/6 19:41
 */
public class AppHeadingSpanFactory implements SpanFactory {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
        return new AppHeadingSpan(MarkwonThemeAdapter.getAdapterInstance(), CoreProps.HEADING_LEVEL.require(props));
    }
}
