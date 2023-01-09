package com.gitee.cnsukidayo.traditionalenglish.factory;

import com.gitee.cnsukidayo.traditionalenglish.entity.Word;
import com.gitee.cnsukidayo.traditionalenglish.handler.WordMeaningConvertHandler;
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

    public static Gson getGson() {
        return GsonHolder.gson;
    }

    public static ExecutorService getExecutorService() {
        return ExecutorServiceHolder.executorService;
    }

    public static Word getEmptyWord() {
        return EmptyWordHolder.emptyWord;
    }

    public static WordMeaningConvertHandler getWordMeaningConvertHandler() {
        return WordMeaningConvertHandlerHolder.wordMeaningConvertHandler;
    }


}
