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
 * Created by qingjie on 2018-06-14.0014.
 */
public class LandDetail {
    private String code;
    private String ext_code;
    private String[] land_region_id;
    private String[] agrotype_id;
    private String[] terrain_id;
    private String place_name;
    private String[] plant;

    private String geo_area;
    private String geo_polygon;
    private String geo_create_date;
    private String[] geo_create_uid;
    private double geo_min_x;
    private double geo_min_y;
    private double geo_max_x;
    private double geo_max_y;
    private double geo_centre_y;
    private double geo_centre_x;

    private int id;
    private String state;
    private String timeliness;

    private String[] farmer_id;
    private String[] recent_crop_id;
    private String[] recent_plant;
    private String recent_plant_time;
    private String recent_harvest_time;
    private String[] recent_sugarcane_plant_period;

    private String[] create_uid;
    private String[] write_uid;
    private String create_date;
    private String write_date;
    private String display_name;

    private String[] last_planting_crop_id;
    private String[] last_planting_plant_season_id;
    private String last_planting_planting_time;
    private String[] planting;

    public String[] getPlanting() {
        return planting;
    }

    public String getCode() {
        if (TextUtils.isEmpty(code)) {
            return "";
        }
        return code;
    }

    public String getExt_code() {
        if (TextUtils.isEmpty(ext_code)) {
            return "";
        }
        return ext_code;
    }

    public String[] getSugarcane_region_id() {
        if (land_region_id == null) {
            return new String[]{"", "请选择烟区"};
        }
        return land_region_id;
    }

    public String[] getAgrotype_id() {
        if (agrotype_id == null) {
            return new String[2];
        }
        return agrotype_id;
    }

    public String[] getTerrain_id() {
        if (terrain_id == null) {
            return new String[]{"", "请选择地形"};
        }
        return terrain_id;
    }

    public String getPlace_name() {
        if (TextUtils.isEmpty(place_name)) {
            return "";
        }
        return place_name;
    }

    public String[] getPlant() {
        if (plant == null) {
            return new String[2];
        }
        return plant;
    }

    public String getGeo_area() {
        if (TextUtils.isEmpty(geo_area)) {
            return "";
        }
        return geo_area;
    }

    public String getGeo_polygon() {
        if (TextUtils.isEmpty(geo_polygon)) {
            return "";
        }
        return geo_polygon;
    }

    public String getGeo_create_date() {
        if (TextUtils.isEmpty(geo_create_date)) {
            return "";
        }
        return geo_create_date;
    }

    public String[] getGeo_create_uid() {
        if (geo_create_uid == null) {
            return new String[2];
        }
        return geo_create_uid;
    }

    public double getGeo_min_x() {
        return geo_min_x;
    }

    public double getGeo_min_y() {
        return geo_min_y;
    }

    public double getGeo_max_x() {
        return geo_max_x;
    }

    public double getGeo_max_y() {
        return geo_max_y;
    }

    public double getGeo_centre_y() {
        return geo_centre_y;
    }

    public double getGeo_centre_x() {
        return geo_centre_x;
    }

    public int getId() {
        return id;
    }

    public String getState() {
        if (TextUtils.isEmpty(state)) {
            return "";
        }
        return state;
    }

    public String getTimeliness() {
        if (TextUtils.isEmpty(timeliness)) {
            return "";
        }
        return timeliness;
    }

    public String[] getRecent_farmer_id() {
        if (farmer_id == null) {
            return new String[2];
        }
        return farmer_id;
    }

    public String[] getRecent_crop_id() {
        if (recent_crop_id == null) {
            return new String[2];
        }
        return recent_crop_id;
    }

    public String[] getRecent_plant() {
        if (recent_plant == null) {
            return new String[2];
        }
        return recent_plant;
    }

    public String getRecent_plant_time() {
        if (TextUtils.isEmpty(recent_plant_time)) {
            return "";
        }
        return recent_plant_time;
    }

    public String getRecent_harvest_time() {
        if (TextUtils.isEmpty(recent_harvest_time)) {
            return "";
        }
        return recent_harvest_time;
    }

    public String[] getRecent_sugarcane_plant_period() {
        if (recent_sugarcane_plant_period == null) {
            return new String[2];
        }
        return recent_sugarcane_plant_period;
    }

    public String[] getCreate_uid() {
        if (create_uid == null) {
            return new String[2];
        }
        return create_uid;
    }

    public String[] getWrite_uid() {
        if (write_uid == null) {
            return new String[2];
        }
        return write_uid;
    }

    public String getCreate_date() {
        if (TextUtils.isEmpty(create_date)) {
            return "";
        }
        return create_date;
    }

    public String getWrite_date() {
        if (TextUtils.isEmpty(write_date)) {
            return "";
        }
        return write_date;
    }



    public String getDisplay_name() {
        if (TextUtils.isEmpty(display_name)) {
            return "";
        }
        return display_name;
    }

    public String[] getLast_planting_crop_id() {
        if (last_planting_crop_id == null) {
            return new String[2];
        }
        return last_planting_crop_id;
    }

    public String[] getLast_planting_grinding_season_id() {
        if (last_planting_plant_season_id == null) {
            return new String[2];
        }
        return last_planting_plant_season_id;
    }

    public String getLast_planting_time_name() {
        if (TextUtils.isEmpty(last_planting_planting_time)) {
            return "";
        }
        switch (last_planting_planting_time) {
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


    public String getLast_planting_planting_time() {
        return last_planting_planting_time;
    }

    /**
     * 由于出现服务器表字段为空返回false情况，因此自定义GSON解析适配器，过滤掉false字段，使数据类型统一
     */
    public static class LandDetailDeserilizer implements JsonDeserializer<LandDetail> {

        @Override
        public LandDetail deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonFilter jsonFilter = new JsonFilter(LandDetail.class);
            JsonObject obj = jsonFilter.valueFilter(json);
            LandDetail datas = new Gson().fromJson(obj, LandDetail.class);
            return datas;
        }
    }

}
