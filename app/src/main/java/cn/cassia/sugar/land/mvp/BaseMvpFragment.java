package cn.cassia.sugar.land.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cn.cassia.sugar.land.base.BaseFragment;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public abstract class BaseMvpFragment<V extends BaseContract.IBaseView, P extends BasePresenter> extends BaseFragment implements BaseContract.IBaseView {

    public P presenter;
    private View mV = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mV == null) {
            mV = inflater.inflate(getContentViewResId(), container, false);
            ButterKnife.bind(this, mV);
        }
        ViewGroup parent = (ViewGroup) mV.getParent();
        if (parent != null)
            parent.removeView(mV);
        presenter = initPresenter();
        if (presenter != null) {
            presenter.attatchWindow(initModule(), this);
        }
        init();
        return mV;
    }

    protected abstract P initPresenter();

    protected abstract BaseModule initModule();

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachWindow();
    }
}
