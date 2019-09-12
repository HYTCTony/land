package cn.cassia.sugar.land.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import android.widget.Toast;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;
import com.pop.android.common.beans.WzOutLocation;
import com.pop.android.location.api.outloc.WzChipDiffConfig;
import com.pop.android.location.api.outloc.WzOutLocationListener;
import com.qx.wz.sdk.magicbox.WzMagicBoxFactory;
import com.qx.wz.sdk.magicbox.WzMagicBoxManager;

import java.util.Map;

import cn.cassia.sugar.land.utils.StringKit;

public class MagicBoxService extends Service {

    private static String APP_KEY = "A4880hhmmn3o";
    private static String APP_SECRET = "c83431ea3048f889a80d41becbbb79808ceb8e798ce87d7cf9b136d03857c8ce";
    //    private static final String APP_KEY = "603947";
//    private static final String APP_SECRET = "095b63fb2acddc081bc7cba7f62334177dbee77a9bac0e5211fdc8a5c4538be0";
    //蓝牙地址
    private String bluetoothAddress;
    private WzChipDiffConfig mWzSdkConfig;
    private WzMagicBoxManager mWzOutLocationManager;
    private WzOutLocationListener wzOutLocationListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("bluetoothAddress")) {
            bluetoothAddress = intent.getStringExtra("bluetoothAddress");
            Log.i("KXY", "蓝牙地址：" + bluetoothAddress);
        } else {
            Toast.makeText(this, "蓝牙地址不存在！", Toast.LENGTH_SHORT).show();
        }
        String appkey = SharedPreferencesUtils.getAppKey(this);
        String appSecret = SharedPreferencesUtils.getAppSecret(this);
        if (!TextUtils.isEmpty(appkey) && !TextUtils.isEmpty(appSecret)) {
            APP_KEY = appkey;
            APP_SECRET = appSecret;
            try {
                mWzSdkConfig = new WzChipDiffConfig(APP_KEY, APP_SECRET, bluetoothAddress, 1);
                mWzOutLocationManager = WzMagicBoxFactory.getWzMagicBoxManager(this, mWzSdkConfig, "magicboxAPP");
                wzOutLocationListener = new LocationListener();
                mWzOutLocationManager.requestLocationUpdates(0, 0, wzOutLocationListener, new Handler(), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "请检查魔盒配置信息！", Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 魔盒持续获取高精度定位
     *
     * @author
     */
    class LocationListener implements WzOutLocationListener {

        @Override
        public void onStatusChanged(int status) {
            String str = "";
            //接收 SDK 的状态信息
            switch (status) {
                case 4000:
                    str = "正在启动";
                    break;
                case 4006:
                    str = "蓝牙重连";
                    break;
                case 4003:
                case 4002:
                case 4007:
                case 4004:
                case 1000:
                case 1021:
                    str = "正在定位";
                    break;
                case 4010:
                case 4001:
                    str = "正在关闭";
                    break;
                case 1001:
                case 4005:
                    str = "离线";
                    break;
                case 1004:
                case 1005:
                case 1022:
                case 2001:
                case 2002:
                case 2003:
                case 2004:
                case 2005:
                case 2006:
                case 2007:
                case 2008:
                case 2010:
                case 2011:
                    str = "连接失败";
                    break;
            }
            Log.i("北斗魔盒状态", status + str);
            updateWithNewLocation(null, status, str);
        }

        @Override
        public void onLocationChanged(WzOutLocation wzOutLocation) {
            //如果需要在高德地图上显示位置信息， 可以使用我们封装的坐标转换方法 Location location = EvilTransform.transform(wzOutLocation);
            /** 1.Time：位置 UTC 时间 (milliseconds)
             2.Latitude ：维度（double）
             3.Longitude：经度（double）
             4.Altitude：海拔 (double)
             5.Speed：速度 (m/s)
             6. Bearing：方向（设备水平方向的角度）
             7. Accuracy：定位精度（估算精度以米为单位）
             **/

            /**
             * double latitude =  wzOutLocation.getLatitude();
             double longitude = wzOutLocation.getLongitude();
             Map<String, String> map = wzOutLocation.getExtraMap();
             double accuracy = Double.valueOf(map.get(WzOutLocation.ACCURACY_RANGE));
             **/

            updateWithNewLocation(wzOutLocation, 0, "");
        }
    }


    //通过location得到经纬度信息，然后把经纬度信息通过广播发送给前段显示的Activity
    private void updateWithNewLocation(WzOutLocation wzOutLocation, int status, String str) {
        //service通过广播发送信息给activity
        Intent intent = new Intent();
        if (wzOutLocation != null) {
            Map<String, String> extraMap = wzOutLocation.getExtraMap();
            float accuracy = 0;
            String strAcc = null;
            if (StringKit.isDouble(extraMap.get(WzOutLocation.ACCURACY_RANGE).toString())) {
                accuracy = Float.valueOf(extraMap.get(WzOutLocation.ACCURACY_RANGE));

            } else {
                strAcc = extraMap.get(WzOutLocation.ACCURACY_RANGE).toString();
                accuracy = 6;//返回的精度为字符串 例如 “>5”
            }
            Location lastLocation = wzOutLocation;
            lastLocation.setAccuracy(accuracy);

            intent.putExtra("location", (Location) lastLocation);
            intent.putExtra("bluetoothAddress", bluetoothAddress);
        } else {
            intent.putExtra("bluetoothAddress", bluetoothAddress);
            intent.putExtra("status", status);
            intent.putExtra("str", str);
        }
        intent.setAction("action.update.location");//action与接收器相同
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("北斗魔盒状态", "正在关闭魔盒");
        if (mWzOutLocationManager != null && wzOutLocationListener != null) {
            mWzOutLocationManager.removeUpdates(wzOutLocationListener);
            mWzOutLocationManager.close();
            updateWithNewLocation(null, 4001, "关闭");
        }


    }

    /**
     *
     状态1.正在启动魔盒
     case 4000:str ="MAGICBOX_SDK启动";
     case 4003:str ="网络可用";
     case 4002:str ="正在定位";
     case 4007:str ="蓝牙已连接";
     case 4004:str ="蓝牙初始化中";
     case 4006:str ="蓝牙重连中";
     case 1000:str ="已连接到ntrip服务器";
     case 1021:str ="Ntrip播发数据正常";

     状态2.正在关闭魔盒
     case 4010: str ="Ntrip已停止";
     case 4001: str ="MAGICBOX_SDK关闭";

     状态3.魔盒正常连接 离线
     case 1001:str ="已断开与ntrip服务器的连接";
     case 4005:str ="蓝牙连接已断开";

     状态4.连接异常 启动失败
     case 1004:str ="网络不可用";
     case 1005:str ="APP KEY用户已经达到上限";
     case 1022:str ="Ntrip认证失败";
     case 2001:str ="缺少参数";
     case 2002: str ="差分账号不存在";
     case 2003:str ="差分账号重复";
     case 2004:str ="密码错误";
     case 2005: str ="差分账号不可用";
     case 2006:str ="没有有效的差分账号";
     case 2007:str ="POPUser不存在";
     case 2008: str ="服务端内部错误";
     case 2010: str ="账号已过期需续费";
     case 2011: str ="账号即将到期";

     状态5.魔盒正常连接 在线  接收到位置信息
     */
}