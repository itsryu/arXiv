package br.com.arxiv.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonUtils {
    private static final Gson gson = new GsonBuilder().create();

    private JsonUtils() {}

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}