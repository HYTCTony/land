package cn.cassia.sugar.land.ui.map;

import cn.cassia.sugar.land.mvp.BaseContract;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class MapContract {
    public interface IMapView extends BaseContract.IBaseView {

        void onResult(String s);
    }

    public interface IMapPresenter extends BaseContract.IBasePresenter {
        void getResult(String s);
    }

    public interface IMapModule extends BaseContract.IBaseModule {
        void getData(int uid, String pwd, MapPresenter presenter);

    }

}
