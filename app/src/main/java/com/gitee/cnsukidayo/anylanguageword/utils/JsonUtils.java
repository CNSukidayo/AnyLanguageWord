package com.gitee.cnsukidayo.anylanguageword.utils;

import androidx.annotation.NonNull;

import com.gitee.cnsukidayo.anylanguageword.factory.StaticFactory;

import java.io.IOException;

/**
 * @author cnsukidayo
 * @date 2023/2/10 13:46
 */
public class JsonUtils {

    public static <T> T readJson(@NonNull String externalPath, Class<T> classOfT) throws IOException {
        T result = StaticFactory.getGson().fromJson(FileUtils.readWithExternalExist(externalPath), classOfT);
        if (result == null) {
            try {
                result = classOfT.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        writeJson(externalPath,result);
        return result;
    }

    public static <T> void writeJson( String externalPath,@NonNull T targetObject) throws IOException {
        FileUtils.writeWithExternalExist(externalPath, StaticFactory.getGson().toJson(targetObject, targetObject.getClass()));
    }

}
