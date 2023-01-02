package com.gitee.cnsukidayo.traditionalenglish.factory;

import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StaticFactory {

    private StaticFactory() {
    }

    private static Gson gson;
    private static ExecutorService executorService;

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }


}
