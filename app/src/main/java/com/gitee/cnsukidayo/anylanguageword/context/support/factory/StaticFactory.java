package com.gitee.cnsukidayo.anylanguageword.context.support.factory;

import android.content.Context;

import androidx.navigation.NavOptions;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.context.support.category.WordMetaInfoFilter;
import com.gitee.cnsukidayo.anylanguageword.context.support.category.WordMetaInfoFilterImpl;
import com.gitee.cnsukidayo.anylanguageword.handler.HomeMessageStreamHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.WordMeaningConvertHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.impl.HomeMessageStreamHandlerImpl;
import com.gitee.cnsukidayo.anylanguageword.handler.impl.WordMeaningConvertHandlerImpl;
import com.gitee.cnsukidayo.anylanguageword.ui.markdown.plugin.GlobalMarkwonPlugin;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.cnsukidayo.wword.model.dto.WordDTO;
import io.noties.markwon.Markwon;
import io.noties.markwon.html.CssInlineStyleParser;
import io.noties.markwon.html.HtmlPlugin;

public class StaticFactory {

    private StaticFactory() {
    }


    private static final class GsonHolder {
        static final Gson GSON = new Gson();
    }

    private static final class ExecutorServiceHolder {
        // todo 需要修改,参照阿里巴巴开发规范手册
        static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    }

    private static final class EmptyWordHolder {
        static final Map<Long, List<WordDTO>> EMPTY_WORD = Collections.unmodifiableMap(new HashMap<>());
    }

    private static final class WordMeaningConvertHandlerHolder {
        static final WordMeaningConvertHandler WORD_MEANING_CONVERT_HANDLER = new WordMeaningConvertHandlerImpl();
    }

    private static final class SimpleNavOptionsHolder {
        static final NavOptions NAV_OPTIONS = new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.fade_out)
                .setPopExitAnim(R.anim.slide_out_left)
                .build();
    }

    private static final class HomeMessageStreamHandlerHolder {
        static final HomeMessageStreamHandler HOME_MESSAGE_STREAM_HANDLER = new HomeMessageStreamHandlerImpl();
    }

    private static final class CssInlineStyleParserHolder {
        static final CssInlineStyleParser CSS_INLINE_STYLE_PARSER = CssInlineStyleParser.create();
    }

    private static final class WordMetaInfoFilterHolder {
        static final WordMetaInfoFilterImpl WORD_META_INFO_FILTER = new WordMetaInfoFilterImpl();
    }

    /**
     * 得到Gson实例
     *
     * @return 返回Gson实例
     */
    public static Gson getGson() {
        return GsonHolder.GSON;
    }

    /**
     * 得到全局的线程池,提高线程复用率
     *
     * @return 返回线程池
     */
    public static ExecutorService getExecutorService() {
        return ExecutorServiceHolder.EXECUTOR_SERVICE;
    }

    /**
     * 得到一个空的单词,注意该单词是单利Bean.
     *
     * @return 获取一个空单词
     */
    public static Map<Long, List<WordDTO>> getEmptyWord() {
        return EmptyWordHolder.EMPTY_WORD;
    }

    /**
     * 得到处理单词的处理器,该处理器可以将一个Word实体对象中的中英文意思映射到一个Map中.<br>
     * 详情见{@link WordMeaningConvertHandler}接口
     *
     * @return 返回@{@link WordMeaningConvertHandler}接口的实现类
     */
    public static WordMeaningConvertHandler getWordMeaningConvertHandler() {
        return WordMeaningConvertHandlerHolder.WORD_MEANING_CONVERT_HANDLER;
    }

    /**
     * 得到navigation控制页面跳转的进入和退出动画参数默认示例对象
     *
     * @return 返回封装动画参数的NavOptions实例
     */
    public static NavOptions getSimpleNavOptions() {
        return SimpleNavOptionsHolder.NAV_OPTIONS;
    }

    /**
     * 得到主页信息流功能的处理Handler<br>
     * 详情见{@link HomeMessageStreamHandler}接口
     *
     * @return 返回封装动画参数的NavOptions实例
     */
    public static HomeMessageStreamHandler getHomeMessageStreamHandler() {
        return HomeMessageStreamHandlerHolder.HOME_MESSAGE_STREAM_HANDLER;
    }

    /**
     * 得到全局的markdown解析对象
     *
     * @param context 上下文
     * @return 返回全局解析对象
     */
    public static Markwon getGlobalMarkwon(Context context) {
        return Markwon.builder(context)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(GlobalMarkwonPlugin.create(context))
                .build();
    }

    /**
     * 得到Css行内解析器
     *
     * @return 返回单利的Css行内解析器对象
     */
    public static CssInlineStyleParser getCssInlineStyleParser() {
        return CssInlineStyleParserHolder.CSS_INLINE_STYLE_PARSER;
    }

    /**
     * 得到单词元数据过滤Map获取器
     *
     * @return 返回单利的元数据过滤获取器
     */
    public static WordMetaInfoFilter getWordMetaInfoFilter() {
        return WordMetaInfoFilterHolder.WORD_META_INFO_FILTER;
    }


}
