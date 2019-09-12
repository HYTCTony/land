package cn.cassia.sugar.land.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.cassia.sugar.land.base.BaseViewActivity;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public abstract class BaseMvpViewActivity<V extends BaseContract.IBaseView, P extends BasePresenter> extends BaseViewActivity implements BaseContract.IBaseView {
    public P presenter;

    @Override
    protected void initP() {
        presenter = initPresenter();
        if (presenter != null) {
            presenter.attatchWindow(initModule(), this);
        }
    }

    protected abstract P initPresenter();

    protected abstract BaseModule initModule();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachWindow();
            presenter = null;
            System.gc();
        }
    }
}
