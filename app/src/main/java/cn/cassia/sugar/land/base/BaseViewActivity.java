package cn.cassia.sugar.land.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.utils.ActivityUtils;

/**
 * Written by Mr.QingJie on 2017/6/19.
 */
public abstract class BaseViewActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindColor(R.color.red)
    protected int red;
    @BindColor(R.color.text_color)
    protected int black;
    public Activity mContext;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewResId());
        ActivityUtils.getInstance().addActivity(this);
        ButterKnife.bind(this);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this,
                null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        Drawable drawable = a.getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
        drawable.setColorFilter(black, PorterDuff.Mode.MULTIPLY);
        toolbar.setNavigationIcon(drawable);
        toolbar.setTitleTextColor(black);
        toolbar.setSubtitleTextColor(black);
        initToolbar(toolbar);
        mContext = this;
        initP();
        initView();
    }

    protected void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtils.getInstance().finishActivity(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected void showNotice(String text) {
        Snackbar.make(toolbar, text, Snackbar.LENGTH_LONG).show();
    }

    protected void showNotice(String text, String clickText, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(toolbar, text, Snackbar.LENGTH_SHORT);
        snackbar.setAction(clickText, listener);
        snackbar.show();
    }

    protected abstract int getContentViewResId();

    protected abstract void initView();

    protected void initP() {

    }

}
