package cn.cassia.sugar.land.ui.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.orhanobut.logger.Logger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseViewActivity;
import cn.cassia.sugar.land.services.MagicBoxService;
import cn.cassia.sugar.land.ui.test.adapter.BleAdapter;


import static cn.cassia.sugar.land.ui.connect.ConnectDeviceActivity.ACTION_MAGIC_BOX;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * Created by qingjie on 2018-07-13.0013.
 */
public class BluetoothTestActivity extends BaseViewActivity {

    public static final String TAG = "kxy";
    @BindView(R.id.rv)
    RecyclerView rv;
    BleAdapter adapter;
    BluetoothClient mClient;

    private MagicBoxReceiver receiver = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss  ");
    List<SearchResult> list = new ArrayList<>();

    public static void start(Context context) {
        Intent starter = new Intent(context, BluetoothTestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle("蓝牙测试");
        super.initToolbar(toolbar);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_blueth;
    }

    @Override
    protected void initView() {
        receiver = new MagicBoxReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_MAGIC_BOX);
        registerReceiver(receiver, filter);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BleAdapter();
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            SearchResult bean = (SearchResult) adapter.getItem(position);
            mClient.registerBluetoothBondListener(mBluetoothBondListener);
            connectBle(bean.getAddress());
        });

        mClient = new BluetoothClient(this);
        if (mClient.isBluetoothOpened()) {
            searchBle(mClient);
        } else {
            mClient.openBluetooth();
            mClient.registerBluetoothStateListener(mBluetoothStateListener);
        }
    }

    //蓝牙开关监测
    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            if (openOrClosed) {
                Logger.v("蓝牙开关已打开");
                searchBle(mClient);
                mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
            }
        }
    };

    //配对状态变化
    private final BluetoothBondListener mBluetoothBondListener = new BluetoothBondListener() {
        @Override
        public void onBondStateChanged(String mac, int bondState) {
            // bondState = Constants.BOND_NONE, BOND_BONDING, BOND_BONDED
            switch (bondState) {
                case Constants.BOND_NONE:
                    Logger.v("还没有开始配对");
                    break;
                case Constants.BOND_BONDING:
                    Logger.v("正在配对...");
                    break;
                case Constants.BOND_BONDED:
                    Logger.v("配对成功");
                    Intent intent = new Intent(BluetoothTestActivity.this, MagicBoxService.class);
                    intent.putExtra("bluetoothAddress", mac); //蓝牙地址
                    startService(intent);//开启service
                    mClient.unregisterBluetoothBondListener(mBluetoothBondListener);
                    break;
            }
        }
    };

    private void connectBle(String MAC) {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)   // 连接如果失败重试3次
                .setConnectTimeout(30000)   // 连接超时30s
                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
                .build();

        mClient.connect(MAC, options, (code, data) -> {
            if (code == REQUEST_SUCCESS) {
                Intent intent = new Intent(BluetoothTestActivity.this, MagicBoxService.class);
                intent.putExtra("bluetoothAddress", MAC); //蓝牙地址
                startService(intent);//开启service
            } else {
                Toast.makeText(BluetoothTestActivity.this, "配对失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchBle(BluetoothClient mClient) {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(5000, 2)   // 先扫BLE设备2次，每次5s
                .searchBluetoothClassicDevice(3000) // 再扫经典蓝牙3s
                .build();

        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {

            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Beacon beacon = new Beacon(device.scanRecord);
                Logger.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
                if (!list.contains(device)) {
                    list.add(device);
                    adapter.setNewData(list);
                }
            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    /**
     * 魔盒广播接
     */
    public class MagicBoxReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_MAGIC_BOX)) {
                Bundle bundle = intent.getExtras();
                String bluetoothAddress = bundle.getString("bluetoothAddress");
                Location wzOutLocation = (Location) bundle.get("location");
                if (wzOutLocation != null) {
                    float accuracy = wzOutLocation.getAccuracy();
                    String strAcc = accuracy + "";
                    if (accuracy == 6.0) strAcc = ">5.0";
                    String str = new BigDecimal(wzOutLocation.getLongitude()).setScale(6, BigDecimal.ROUND_DOWN).toString() + ", "
                            + new BigDecimal(wzOutLocation.getLatitude()).setScale(6, BigDecimal.ROUND_DOWN).toString() + " ," + strAcc;
                    Logger.e(sdf.format(new Date()) + str);
                } else {
                    String str = bundle.getString("str");
                    int status = bundle.getInt("status");
                    Logger.e(sdf.format(new Date()) + status + magicBoxStatus(status));
                }
            }
        }
    }

    /**
     * 魔盒状态
     *
     * @param status
     * @return
     */
    private String magicBoxStatus(int status) {
        String str = "";
        switch (status) {
            case 4000:
                str = "MAGICBOX_SDK启动";
                break;
            case 4003:
                str = "网络可用";
                break;
            case 4002:
                str = "正在定位";
                break;
            case 4007:
                str = "蓝牙已连接";
                break;
            case 4004:
                str = "蓝牙初始化中";
                break;
            case 4006:
                str = "蓝牙重连中";
                break;
            case 1000:
                str = "已连接到ntrip服务器";
                break;
            case 1021:
                str = "Ntrip播发数据正常";
                break;
            case 4010:
                str = "Ntrip已停止";
                break;
            case 4001:
                str = "MAGICBOX_SDK关闭";
                break;
            case 1001:
                str = "已断开与ntrip服务器的连接";
                break;
            case 4005:
                str = "蓝牙连接已断开";
                break;
            case 1004:
                str = "网络不可用";
                break;
            case 1005:
                str = "APP KEY用户已经达到上限";
                break;
            case 1022:
                str = "Ntrip认证失败";
                break;
            case 2001:
                str = "缺少参数";
                break;
            case 2002:
                str = "差分账号不存在";
                break;
            case 2003:
                str = "差分账号重复";
                break;
            case 2004:
                str = "密码错误";
                break;
            case 2005:
                str = "差分账号不可用";
                break;
            case 2006:
                str = "没有有效的差分账号";
                break;
            case 2007:
                str = "POPUser不存在";
                break;
            case 2008:
                str = "服务端内部错误";
                break;
            case 2010:
                str = "账号已过期需续费";
                break;
            case 2011:
                str = "账号即将到期";
                break;
        }
        return str;
    }
}
