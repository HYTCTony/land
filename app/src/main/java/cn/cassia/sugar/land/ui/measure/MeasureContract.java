package cn.cassia.sugar.land.ui.measure;

import java.util.HashMap;
import java.util.List;

import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.model.CurrentSeason;
import cn.cassia.sugar.land.model.LandDetail;
import cn.cassia.sugar.land.model.LandList;
import cn.cassia.sugar.land.model.Plant;
import cn.cassia.sugar.land.mvp.BaseContract;

/**
 * Created by qingjie on 2018-05-29.0029.
 */
public class MeasureContract {

    public interface IMeasureView extends BaseContract.IBaseView {
        void addRecordSuccess(String jsonStr);

        void updateRecordSuccess(String jsonStr);

        void deleteRecordSuccess(String jsonStr);

        void initLandData(List<LandList> landList, String dataStr);

        void initNearLandData(String jsonStr);

        void initLandDetailData(List<LandDetail> list);

        void initTerrainData(List<String[]> data);

        void initSugarcaneData(List<String[]> data);

        void initAgrotype(List<String[]> data);

        void initFarmer(List<String[]> data);

        void initPlanting(List<Plant> data);

        void initCurrentSeason(CurrentSeason season);

        void initCrop(List<String[]> data);
    }

    public interface IMeasurePresenter extends BaseContract.IBasePresenter {
        void createRecord(String sugarcaneid, String agroType, String terrainId, String farmerId, String placeName, String geoPolygon, String geoArea, String cropId, String period, String seasonId);

        void updateRecord(int landId, String sugarcaneid, String agroType, String terrainId, String farmerId, String placeName, String geoPolygon, String geoArea, String cropId, String period, String seasonId, String plantId);

        void deleteRecord(int landId);

        void getAllLand(String key, int offset, int pageSize);

        void getNearbyLand(double minX, double minY, double maxX, double maxY);

        void getLandDetail(int landId);

        void getTerrain();

        void getSugarcane();

        void getAgrotype();

        void getPlanting(int landId);

        void getCurrentSeason();

        void getCrop();

        void getFarmer();

    }

    public interface IMeasureModule extends BaseContract.IBaseModule {
        void createRecord(int uid, String pwd, HashMap hashMap, AsyncResponseHandler callback);

        void updateRecord(int uid, String pwd, int id, HashMap hashMap, AsyncResponseHandler callback);

        void deleteRecord(int uid, String pwd, int id, AsyncResponseHandler callback);

        void getAllLand(int uid, String pwd, String key, int offset, int pageSize, AsyncResponseHandler callback);

        void getNearbyLand(int uid, String pwd, double minX, double minY, double maxX, double maxY, AsyncResponseHandler callback);

        void getLandDetail(int uid, String pwd, int landId, AsyncResponseHandler callback);

        void getTerrain(int uid, String pwd, AsyncResponseHandler callback);

        void getSugarcane(int uid, String pwd, AsyncResponseHandler callback);

        void getAgrotype(int uid, String pwd, AsyncResponseHandler callback);

        void getPlanting(int uid, String pwd, int landId, AsyncResponseHandler callback);

        void getCurrentSeason(int uid, String pwd, AsyncResponseHandler callback);

        void getCrop(int uid, String pwd, AsyncResponseHandler callback);

        void getFarmer(int uid, String pwd, AsyncResponseHandler callback);
    }
}
