package cn.cassia.sugar.land.ui.mine;

import cn.cassia.sugar.land.mvp.BasePresenter;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class MinePresenter extends BasePresenter<MineModule, MineActivity> implements MineContract.IMinePresenter {

    public void getData() {
        module.getData(view.getUid(), view.getPwd(), this);
    }

    @Override
    public void getResult(String s) {

    }
}
