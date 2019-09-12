package cn.cassia.sugar.land.ui.map;

import android.os.Bundle;

import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.mvp.BaseModule;
import cn.cassia.sugar.land.mvp.BaseMvpFragment;
import cn.cassia.sugar.land.mvp.BasePresenter;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class MapFragment extends BaseMvpFragment<MapContract.IMapView, MapPresenter> implements MapContract.IMapView {

    public static MapFragment newInstance() {
        Bundle args = new Bundle();
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected MapPresenter initPresenter() {
        return new MapPresenter();
    }

    @Override
    protected BaseModule initModule() {
        return new MapModule();
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.fragment_map;
    }

    @Override
    protected void init() {

    }

    @Override
    protected boolean hasEvent() {
        return false;
    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public int getUid() {
        return 0;
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
    public void onResult(String s) {

    }
}
