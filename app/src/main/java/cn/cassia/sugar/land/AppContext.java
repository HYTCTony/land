package cn.cassia.sugar.land;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;
import com.github.moduth.blockcanary.internal.BlockInfo;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import cn.cassia.sugar.land.http.UrlUtil;
import cn.cassia.sugar.land.spiderman.CrashModel;
import cn.cassia.sugar.land.spiderman.SpiderMan;
import cn.cassia.sugar.land.utils.ActivityUtils;
import cn.cassia.sugar.land.web.WebViewUtil;

/**
 * Created by qingjie on 2017/7/4.
 */
public class AppContext extends Application {

    private static final int CACHE_SIZE = 1024 * 1024 * 50;
    private static final int CONNECT_TIMEOUT = 15;
    private static final int READ_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 15;

    private static boolean loginstate = false;
    private static Context mContext;

    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        UrlUtil.init(getPackageName());
        initLog();
        WebViewUtil.init(this);
        registerActivityListener();
        initSpiderMan();
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
    }

    protected void initLog() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(1)
                .tag("KXY")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return Constant.DEBUG;
            }
        });
    }

    protected void initSpiderMan() {
        SpiderMan.getInstance()
                .init(this)
                //设置是否捕获异常，不弹出崩溃框
                .setEnable(Constant.DEBUG)
                //设置是否显示崩溃信息展示页面
                .showCrashMessage(Constant.DEBUG)
                //是否回调异常信息，友盟等第三方崩溃信息收集平台会用到,
                .setOnCrashListener((Thread t, Throwable ex, CrashModel model) -> {
                    //CrashModel 崩溃信息记录，包含设备信息
                });
    }

    private void registerActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityUtils.getInstance().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ActivityUtils.getInstance().finishActivity(activity);
            }
        });
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static boolean isLoginstate() {
        return loginstate;
    }

    public static void setLoginstate(boolean loginstate) {
        AppContext.loginstate = loginstate;
    }

}
