package com.gitee.cnsukidayo.anylanguageword.factory;

import android.content.Context;

import androidx.navigation.NavOptions;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.entity.Word;
import com.gitee.cnsukidayo.anylanguageword.handler.HomeMessageStreamHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.WordMeaningConvertHandler;
import com.gitee.cnsukidayo.anylanguageword.handler.impl.HomeMessageStreamHandlerImpl;
import com.gitee.cnsukidayo.anylanguageword.handler.impl.WordMeaningConvertHandlerImpl;
import com.gitee.cnsukidayo.anylanguageword.ui.markdown.plugin.GlobalMarkwonPlugin;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.CssInlineStyleParser;
import io.noties.markwon.html.HtmlPlugin;

public class StaticFactory {

    private StaticFactory() {
    }


    private static final class GsonHolder {
        static final Gson gson = new Gson();
    }

    private static final class ExecutorServiceHolder {
        // todo 需要修改
        static final ExecutorService executorService = Executors.newCachedThreadPool();
    }

    private static final class EmptyWordHolder {
        static final Word emptyWord = new Word();
    }

    private static final class WordMeaningConvertHandlerHolder {
        static final WordMeaningConvertHandler wordMeaningConvertHandler = new WordMeaningConvertHandlerImpl();
    }

    private static final class SimpleNavOptionsHolder {
        static final NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.fade_out)
                .setPopExitAnim(R.anim.slide_out_left)
                .build();
    }

    private static final class HomeMessageStreamHandlerHolder {
        static final HomeMessageStreamHandler homeMessageStreamHandler = new HomeMessageStreamHandlerImpl();
    }

    private static final class CssInlineStyleParserHolder {
        static final CssInlineStyleParser cssInlineStyleParser = CssInlineStyleParser.create();
    }

    /**
     * 得到Gson实例
     *
     * @return 返回Gson实例
     */
    public static Gson getGson() {
        return GsonHolder.gson;
    }

    /**
     * 得到全局的线程池,提高线程复用率
     *
     * @return 返回线程池
     */
    public static ExecutorService getExecutorService() {
        return ExecutorServiceHolder.executorService;
    }

    /**
     * 得到一个空的单词,注意该单词是单利Bean.
     *
     * @return 获取一个空单词
     */
    public static Word getEmptyWord() {
        return EmptyWordHolder.emptyWord;
    }

    /**
     * 得到处理单词的处理器,该处理器可以将一个Word实体对象中的中英文意思映射到一个Map中.<br>
     * 详情见{@link WordMeaningConvertHandler}接口
     *
     * @return 返回@{@link WordMeaningConvertHandler}接口的实现类
     */
    public static WordMeaningConvertHandler getWordMeaningConvertHandler() {
        return WordMeaningConvertHandlerHolder.wordMeaningConvertHandler;
    }

    /**
     * 得到navigation控制页面跳转的进入和退出动画参数默认示例对象
     *
     * @return 返回封装动画参数的NavOptions实例
     */
    public static NavOptions getSimpleNavOptions() {
        return SimpleNavOptionsHolder.navOptions;
    }

    /**
     * 得到主页信息流功能的处理Handler<br>
     * 详情见{@link HomeMessageStreamHandler}接口
     *
     * @return 返回封装动画参数的NavOptions实例
     */
    public static HomeMessageStreamHandler getHomeMessageStreamHandler() {
        return HomeMessageStreamHandlerHolder.homeMessageStreamHandler;
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
        return CssInlineStyleParserHolder.cssInlineStyleParser;
    }


}
