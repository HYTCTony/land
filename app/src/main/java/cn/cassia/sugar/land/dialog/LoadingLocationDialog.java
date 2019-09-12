package cn.cassia.sugar.land.dialog;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;

import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseDialogFragment;
import cn.cassia.sugar.land.ui.login.LoginActivity;
import cn.cassia.sugar.land.ui.measure.LandManagementActivity;
import cn.cassia.sugar.land.ui.mine.MineActivity;
import cn.cassia.sugar.land.utils.ActivityUtils;
import cn.cassia.sugar.land.utils.GisUtils;
import cn.cassia.sugar.land.utils.ScreenUtils;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;

/**
 * Created by qingjie on 2018-09-04.0004.
 */
public class LoadingLocationDialog extends DialogFragment {

    private static final String FRAGMENT_TAG = "LoadingLocationDialog";

    private View mV = null;

    @BindView(R.id.animationView)
    LottieAnimationView animationView;
    private static boolean isFinish;
    private static boolean isonFailure;

    private static onClickListener listener;

    @BindView(R.id.tv)
    AppCompatTextView tv;
    @BindView(R.id.iv)
    AppCompatImageView iv;
    @BindView(R.id.btn_auto_login)
    AppCompatTextView btn;

    public static void show(FragmentActivity activity, onClickListener l) {
        listener = l;
        LoadingLocationDialog fragment = (LoadingLocationDialog) activity
                .getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new LoadingLocationDialog();
        }
        if (!fragment.isAdded()) {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }
    }

    public static void dismiss(FragmentActivity activity) {
        new CountDownTimer(5 * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (isFinish) {
                    LoadingLocationDialog fragment = (LoadingLocationDialog) activity
                            .getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                    if (fragment != null) {
                        activity.getSupportFragmentManager().beginTransaction().remove(fragment)
                                .commitAllowingStateLoss();
                    }
                }
            }

            @Override
            public void onFinish() {
                LoadingLocationDialog fragment = (LoadingLocationDialog) activity
                        .getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                if (fragment != null) {
                    activity.getSupportFragmentManager().beginTransaction().remove(fragment)
                            .commitAllowingStateLoss();
                }
            }
        }.start();
    }

    public static void reload(FragmentActivity activity) {
        isonFailure = true;
        LoadingLocationDialog fragment = (LoadingLocationDialog) activity
                .getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            fragment.iv.setVisibility(View.VISIBLE);
            fragment.animationView.setVisibility(View.GONE);
            fragment.btn.setVisibility(View.VISIBLE);
            if (GisUtils.isConn(activity, false)) {
                fragment.tv.setText("服务器失联，请点击重试！");
            } else {
                fragment.tv.setText("网络未连接，请检查网络后，点击重试！");
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AlertDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (mV == null) {
            Window window = getDialog().getWindow();
            mV = inflater.inflate(getContentViewResId(), window.findViewById(android.R.id
                    .content), false);//需要用android.R.id.content这个view
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
            window.setLayout(-1, -2);
            ButterKnife.bind(this, mV);
            init();
        }
        ViewGroup parent = (ViewGroup) mV.getParent();
        if (parent != null)
            parent.removeView(mV);
        return mV;
    }

    protected int getContentViewResId() {
        return R.layout.dialog_loading_location;
    }

    protected void init() {
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        animationView.addAnimatorUpdateListener((ValueAnimator animation) -> {
            isFinish = animation.getAnimatedFraction() == 1;
        });
    }

    @OnClick(R.id.ll)
    void onClick() {
        if (isonFailure) {
            tv.setText("正在努力加载中...");
            iv.setVisibility(View.GONE);
            animationView.setVisibility(View.VISIBLE);
            listener.onButtonClick();
            isonFailure = false;
        }
    }

    @OnClick(R.id.btn_auto_login)
    void onAutoLoginClick() {
        SharedPreferencesUtils.clearUser(getActivity());
        ActivityUtils.getInstance().finishActivityclass(LandManagementActivity.class);
        LoginActivity.start(getActivity());
    }

    public interface onClickListener {
        void onButtonClick();
    }
}
