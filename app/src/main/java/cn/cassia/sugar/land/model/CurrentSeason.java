package cn.cassia.sugar.land.model;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import cn.cassia.sugar.land.utils.JsonFilter;

/**
 * Created by qingjie on 2018-09-06.0006.
 */
public class CurrentSeason {
    private String id;
    private String start_date;
    private String name;

    public String getId() {
        return id;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getName() {
        return name;
    }

    public static class SeasonDeserilizer implements JsonDeserializer<CurrentSeason> {

        @Override
        public CurrentSeason deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonFilter jsonFilter = new JsonFilter(CurrentSeason.class);
            JsonObject obj = jsonFilter.valueFilter(json);
            CurrentSeason datas = new Gson().fromJson(obj, CurrentSeason.class);
            return datas;
        }
    }
}
