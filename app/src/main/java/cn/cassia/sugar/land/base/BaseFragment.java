package cn.cassia.sugar.land.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Written by Mr.QingJie on 2018-1-17 0017.
 */
public abstract class BaseFragment extends Fragment {

    private View mV = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mV == null) {
            mV = inflater.inflate(getContentViewResId(), container, false);
            ButterKnife.bind(this, mV);
        }
        ViewGroup parent = (ViewGroup) mV.getParent();
        if (parent != null)
            parent.removeView(mV);
        return mV;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    protected abstract int getContentViewResId();

    protected abstract void init();

    protected abstract boolean hasEvent();

    protected void showNotice(String text) {
        Snackbar.make(mV, text, Snackbar.LENGTH_LONG).show();
    }

    protected void showNotice(String text, String clickText, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(mV, text, Snackbar.LENGTH_SHORT);
        snackbar.setAction(clickText, listener);
        snackbar.show();
    }


}
