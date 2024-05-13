package com.gitee.cnsukidayo.anylanguageword.context;

import java.io.File;

public class AnyLanguageWordProperties {

    private static File externalFilesDir = null;

    public static final String androidPackageName = "android";

    public static final String ip = "192.168.148.63";
    public static final String port = "8200";
    public static final String imagePrefix = "https://" + AnyLanguageWordProperties.ip + ":" + AnyLanguageWordProperties.port + "/";

    public static File getExternalFilesDir() {
        return AnyLanguageWordProperties.externalFilesDir;
    }

    public static void setExternalFilesDir(File externalFilesDir) {
        AnyLanguageWordProperties.externalFilesDir = externalFilesDir;
    }

    private AnyLanguageWordProperties() {

    }
}
