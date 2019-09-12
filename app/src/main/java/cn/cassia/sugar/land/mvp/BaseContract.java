package cn.cassia.sugar.land.mvp;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class BaseContract {
    public interface IBaseModule {

    }

    public interface IBasePresenter {

    }

    public interface IBaseView {
        int getUid();

        String getPwd();

        void showProgress();

        void hideProgress();

        void onFailure(int code, String err);

        void showToast(String msg);
    }
}
