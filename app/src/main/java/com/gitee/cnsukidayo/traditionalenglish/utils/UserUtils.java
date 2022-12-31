package com.gitee.cnsukidayo.traditionalenglish.utils;

import android.os.Environment;

import com.gitee.cnsukidayo.traditionalenglish.context.TraditionalEnglishProperties;
import com.gitee.cnsukidayo.traditionalenglish.context.UserSettings;
import com.gitee.cnsukidayo.traditionalenglish.factory.StaticFactory;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class UserUtils {

    private static UserSettings userSettings;

    /**
     * 获取用户配置文件映射对象
     *
     * @return 返回用户设置配置文件映射对象
     */
    public static UserSettings getUserSettings() {
        if (userSettings == null) {
            synchronized (UserUtils.class) {
                Gson gson = StaticFactory.getGson();
                File userSettingsFiles = new File(TraditionalEnglishProperties.getExternalFilesDir(), TraditionalEnglishProperties.userSettings);
                if (userSettingsFiles.exists()) {
                    try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(userSettingsFiles), StandardCharsets.UTF_8)) {
                        userSettings = gson.fromJson(inputStreamReader, UserSettings.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    userSettings = new UserSettings();
                    upDateUserSettings();
                }
            }
        }
        return userSettings;
    }

    public static void upDateUserSettings() {
        File userSettingsFiles = new File(TraditionalEnglishProperties.getExternalFilesDir(), TraditionalEnglishProperties.userSettings);
        if (!userSettingsFiles.getParentFile().exists()) {
            userSettingsFiles.getParentFile().mkdirs();
        }
        String write = StaticFactory.getGson().toJson(userSettings, userSettings.getClass());
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(userSettingsFiles), StandardCharsets.UTF_8);) {
            outputStreamWriter.write(write);
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
