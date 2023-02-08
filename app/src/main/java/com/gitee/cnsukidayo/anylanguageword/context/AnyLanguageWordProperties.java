package com.gitee.cnsukidayo.anylanguageword.context;

import java.io.File;

public class AnyLanguageWordProperties {

    private static File externalFilesDir = null;

    public static final String androidPackageName = "android";
    public static File getExternalFilesDir() {
        return AnyLanguageWordProperties.externalFilesDir;
    }

    public static void setExternalFilesDir(File externalFilesDir) {
        AnyLanguageWordProperties.externalFilesDir = externalFilesDir;
    }

    private AnyLanguageWordProperties() {

    }
}
