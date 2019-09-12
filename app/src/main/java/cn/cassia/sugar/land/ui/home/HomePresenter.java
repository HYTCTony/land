package cn.cassia.sugar.land.ui.home;

import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.mvp.BasePresenter;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class HomePresenter extends BasePresenter<HomeModule, HomeFragment> implements HomeContract.IHomePresenter {

    @Override
    public void getData() {
        checkViewAttached();
        getMvpView().showProgress();
        module.getData(getMvpView().getUid(), getMvpView().getPwd(), new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().onResult(content);
                }
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) {
                    getMvpView().onFailure(FAILURE_MESSAGE, SERVER_ERROR_STRING);
                    getMvpView().hideProgress();
                }
            }
        });
    }
}
