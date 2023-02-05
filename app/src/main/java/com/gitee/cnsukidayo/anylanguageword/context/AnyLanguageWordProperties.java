package com.gitee.cnsukidayo.anylanguageword.context;

import android.os.Environment;

import java.io.File;

public class AnyLanguageWordProperties {

    private AnyLanguageWordProperties() {

    }

    public static final String userInfoRoot = Environment.DIRECTORY_DOCUMENTS + File.separator + "userInfo";
    public static final String cacheRoot = Environment.DIRECTORY_DOCUMENTS + File.separator + "cache";
    public static final String userSettings = userInfoRoot + File.separator + "userSettings.json";
    private static File externalFilesDir = null;

    public static File getExternalFilesDir() {
        return AnyLanguageWordProperties.externalFilesDir;
    }

    public static void setExternalFilesDir(File externalFilesDir) {
        AnyLanguageWordProperties.externalFilesDir = externalFilesDir;
    }
}
