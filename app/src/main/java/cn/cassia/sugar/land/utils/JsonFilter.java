package cn.cassia.sugar.land.utils;

import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;

/**
 * Created by MOA on 2018/6/13.
 */
public class JsonFilter {

    private Class clz;
    private Field[] fields;

    public JsonFilter(Class clz) {
        this.clz = clz;
        this.fields = clz.getDeclaredFields();
    }

    /**
     * 遍历字段进行过滤
     *
     * @param json
     */
    public JsonObject valueFilter(JsonElement json) {
        JsonObject item = json.getAsJsonObject();
        for (Field field : fields) {
            if (item.has(field.getName())) {
                JsonElement jsonElement = item.get(field.getName());
                //当字段值为false，且实体类字段不是Boolean和boolean时，进行过滤
                if (jsonElement.isJsonPrimitive() && !TextUtils.equals(field.getType().getName(), "boolean")
                        && !TextUtils.equals(field.getType().getName(), "java.lang.Boolean")) {
                    String str = jsonElement.getAsString();
                    if (TextUtils.equals(str, "false"))
                        item.add(field.getName(), null);
                }
            }
        }
        return item;
    }

}
