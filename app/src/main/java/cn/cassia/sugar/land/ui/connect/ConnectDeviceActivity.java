package cn.cassia.sugar.land.ui.connect;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import cn.cassia.sugar.land.ui.set.SetQianXunKeyActivity;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseViewActivity;
import cn.cassia.sugar.land.services.MagicBoxService;
import cn.cassia.sugar.land.ui.connect.adapter.ConnectDeviceAdapter;
import cn.cassia.sugar.land.ui.connect.adapter.ConnectLogAdapter;
import cn.cassia.sugar.land.model.DeviceBean;
import cn.cassia.sugar.land.utils.GisUtils;

/**
 * Created by qingjie on 2018-05-28.0028.
 */
public class ConnectDeviceActivity extends BaseViewActivity {
    public static final String ACTION_MAGIC_BOX = "action.update.location";

    @BindView(R.id.tv_network)
    AppCompatTextView tvNetwork;          //网络状态

    @BindView(R.id.tv_bluetooth)
    AppCompatTextView tvBluetooth;        //蓝牙状态

    @BindView(R.id.tv_connect)
    AppCompatTextView tvConnect;          //当前连接设备信息

    @BindView(R.id.rv_1)
    RecyclerView rvDevices;               //列表-已配对魔盒列表
    private boolean isDevicesShow = false;//是否显示魔盒列表
    ConnectDeviceAdapter cdAdapter;       //适配器 - 已配对魔盒列表
    List<DeviceBean> list;                //魔盒列表数据

    @BindView(R.id.iv)
    AppCompatImageView iv;

    @BindView(R.id.rv)
    RecyclerView rvLog;//魔盒状态日志列表
    ConnectLogAdapter rvLogAdapter;       //适配器 - 日志列表
    List<String> logList;                 //日志列表数据
    //接收 魔盒信息广播
    private MagicBoxReceiver receiver = null;
    private BluetoothAdapter adapter;
    //蓝牙地址
    private String bluetoothAddress = null;
    private Location lastLocation = null;
    //网络是否开启
    private boolean netWork = false;
    //蓝牙是否打开
    private boolean blueTooth = false;
    private SimpleDateFormat sdf;
    private Dialog dialog;

    public static void start(Context context) {
        Intent starter = new Intent(context, ConnectDeviceActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle("魔盒");
        super.initToolbar(toolbar);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_connect_device;
    }

    @Override
    protected void initView() {
        sdf = new SimpleDateFormat("MM-dd HH:mm:ss  ");
        //注册广播：魔盒数据接收
        if (receiver == null) {
            receiver = new MagicBoxReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_MAGIC_BOX);
            registerReceiver(receiver, filter);
        }
        //注册广播：网络状态变化
        IntentFilter networkChangeFilter = new IntentFilter();
        networkChangeFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, networkChangeFilter);
        //注册广播：蓝牙状态变化
        IntentFilter bluetoothChangeFilter = new IntentFilter();
        bluetoothChangeFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothChangeReceiver, bluetoothChangeFilter);
        //日志列表初始化
        logList = new ArrayList<>();
        rvLogAdapter = new ConnectLogAdapter();
        rvLog.setLayoutManager(new LinearLayoutManager(this, 1, false));
        rvLog.setAdapter(rvLogAdapter);
        //已连接魔盒列表初始化
        list = new ArrayList<>();
        cdAdapter = new ConnectDeviceAdapter();
        rvDevices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvDevices.setAdapter(cdAdapter);
        cdAdapter.setOnItemClickListener((adapter, view, position) -> {
            DeviceBean bean = list.get(position);
            String name = bean.name;
            String address = bean.address;
            String appkey = SharedPreferencesUtils.getAppKey(mContext);
            String appSecret = SharedPreferencesUtils.getAppSecret(mContext);
            if (bluetoothAddress != null && address != null && address.equals(bluetoothAddress) && TextUtils.isEmpty(appkey)
                    && TextUtils.isEmpty(appSecret)) {
                showNotice("请检查配置信息！");
            } else {
                bluetoothAddress = address;
                Intent intent = new Intent(mContext, MagicBoxService.class);
                intent.putExtra("bluetoothAddress", bluetoothAddress); //蓝牙地址
                startService(intent);//开启service
                tvConnect.setText(name + "(" + address + ")");
                rvDevices.setVisibility(View.GONE);
                for (int i = 0; i < list.size(); i++) {
                    DeviceBean b = list.get(i);
                    if (position == i) b.active = true;
                    else b.active = false;
                    list.set(i, b);
                }
                cdAdapter.setNewData(list);
                isDevicesShow = !isDevicesShow;
                iv.setBackgroundDrawable(getResources().getDrawable(isDevicesShow ? R.mipmap.ic_arrow_up : R.mipmap.ic_arrow_down));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //关于魔盒初始化
        initMagicBox();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                SetQianXunKeyActivity.start(ConnectDeviceActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @OnClick({R.id.iv, R.id.ll, R.id.ll_network, R.id.ll_bluetooth})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv:
            case R.id.ll:
                isDevicesShow = !isDevicesShow;
                rvDevices.setVisibility(isDevicesShow ? View.VISIBLE : View.GONE);
                iv.setBackgroundDrawable(getResources().getDrawable(isDevicesShow ? R.mipmap.ic_arrow_up : R.mipmap.ic_arrow_down));
                break;
            case R.id.ll_network:
                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
                break;
            case R.id.ll_bluetooth:
                Intent it = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivityForResult(it, 2);
                break;
        }
    }

    /**
     * 检查网络和蓝牙状态
     * 初始化已配对魔盒列表
     */
    private void initMagicBox() {
        if (GisUtils.isConn(this, false)) {
            netWork = true;
            tvNetwork.setText("已连接");
            tvNetwork.setTextColor(getResources().getColor(R.color.blue));
        } else {
            netWork = false;
            tvNetwork.setText("未连接");
            tvNetwork.setTextColor(getResources().getColor(R.color.red));
        }
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        if (adapter.isEnabled()) {
            blueTooth = true;
            tvBluetooth.setText("已开启");
            tvBluetooth.setTextColor(getResources().getColor(R.color.blue));
        } else {
            blueTooth = false;
            tvBluetooth.setText("未开启");
            tvBluetooth.setTextColor(getResources().getColor(R.color.red));
        }
        //魔盒已经打开
        if (GisUtils.isServiceRunning(getApplicationContext(), "cn.cassia.sugar.land.services.MagicBoxService")) {
            initMagiBoxList(adapter);
        } else {
            if (!netWork) {//网络
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
                dialog = GisUtils.openNetworkDialog(this);
                dialog.show();
                return;
            } else if (!blueTooth) {//蓝牙
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
                dialog = GisUtils.openBluetoothDialog(this);
                dialog.show();
                return;
            } else {
                initMagiBoxList(adapter);
            }
        }
    }

    /**
     * 获取所有已连接魔盒
     *
     * @param adapter
     */
    private void initMagiBoxList(BluetoothAdapter adapter) {
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        list = new ArrayList<>();
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices) {//检出所有已配对的 且是魔盒的设备
                int deviceType = device.getBluetoothClass().getMajorDeviceClass();
                if ("QX BeiDou Box".equals(device.getName()) || deviceType == 7936) {
                    DeviceBean item = new DeviceBean(device.getName(), device.getAddress(), bluetoothAddress != null && bluetoothAddress.equals(device.getAddress()));
                    list.add(item);
                }
            }
            cdAdapter.setNewData(list);
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
                if (bluetoothAddress == null)
                    tvConnect.setText("QX BeiDou Box" + "(" + bundle.getString("bluetoothAddress") + ")");
                bluetoothAddress = bundle.getString("bluetoothAddress");
                Location wzOutLocation = (Location) bundle.get("location");
                if (wzOutLocation != null) {
                    lastLocation = wzOutLocation;
                    float accuracy = lastLocation.getAccuracy();
                    String strAcc = accuracy + "";
                    if (accuracy == 6.0) strAcc = ">5.0";
                    String str = new BigDecimal(lastLocation.getLongitude()).setScale(6, BigDecimal.ROUND_DOWN).toString() + ", "
                            + new BigDecimal(lastLocation.getLatitude()).setScale(6, BigDecimal.ROUND_DOWN).toString() + " ," + strAcc;
                    logList.add(sdf.format(new Date()) + str);
                } else {
                    String str = bundle.getString("str");
                    int status = bundle.getInt("status");
                    if (status == 4001) {
                        tvConnect.setText("请选择魔盒进行连接");
                        bluetoothAddress = null;
                    }
                    logList.add(sdf.format(new Date()) + status + magicBoxStatus(status));
                }
                rvLogAdapter.setNewData(logList);
                rvLog.smoothScrollToPosition((rvLogAdapter.getItemCount() - 1));
            }
        }
    }

    /**
     * 广播接收器：蓝牙状态变化
     */
    private BroadcastReceiver bluetoothChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (dialog != null && dialog.isShowing()) dialog.dismiss();
                        initMagicBox();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        stopService(new Intent(context, MagicBoxService.class));
                        if (dialog != null && dialog.isShowing()) dialog.dismiss();
                        initMagicBox();
                        break;
                }
            }
        }
    };

    /**
     * 广播接收器：网络变化
     */
    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    //NetworkInfo netWorkInfo = connectivityManager.getActiveNetworkInfo();
                    //if (netWorkInfo!=null) {//网络连接
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    initMagicBox();
                    //}
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
            networkChangeReceiver = null;
        }
        if (bluetoothChangeReceiver != null) {
            unregisterReceiver(bluetoothChangeReceiver);
            bluetoothChangeReceiver = null;
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
