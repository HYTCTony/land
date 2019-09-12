package cn.cassia.sugar.land.ui.measure;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import cn.cassia.sugar.land.http.AppClient;
import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.http.UrlUtil;
import cn.cassia.sugar.land.mvp.BaseModule;

import static java.util.Arrays.asList;

/**
 * Created by qingjie on 2018-05-29.0029.
 */
public class MeasureModule extends BaseModule implements MeasureContract.IMeasureModule {

    @Override
    public void createRecord(int uid, String pwd, HashMap hashMap, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.land", "create", asList(hashMap)), callback);
    }

    @Override
    public void updateRecord(int uid, String pwd, int id, HashMap hashMap, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.land", "write", asList(asList(id), hashMap)), callback);
    }

    @Override
    public void deleteRecord(int uid, String pwd, int id, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.land", "unlink", asList(asList(id))), callback);
    }

    //查询、搜索地块
    @Override
    public void getAllLand(int uid, String pwd, String key, final int offset, final int pageSize, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.land", "search_read",
                asList(asList("&", "|", "|",
                asList("code", "like", "%" + key + "%"),                 //编号
                asList("land_region_id", "like", "%" + key + "%"),      //蔗区
                asList("farmer_id", "like", "%" + key + "%"),           //蔗农
                asList("state", "!=", "history"))), new HashMap() {{
            put("fields", asList("code", "land_region_id", "agrotype_id", "terrain_id", "place_name", "geo_create_date", "geo_create_uid", "geo_polygon", "geo_area", "state", "farmer_id", "state"));
            put("offset", offset);
            put("limit", pageSize);
            put("order", "code desc");
        }}), callback);
    }

    //附近地块
    @Override
    public void getNearbyLand(int uid, String pwd, final double minX, final double minY, final double maxX, final double maxY, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.land", "get_around_land", asList(), new HashMap() {
            {
                put("extent", asList(minX, minY, maxX, maxY));
                put("exp_ids", asList());
            }
        }), callback);
    }

    //地块详情
    @Override
    public void getLandDetail(int uid, String pwd, int landId, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setEnabledForExtensions(true);
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.land", "read", asList(landId)), callback);
    }

    //种植
    @Override
    public void getPlanting(int uid, String pwd, int landId, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setEnabledForExtensions(true);
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.land.planting", "search_read", asList(asList(asList("land_id", "=", landId))), new HashMap() {{
            put("fields", asList("crop_id", "plant_season_id", "planting_time", "code", "land_id"));
        }}), callback);
    }

    //榨季
    @Override
    public void getCurrentSeason(int uid, String pwd, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setEnabledForExtensions(true);
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.plant.season", "get_current_season", asList("")), callback);
    }

    //作物
    @Override
    public void getCrop(int uid, String pwd, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setEnabledForExtensions(true);
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.crop", "mobile_name_search", asList("")), callback);
    }

    //蔗农
    @Override
    public void getFarmer(int uid, String pwd, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setEnabledForExtensions(true);
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.farmer", "name_search",
                asList()), callback);
    }

    //地形
    @Override
    public void getTerrain(int uid, String pwd, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setEnabledForExtensions(true);
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.terrain", "name_search", asList("")), callback);
    }

    //蔗区
    @Override
    public void getSugarcane(int uid, String pwd, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setEnabledForExtensions(true);
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.land.region", "name_search", asList(""), new HashMap() {{
            put("args", asList(asList("level", "=", "community")));
        }}), callback);
    }

    //土壤
    @Override
    public void getAgrotype(int uid, String pwd, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setEnabledForExtensions(true);
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.agrotype", "name_search", asList("")), callback);
    }

}