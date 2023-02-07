package com.gitee.cnsukidayo.anylanguageword.utils;

import com.gitee.cnsukidayo.anylanguageword.context.AnyLanguageWordProperties;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author cnsukidayo
 * @date 2023/2/6 12:56
 */
public class FileReaders {


    public static String readWithExternal(File target) throws IOException {
        return readWithExternal(target.getAbsolutePath());
    }

    public static String readWithExternal(String target) throws IOException {
        return readAll(AnyLanguageWordProperties.getExternalFilesDir(), target);
    }

    public static String readAll(File parent, String child) throws IOException {
        return readAll(parent.getAbsolutePath(), child);
    }

    public static String readAll(String parent, String child) throws IOException {
        return new String(Files.readAllBytes(Paths.get(parent, child)), StandardCharsets.UTF_8);
    }


}
