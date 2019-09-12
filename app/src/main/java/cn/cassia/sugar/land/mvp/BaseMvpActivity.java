package cn.cassia.sugar.land.mvp;

import cn.cassia.sugar.land.base.BaseActivity;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public abstract class BaseMvpActivity<V extends BaseContract.IBaseView, P extends BasePresenter> extends BaseActivity implements BaseContract.IBaseView {
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
        presenter.detachWindow();
    }
}
