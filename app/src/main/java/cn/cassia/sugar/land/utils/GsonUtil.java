package cn.cassia.sugar.land.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;

/**
 * Written by Mr.QingJie on 2018-1-10 0010.
 */
public class GsonUtil {

    private Gson mGson;
    private static GsonUtil mInstance;

    private GsonUtil() {
        GsonBuilder builder = new GsonBuilder();
        mGson = builder.create();
    }

    public static GsonUtil getInstance() {
        if (mInstance == null)
            mInstance = new GsonUtil();
        return mInstance;
    }

    public Gson getGson() {
        return mGson;
    }

}
