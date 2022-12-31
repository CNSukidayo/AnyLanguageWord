package com.gitee.cnsukidayo.traditionalenglish.factory;

import com.google.gson.Gson;

public class StaticFactory {

    private StaticFactory() {
    }

    private static final Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }

}
