package cn.cassia.sugar.land.ui.test;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseActivity;

/**
 * Created by qingjie on 2018-06-01.0001.
 */
public class LocationTestActivity extends BaseActivity {
    @BindView(R.id.textView1)
    TextView LocationResult;
    @BindView(R.id.textView2)
    TextView LocationResult2;
    @BindView(R.id.textView3)
    TextView LocationResult3;

    LocationManager lm = null;

    Location myLocation = null;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss.SSSZ", Locale.CHINA);

    public static void start(Context context) {
        Intent starter = new Intent(context, LocationTestActivity.class);
        context.startActivity(starter);
    }

    private Context context;

    @Override
    protected int getContentViewResId() {
        return R.layout.location;
    }

    @Override
    protected void initView() {
        context = getApplicationContext();
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        lm.removeUpdates(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }

    LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (isBetterLocation(location, myLocation)) {
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

                String locationTime = sdf.format(new Date(location.getTime()));
                String currentTime = null;

                if (myLocation != null) {
                    currentTime = sdf.format(new Date(myLocation.getTime()));
                    myLocation = location;
                } else {
                    myLocation = location;
                }

                // 获取当前详细地址
                StringBuffer sb = new StringBuffer();
                if (myLocation != null) {
                    Geocoder gc = new Geocoder(context);
                    List<Address> addresses = null;
                    try {
                        addresses = gc.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        sb.append(address.getCountryName() + address.getLocality());
                        sb.append(address.getSubThoroughfare());

                    }
                }

                LocationResult.setText("经度：" + lon + "\n纬度：" + lat + "\n服务商：" + provider + "\n准确性：" + accuracy + "\n高度：" + altitude + "\n方向角：" + bearing
                        + "\n速度：" + speed + "\n上次上报时间：" + currentTime + "\n最新上报时间：" + locationTime + "\n您所在的城市：" + sb.toString());

            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Logger.i("onStatusChanged: " + provider);

        }

        @Override
        public void onProviderEnabled(String provider) {
            Logger.i("onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Logger.i("onProviderDisabled: " + provider);
        }

    };

    private static final int TWO_MINUTES = 1000 * 1 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
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
        }
        return false;
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

    private BottomSheetDialog mDialog;

    //初始化底部弹窗
    private void initBottomDialog() {
        mDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_measure, null);
        AppCompatImageView btn_search = view.findViewById(R.id.btn_search);
        AppCompatImageView btn_add = view.findViewById(R.id.btn_add);
        RelativeLayout btn_dismiss = view.findViewById(R.id.btn_dismiss);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(view);
        mDialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet).setBackgroundColor(this.getResources().getColor(R.color.transparent));
    }
}
