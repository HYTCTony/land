package cn.cassia.sugar.land.web;

import android.content.Context;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by qingjie on 2017/6/26.
 */

public class WebViewUtil {

    private WebViewUtil() {

    }

    public static void init(Context context) {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                Logger.i("appx5内核初始化完成回调接口", arg0 ? "初始化成功" : "初始化失败");

            }

            @Override
            public void onCoreInitFinished() {
                Logger.i("X5预加载", "完成");
            }
        };
        QbSdk.initX5Environment(context.getApplicationContext(), cb);
    }


}
