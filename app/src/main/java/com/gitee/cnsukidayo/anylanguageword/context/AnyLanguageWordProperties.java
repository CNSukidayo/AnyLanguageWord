package com.gitee.cnsukidayo.anylanguageword.context;

import android.os.Environment;

import java.io.File;

public class AnyLanguageWordProperties {

    private AnyLanguageWordProperties() {

    }

    public static final String cacheRoot = Environment.DIRECTORY_DOCUMENTS + File.separator + "cache";
    private static File externalFilesDir = null;
    public static final String androidPackageName = "android";

    public static File getExternalFilesDir() {
        return AnyLanguageWordProperties.externalFilesDir;
    }

    public static void setExternalFilesDir(File externalFilesDir) {
        AnyLanguageWordProperties.externalFilesDir = externalFilesDir;
    }
}
