package cn.cassia.sugar.land.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.lang.reflect.Type;

import cn.cassia.sugar.land.utils.JsonFilter;

/**
 * Created by qingjie on 2018-06-05.0005.
 */
public class LandList implements Serializable {
    @Expose
    public String geo_area;
    @Expose
    public String geo_polygon;
    @Expose
    public int id;
    @Expose(serialize = false)
    public String recent_crop_id;
    @Expose(serialize = false)
    public String recent_plant_time;
    @Expose(serialize = false)
    public String[] land_region_id;
    @Expose(serialize = false)
    public String[] recent_farmer_id;
    @Expose(serialize = false)
    public String[] terrain_id;
    @Expose(serialize = false)
    public String state;
    @Expose(serialize = false)
    public String place_name;
    @Expose(serialize = false)
    public String[] agrotype_id;
    @Expose(serialize = false)
    public String geo_create_date;
    @Expose(serialize = false)
    public String code;
    @Expose(serialize = false)
    public String[] geo_create_uid;
    @Expose(serialize = false)
    public String[] farmer_id;

    public String getGeo_area() {
        if (TextUtils.isEmpty(geo_area)) {
            return "";
        }
        return geo_area;
    }

    public String getGeo_create_date() {
        if (TextUtils.isEmpty(geo_create_date)) {
            return "";
        }
        return geo_create_date;
    }

    public String[] getSugarcane_region_id() {
        if (land_region_id == null) {
            return new String[]{"", "    "};
        }
        return land_region_id;
    }

    public String[] getRecent_farmer_id() {
        if (recent_farmer_id == null) {
            return new String[]{"", "    "};
        }
        return recent_farmer_id;
    }

    public String[] getTerrain_id() {
        if (terrain_id == null) {
            return new String[]{"", "    "};
        }
        return terrain_id;
    }

    public String[] getAgrotype_id() {
        if (agrotype_id == null) {
            return new String[]{"", "    "};
        }
        return agrotype_id;
    }

    public String getState() {
        if (TextUtils.isEmpty(geo_create_date)) {
            return "";
        }
        return state;
    }

    public String[] getFarmer_id() {
        if (farmer_id == null) {
            return new String[]{"", ""};
        }
        return farmer_id;
    }

    public String[] getGeo_create_uid() {
        if (geo_create_uid == null) {
            return new String[]{"", "    "};
        }
        return geo_create_uid;
    }

    /**
     * 由于出现服务器表字段为空返回false情况，因此自定义GSON解析适配器，过滤掉false字段，使数据类型统一
     */
    public static class LandListDeserilizer implements JsonDeserializer<LandList> {

        @Override
        public LandList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonFilter jsonFilter = new JsonFilter(LandList.class);
            JsonObject obj = jsonFilter.valueFilter(json);
            LandList datas = new Gson().fromJson(obj, LandList.class);
            return datas;
        }
    }

}