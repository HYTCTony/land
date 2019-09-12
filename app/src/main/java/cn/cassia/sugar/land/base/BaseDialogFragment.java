package cn.cassia.sugar.land.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Written by Mr.QingJie on 2017/6/20.
 */
public abstract class BaseDialogFragment extends AppCompatDialogFragment {

    private View mV = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mV == null) {
            mV = inflater.inflate(getContentViewResId(), container, false);
            ButterKnife.bind(this, mV);
            init();
        }
        ViewGroup parent = (ViewGroup) mV.getParent();
        if (parent != null)
            parent.removeView(mV);
        return mV;
    }

    protected abstract int getContentViewResId();

    protected abstract void init();

}
