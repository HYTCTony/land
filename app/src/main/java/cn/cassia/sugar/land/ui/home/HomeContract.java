package cn.cassia.sugar.land.ui.home;

import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.mvp.BaseContract;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class HomeContract {
    public interface IHomeView extends BaseContract.IBaseView {
        void onResult(String s);
    }

    public interface IHomePresenter extends BaseContract.IBasePresenter {
        void getData();
    }

    public interface IHomeLoginModule extends BaseContract.IBaseModule {
        void getData(int uid, String pwd, AsyncResponseHandler callback);
    }
}
