package com.gitee.cnsukidayo.traditionalenglish.factory;

import androidx.navigation.NavOptions;

import com.gitee.cnsukidayo.traditionalenglish.R;
import com.gitee.cnsukidayo.traditionalenglish.entity.Word;
import com.gitee.cnsukidayo.traditionalenglish.handler.HomeMessageStreamHandler;
import com.gitee.cnsukidayo.traditionalenglish.handler.WordMeaningConvertHandler;
import com.gitee.cnsukidayo.traditionalenglish.handler.impl.HomeMessageStreamHandlerImpl;
import com.gitee.cnsukidayo.traditionalenglish.handler.impl.WordMeaningConvertHandlerImpl;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StaticFactory {

    private StaticFactory() {
    }


    private static final class GsonHolder {
        static final Gson gson = new Gson();
    }

    private static final class ExecutorServiceHolder {
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


}
