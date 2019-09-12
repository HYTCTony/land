package cn.cassia.sugar.land.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Written by Mr.QingJie on 2018-1-10 0010.
 */
public class ActivityUtils {
    private static ActivityUtils mActivityManagerUtils;

    static {
        mActivityManagerUtils = new ActivityUtils();
    }

    private ActivityUtils() {
        /**
         * 这里面写一些需要执行初始化的工作
         */
    }

    public static ActivityUtils getInstance() {
        return mActivityManagerUtils;

    }

    /**
     * 打开的activity
     **/

    private List<Activity> activities = new ArrayList<>();

    /**
     * 新建了一个activity
     *
     * @param activity
     */

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */

    public void finishActivity(Activity activity) {
        if (activity != null) {
            this.activities.remove(activity);
            activity.finish();
        }
    }

    /**
     * 应用退出，结束所有的activity
     */

    public void exit() {
        for (Activity activity : activities) {
            if (activity != null) {
                activity.finish();
            }
        }
        System.exit(0);
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivityclass(Class<?> cls) {
        if (activities != null) {
            for (Activity activity : activities) {
                if (activity.getClass().equals(cls)) {
                    this.activities.remove(activity);
                    finishActivity(activity);
                    break;
                }
            }
        }
    }

    /**
     * 结束指定类名以外的Activity
     */
    public void finishAllActivityExcept(Class<?> cls) {
        if (activities != null) {
            for (Activity activity : activities) {
                if (!activity.getClass().equals(cls)) {
                    this.activities.remove(activity);
                    finishActivity(activity);
                    break;
                }
            }
        }
    }

    public void startSettingActivity(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

}
