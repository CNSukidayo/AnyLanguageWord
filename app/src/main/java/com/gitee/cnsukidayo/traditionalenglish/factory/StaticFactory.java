package com.gitee.cnsukidayo.traditionalenglish.factory;

import com.gitee.cnsukidayo.traditionalenglish.entity.Word;
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

    public static Gson getGson() {
        return GsonHolder.gson;
    }

    public static ExecutorService getExecutorService() {
        return ExecutorServiceHolder.executorService;
    }

    public static Word getEmptyWord() {
        return EmptyWordHolder.emptyWord;
    }


}
