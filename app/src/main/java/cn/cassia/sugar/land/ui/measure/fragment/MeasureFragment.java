package cn.cassia.sugar.land.ui.measure.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseDialogFragment;


/**
 * Created by qingjie on 2018-06-04.0004.
 */
public class MeasureFragment extends BaseDialogFragment {
    private static final String FRAGMENT_TAG = "measure";

    public static MeasureFragment newInstance() {
        Bundle args = new Bundle();
        MeasureFragment fragment = new MeasureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentActivity activity, int id, int modelsId) {
        MeasureFragment fragment = (MeasureFragment) activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = newInstance();
        }
        if (!fragment.isAdded()) {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Theme_AppCompat_DayNight_NoActionBar);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.fragment_measure;
    }

    @Override
    protected void init() {

    }

}
