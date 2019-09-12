package cn.cassia.sugar.land.ui.measure;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.model.CurrentSeason;
import cn.cassia.sugar.land.model.LandDetail;
import cn.cassia.sugar.land.model.LandList;
import cn.cassia.sugar.land.model.Plant;
import cn.cassia.sugar.land.mvp.BasePresenter;
import cn.cassia.sugar.land.utils.GsonUtil;

import static java.util.Arrays.asList;

/**
 * Created by qingjie on 2018-09-05.0005.
 */
public class LandManagerPresenter extends BasePresenter<MeasureModule, LandManagementActivity> implements MeasureContract.IMeasurePresenter {

    @Override
    public void createRecord(String sugarcaneid, String agroType, String terrainId, String farmerId, String placeName, String geoPolygon, String geoArea, String cropId, String period, String seasonId) {
        checkViewAttached();
        if (TextUtils.isEmpty(geoPolygon) || TextUtils.isEmpty(geoArea)) {
            getMvpView().showToast("测量数据不完整，请进行测量！");
            return;
        }
        HashMap planting = new HashMap();
        planting.put("crop_id", cropId);
        planting.put("plant_season_id", seasonId);
//        planting.put("planting_time", TextUtils.isEmpty(period) ? false : period);
        HashMap hashMap = new HashMap();
        hashMap.put("land_region_id", TextUtils.isEmpty(sugarcaneid) ? false : sugarcaneid);
        hashMap.put("agrotype_id", TextUtils.isEmpty(agroType) ? false : agroType);
        hashMap.put("terrain_id", TextUtils.isEmpty(terrainId) ? false : terrainId);
        hashMap.put("farmer_id", TextUtils.isEmpty(farmerId) ? false : farmerId);//蔗农ID
        hashMap.put("place_name", TextUtils.isEmpty(placeName) ? false : placeName);
        hashMap.put("geo_polygon", geoPolygon);
        hashMap.put("geo_area", geoArea);
        if (!TextUtils.isEmpty(cropId)) hashMap.put("planting", asList(asList(0, false, planting)));
        Logger.d(hashMap.toString());
        getMvpView().showProgress();
        module.createRecord(getMvpView().getUid(), getMvpView().getPwd(), hashMap, new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().addRecordSuccess(content);
                }
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
                }
            }
        });
    }

    @Override
    public void updateRecord(int id, String sugarcaneid, String agroType, String terrainId, String farmerId, String placeName, String geoPolygon, String geoArea, String cropId, String period, String seasonId, String plantId) {
        checkViewAttached();
        if (TextUtils.isEmpty(geoPolygon) || TextUtils.isEmpty(geoArea)) {
            getMvpView().showToast("测量数据不完整，请进行测量！");
            return;
        }
        HashMap planting = new HashMap();
        planting.put("crop_id", cropId);//农作物ID
        planting.put("plant_season_id", seasonId);//榨季ID
//        planting.put("planting_time", TextUtils.isEmpty(period) ? false : period);//植期ID
        HashMap hashMap = new HashMap();
        hashMap.put("land_region_id", TextUtils.isEmpty(sugarcaneid) ? false : sugarcaneid);
        hashMap.put("agrotype_id", TextUtils.isEmpty(agroType) ? false : agroType);
        hashMap.put("terrain_id", TextUtils.isEmpty(terrainId) ? false : terrainId);
        hashMap.put("farmer_id", TextUtils.isEmpty(farmerId) ? false : farmerId);//蔗农ID
        hashMap.put("place_name", TextUtils.isEmpty(placeName) ? false : placeName);
        hashMap.put("geo_polygon", geoPolygon);
        hashMap.put("geo_area", geoArea);
        Logger.d(hashMap.toString());
        if (!TextUtils.isEmpty(cropId))
            hashMap.put("planting", TextUtils.isEmpty(plantId) ? asList(asList(0, false, planting)) : asList(asList(1, Integer.parseInt(plantId), planting)));
        getMvpView().showProgress();
        module.updateRecord(getMvpView().getUid(), getMvpView().getPwd(), id, hashMap, new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().updateRecordSuccess(content);
                }
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
                }
            }
        });
    }

    @Override
    public void deleteRecord(int id) {
        checkViewAttached();
        getMvpView().showProgress();
        module.deleteRecord(getMvpView().getUid(), getMvpView().getPwd(), id, new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().deleteRecordSuccess(content);
                }
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
                }
            }
        });
    }

    @Override
    public void getAllLand(String key, int offset, int pageSize) {
        checkViewAttached();
        module.getAllLand(getMvpView().getUid(), getMvpView().getPwd(), key, offset, pageSize, new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Logger.json(content);
                Gson gson = new GsonBuilder().registerTypeAdapter(LandList.class, new LandList.LandListDeserilizer()).excludeFieldsWithoutExposeAnnotation().create();
                List<LandList> landList = gson.fromJson(content, new TypeToken<List<LandList>>() {
                }.getType());
                String dataStr = gson.toJson(landList);
                if (isViewAttached()) getMvpView().initLandData(landList, dataStr);
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached())
                    getMvpView().onFailure(FAILURE_MESSAGE, JSON_EXCEPTION_ERROR_STRING);
            }
        });
    }

    @Override
    public void getNearbyLand(double minX, double minY, double maxX, double maxY) {
        checkViewAttached();
        module.getNearbyLand(getMvpView().getUid(), getMvpView().getPwd(), minX, minY, maxX, maxY, new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                if (isViewAttached()) getMvpView().initNearLandData(content);
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
            }
        });
    }

    @Override
    public void getLandDetail(int landId) {
        checkViewAttached();
        getMvpView().showProgress();
        module.getLandDetail(getMvpView().getUid(), getMvpView().getPwd(), landId, new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Logger.json(content);
                Gson gson = new GsonBuilder().registerTypeAdapter(LandDetail.class, new LandDetail.LandDetailDeserilizer()).excludeFieldsWithoutExposeAnnotation().create();
                List<LandDetail> landDetail = gson.fromJson(content, new TypeToken<List<LandDetail>>() {
                }.getType());
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().initLandDetailData(landDetail);
                }
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
            }
        });
    }

    @Override
    public void getTerrain() {
        checkViewAttached();
        module.getTerrain(getMvpView().getUid(), getMvpView().getPwd(), new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                List<String[]> datas = GsonUtil.getInstance().getGson().fromJson(content, new TypeToken<List<String[]>>() {
                }.getType());
                if (isViewAttached()) getMvpView().initTerrainData(datas);
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
            }
        });
    }

    @Override
    public void getSugarcane() {
        checkViewAttached();
        module.getSugarcane(getMvpView().getUid(), getMvpView().getPwd(), new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                List<String[]> datas = GsonUtil.getInstance().getGson().fromJson(content, new TypeToken<List<String[]>>() {
                }.getType());
                if (isViewAttached()) getMvpView().initSugarcaneData(datas);
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
            }
        });
    }

    @Override
    public void getAgrotype() {
        checkViewAttached();
        module.getAgrotype(getMvpView().getUid(), getMvpView().getPwd(), new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                List<String[]> datas = GsonUtil.getInstance().getGson().fromJson(content, new TypeToken<List<String[]>>() {
                }.getType());
                if (isViewAttached()) getMvpView().initAgrotype(datas);
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
            }
        });
    }

    @Override
    public void getPlanting(int landId) {
        checkViewAttached();
        module.getPlanting(getMvpView().getUid(), getMvpView().getPwd(), landId, new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Gson gson = new GsonBuilder().registerTypeAdapter(Plant.class, new Plant.PlantDeserilizer()).excludeFieldsWithoutExposeAnnotation().create();
                List<Plant> data = gson.fromJson(content, new TypeToken<List<Plant>>() {
                }.getType());
                if (isViewAttached()) {
                    getMvpView().initPlanting(data);
                }
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
            }
        });
    }

    @Override
    public void getCurrentSeason() {
        checkViewAttached();
        module.getCurrentSeason(getMvpView().getUid(), getMvpView().getPwd(), new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Logger.json(content);
                Gson gson = new GsonBuilder().registerTypeAdapter(CurrentSeason.class, new CurrentSeason.SeasonDeserilizer()).excludeFieldsWithoutExposeAnnotation().create();
                CurrentSeason data = gson.fromJson(content, CurrentSeason.class);
                if (isViewAttached()) getMvpView().initCurrentSeason(data);
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
            }
        });
    }

    @Override
    public void getCrop() {
        checkViewAttached();
        module.getCrop(getMvpView().getUid(), getMvpView().getPwd(), new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                List<String[]> data = GsonUtil.getInstance().getGson().fromJson(content, new TypeToken<List<String[]>>() {
                }.getType());
                if (isViewAttached()) getMvpView().initCrop(data);
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
            }
        });
    }

    @Override
    public void getFarmer() {
        checkViewAttached();
        module.getFarmer(getMvpView().getUid(), getMvpView().getPwd(), new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Logger.json(content);
                List<String[]> data = GsonUtil.getInstance().getGson().fromJson(content, new TypeToken<List<String[]>>() {
                }.getType());
                if (isViewAttached()) getMvpView().initFarmer(data);
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) getMvpView().onFailure(FINISH_MESSAGE, SERVER_ERROR_STRING);
            }
        });
    }

}

