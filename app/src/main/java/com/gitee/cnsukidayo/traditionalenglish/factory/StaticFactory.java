package com.gitee.cnsukidayo.traditionalenglish.factory;

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

    public static Gson getGson() {
        return GsonHolder.gson;
    }

    public static ExecutorService getExecutorService() {
        return ExecutorServiceHolder.executorService;
    }


}
