package cn.cassia.sugar.land.ui.test;

import cn.cassia.sugar.land.mvp.BaseModule;
import cn.cassia.sugar.land.mvp.BaseMvpActivity;
import cn.cassia.sugar.land.mvp.BasePresenter;

/**
 * Created by qingjie on 2018-07-19.0019.
 */
public class Test2Activity extends BaseMvpActivity {

    @Override
    protected int getContentViewResId() {
        return 0;
    }

    @Override
    public int getUid() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected BaseModule initModule() {
        return null;
    }

    @Override
    public String getPwd() {
        return null;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onFailure(int code, String err) {

    }

    @Override
    public void showToast(String msg) {

    }
}
