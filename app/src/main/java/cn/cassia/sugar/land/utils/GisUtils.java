package cn.cassia.sugar.land.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import cn.cassia.sugar.land.dialog.OpenGpsDialog;

/**
 * Created by qingjie on 2018-05-30.0030.
 */
public class GisUtils {

    public static void toOpenGPS(Activity context) {
        OpenGpsDialog.getInstance(context)
                .setCancelable(false)
                .setTitle("请打开GPS连接")
                .setMsg("为方便司机更容易接到您，请先打开GPS")
                .setPositiveButton("退出", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.finish();
                        ActivityUtils.getInstance().exit();
                    }
                })
                .setNegativeButton("设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 转到手机设置界面，用户设置GPS
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Toast.makeText(context, "打开后直接点击返回键即可，若不打开返回下次将再次出现", Toast.LENGTH_SHORT).show();
                        context.startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                    }
                }).show();
//          AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//            dialog.setTitle("请打开GPS连接");
//            dialog.setMessage("为方便司机更容易接到您，请先打开GPS");
//            dialog.setPositiveButton("设置", new android.content.DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface arg0, int arg1) {
//                    // 转到手机设置界面，用户设置GPS
//                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    Toast.makeText(context, "打开后直接点击返回键即可，若不打开返回下次将再次出现", Toast.LENGTH_SHORT).show();
//                    context.startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
//                }
//            });
//            dialog.setNeutralButton("退出", new android.content.DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface arg0, int arg1) {
//                    context.finish();
//                    arg0.dismiss();
//                }
//            });
//            dialog.setCancelable(false);
//            dialog.show();
    }


    /**
     * 判断GPS是否开启,GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static boolean isOPen(Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位,定位级别可以精确到街(通过24颗卫星定位,在室外和空旷的地方定位准确、速度快)
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置(也称作AGPS,辅助GPS定位。主要用于在室内或遮盖物(建筑群或茂密的深林等)密集的地方定位)
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;

    }

    /**
     * 判断GPS是否开启
     *
     * @param context
     * @return true 表示开启
     */
    public static boolean isGpsOPen(Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位,定位级别可以精确到街(通过24颗卫星定位,在室外和空旷的地方定位准确、速度快)
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gps;
    }


    /**
     * 强制打开GPS
     *
     * @param context
     */
    public static void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断网络连接是否已开
     *
     * @param context
     * @param flagDialog 网络未连接时是否显示对话框 true显示  false不显示
     * @return true 已打开  false 未打开
     */
    public static boolean isConn(Context context, boolean flagDialog) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
            if (flagDialog) searchNetwork(context);//弹出提示对话框
        }
        return false;
    }

    public static void searchNetwork(final Context context) {
        //提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }


    /**
     * 打开网络对话框
     *
     * @param context
     * @return
     */
    public static Dialog openNetworkDialog(final Context context) {
        //提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                //判断手机系统的版本  即API大于10 就是3.0或以上版本
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        return dialog;
    }


    /**
     * 打开蓝牙对话框
     *
     * @param context
     * @return
     */
    public static Dialog openBluetoothDialog(final Context context) {
        //提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("蓝牙设置提示").setMessage("蓝牙未开启,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        return dialog;
    }


    /**
     * 判断服务是否开启
     *
     * @param context
     * @param serviceName 服务名称
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        runningServiceInfos.clear();
        runningServiceInfos = null;
        return false;
    }

}
