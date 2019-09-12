package cn.cassia.sugar.land.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.cassia.sugar.land.AppContext;

/**
 * Created by qingjie on 2018-06-01.0001.
 */
public class LocationUtils {
    private final long REFRESH_TIME = 1000;//位置刷新间隔
    private final float METER_POSITION = 0.1f;//位置改变多少后刷新
    private ILocationListener mLocationListener;//对外接口

    private SimpleDateFormat sdf;
    private LocationManager manager;
    private Context mContext;

    public LocationUtils(Context context) {
        mContext = context;
        manager = getLocationManager(mContext);
        sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss.SSSZ", Locale.CHINA);
    }

    /**
     * 定位监听
     */
    public void addLocationListener(ILocationListener locationListener) {
        addLocationListener(REFRESH_TIME, METER_POSITION, locationListener);
    }

    /**
     * 定位监听
     */
    public void addLocationListener(long time, float meter, ILocationListener locationListener) {
        if (locationListener != null) {
            mLocationListener = locationListener;
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, time, meter, listener);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, meter, listener);
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {//定位改变监听
            // 获取纬度
            double lat = location.getLatitude();
            // 获取经度
            double lon = location.getLongitude();
            // 位置提供者
            String provider = location.getProvider();
            // 位置的准确性
            float accuracy = location.getAccuracy();
            // 高度信息
            double altitude = location.getAltitude();
            // 方向角
            float bearing = location.getBearing();
            // 速度 米/秒
            float speed = location.getSpeed();
            // Logger.i("经度：" + lon + "  纬度：" + lat + "  提供者：" + provider + "  准确性:" + accuracy);
            if (mLocationListener != null) {
                mLocationListener.onSuccessLocation(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {//定位状态监听
            if (LocationProvider.OUT_OF_SERVICE == status) {
                //GPS服务丢失,切换至网络定位
            }
        }

        @Override
        public void onProviderEnabled(String provider) {//定位状态可用监听

        }

        @Override
        public void onProviderDisabled(String provider) {//定位状态不可用监听

        }
    };

    /**
     * 取消定位监听
     */
    public void unRegisterListener() {
        if (manager != null) {
            //移除定位监听
            manager.removeUpdates(listener);
            manager = null;
        }
        if (listener != null) {
            listener = null;
        }
        //Logger.d("Stop positioning");
    }

    private LocationManager getLocationManager(@NonNull Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * 自定义接口
     */
    public interface ILocationListener {
        void onSuccessLocation(Location location);
    }

    //地理编码获取当前详细地址
    public StringBuffer geocoder(Location location) {
        StringBuffer sb = new StringBuffer();
        if (location != null) {
            Geocoder gc = new Geocoder(mContext);
            List<Address> addresses = null;
            try {
                addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
                sb.append("获取城市失败");
                return sb;
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                if (address.getLocality().length() <= 3) {
                    sb.append(address.getCountryName());
                }
                sb.append(address.getLocality());
                // sb.append(address.getSubThoroughfare());
            }
        }
        return sb;
    }

    private final long ONE_MINUTES = 2000 * 1;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    public boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        } else return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /*----------------------------------以下为只获取一次定位信息的方法-------------------------------*/

    /**
     * GPS获取定位方式
     */
    public Location getGPSLocation(@NonNull Context context) {
        Location location = null;
        LocationManager manager = getLocationManager(context);
        //高版本的权限检查
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//是否支持GPS定位
            //获取最后的GPS定位信息，如果是第一次打开，一般会拿不到定位信息，一般可以请求监听，在有效的时间范围可以获取定位信息
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return location;
    }

    /**
     * network获取定位方式
     */
    public Location getNetWorkLocation(Context context) {
        Location location = null;
        LocationManager manager = getLocationManager(context);
        //高版本的权限检查
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {//是否支持Network定位
            //获取最后的network定位信息
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    /**
     * 获取最好的定位方式
     */
    public Location getBestLocation() {
        Criteria criteria = getCriteria();
        Location location;
        LocationManager manager = getLocationManager(mContext);
        if (criteria == null) {
            criteria = new Criteria();
        }
        String provider = manager.getBestProvider(criteria, true);
        if (TextUtils.isEmpty(provider)) {
            //如果找不到最适合的定位，使用network定位
            location = getNetWorkLocation(mContext);
        } else {
            //高版本的权限检查
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            //获取最适合的定位方式的最后的定位权限
            location = manager.getLastKnownLocation(provider);
        }
        return location;
    }

    private Criteria getCriteria() {
        Criteria c = new Criteria();
        c.setPowerRequirement(Criteria.POWER_LOW);//设置耗电量为低耗电
        c.setBearingAccuracy(Criteria.ACCURACY_COARSE);//设置精度标准为粗糙
        c.setAltitudeRequired(false);//设置海拔不需要
        c.setBearingRequired(false);//设置导向不需要
        c.setAccuracy(Criteria.ACCURACY_COARSE);//设置精度为低
        c.setCostAllowed(false);//设置成本为不需要
        return c;
    }
}
