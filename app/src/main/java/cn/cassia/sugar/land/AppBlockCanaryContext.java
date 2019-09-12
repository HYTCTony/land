package cn.cassia.sugar.land;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.github.moduth.blockcanary.BlockCanaryContext;

import java.util.List;

/**
 * Created by qingjie on 2018-07-03.0003.
 */
public class AppBlockCanaryContext extends BlockCanaryContext {
    private static final String TAG = "AppBlockCanaryContext";

    @Override
    public String provideQualifier() {
        String qualifier = "";
        try {
            PackageInfo info = AppContext.getAppContext().getPackageManager()
                    .getPackageInfo(AppContext.getAppContext().getPackageName(), 0);
            qualifier += info.versionCode + "_" + info.versionName + "_Sugar";
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "provideQualifier exception", e);
        }
        return qualifier;
    }

    @Override
    public String provideUid() {
        return "admin";
    }

    @Override
    public String provideNetworkType() {
        return "WI-FI";
    }

    @Override
    public int provideMonitorDuration() {
        return 9999;
    }

    @Override
    public int provideBlockThreshold() {
        return 1000;
    }

    @Override
    public boolean displayNotification() {
        return BuildConfig.DEBUG;
    }

    @Override
    public List<String> concernPackages() {
        List<String> list = super.provideWhiteList();
        list.add("");
        return list;
    }

    @Override
    public List<String> provideWhiteList() {
        List<String> list = super.provideWhiteList();
        list.add("com.github.bumptech.glide");
        list.add("com.airbnb.android");
        return list;
    }

    @Override
    public boolean stopWhenDebugging() {
        return true;
    }
}