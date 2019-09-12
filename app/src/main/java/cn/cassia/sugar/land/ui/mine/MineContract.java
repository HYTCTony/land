package cn.cassia.sugar.land.ui.mine;

import cn.cassia.sugar.land.mvp.BaseContract;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class MineContract {
    public interface IMineView extends BaseContract.IBaseView {
        void onResult(String s);
    }

    public interface IMinePresenter extends BaseContract.IBasePresenter {
        void getResult(String s);
    }

    public interface IMineModule extends BaseContract.IBaseModule {
        void getData(int uid, String pwd, MineContract.IMinePresenter presenter);
    }
}

