package cn.cassia.sugar.land.ui.messsage;

import android.os.Bundle;

import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.mvp.BaseModule;
import cn.cassia.sugar.land.mvp.BaseMvpFragment;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class MessageFragment extends BaseMvpFragment<MessageContract.IMessageView, MessagePresenter> implements MessageContract.IMessageView {

    public static MessageFragment newInstance() {
        Bundle args = new Bundle();
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected MessagePresenter initPresenter() {
        return new MessagePresenter();
    }

    @Override
    protected BaseModule initModule() {
        return new MessageModule();
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void init() {

    }

    @Override
    protected boolean hasEvent() {
        return false;
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
    public void onFailure(int code,String err) {

    }


    @Override
    public void showToast(String msg) {

    }

    @Override
    public void onResult(String s) {

    }
}
