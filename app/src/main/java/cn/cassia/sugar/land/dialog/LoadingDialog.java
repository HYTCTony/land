package cn.cassia.sugar.land.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseDialogFragment;


/**
 * Written by Mr.QingJie on 2017/6/21.
 */
public class LoadingDialog extends BaseDialogFragment {

    private static final String FRAGMENT_TAG = "LoadingDialog";

    @BindView(R.id.animationView)
    LottieAnimationView animationView;

    public static void show(FragmentActivity activity) {
        LoadingDialog fragment = (LoadingDialog) activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new LoadingDialog();
        }
        if (!fragment.isAdded()) {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }
    }

    public static void dismiss(FragmentActivity activity) {
        LoadingDialog fragment = (LoadingDialog) activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            activity.getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, 0);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.dialog_loading;
    }

    @Override
    protected void init() {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
