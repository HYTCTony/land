package cn.cassia.sugar.land.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.Serializable;
import java.lang.reflect.Type;

import cn.cassia.sugar.land.utils.JsonFilter;

/**
 * Created by qingjie on 2018-08-20.0020.
 */
public class Plant implements Serializable {
    private int id;
    private String planting_time;
    private String[] land_id;
    private String code;
    private String[] crop_id;
    private String[] plant_season_id;

    public int getId() {
        return id;
    }

    public String getPlanting_time() {
        if (TextUtils.isEmpty(planting_time)) {
            return "";
        }
        switch (planting_time) {
            case "xzz":
                return "新植期";
            case "sgn1":
                return "宿根1年";
            case "sgn2":
                return "宿根2年";
            case "sgn3":
                return "宿根3年";
            default:
                return "";
        }
    }

    public String[] getLand_id() {
        if (land_id == null) {
            return new String[]{"", "    "};
        }
        return land_id;
    }

    public String getCode() {
        if (TextUtils.isEmpty(code)) {
            return "";
        }
        return code;
    }

    public String[] getCrop_id() {
        if (crop_id == null) {
            return new String[]{"", "    "};
        }
        return crop_id;
    }

    public String[] getGrinding_season_id() {
        if (plant_season_id == null) {
            return new String[]{"", "    "};
        }
        return plant_season_id;
    }

    public static class PlantDeserilizer implements JsonDeserializer<Plant> {

        @Override
        public Plant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonFilter jsonFilter = new JsonFilter(Plant.class);
            JsonObject obj = jsonFilter.valueFilter(json);
            Plant datas = new Gson().fromJson(obj, Plant.class);
            return datas;
        }
    }
}
