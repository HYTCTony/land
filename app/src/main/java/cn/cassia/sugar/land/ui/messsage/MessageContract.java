package cn.cassia.sugar.land.ui.messsage;

import cn.cassia.sugar.land.mvp.BaseContract;
import cn.cassia.sugar.land.ui.mine.MineContract;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class MessageContract {
    public interface IMessageView extends BaseContract.IBaseView {
        void onResult(String s);
    }

    public interface IMessagePresenter extends BaseContract.IBasePresenter {
        void getResult(String s);
    }

    public interface IMessageModule extends BaseContract.IBaseModule {
        void getData(int uid, String pwd, MineContract.IMinePresenter presenter);
    }
}
