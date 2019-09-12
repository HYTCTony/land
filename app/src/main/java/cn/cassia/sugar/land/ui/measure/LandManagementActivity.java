package cn.cassia.sugar.land.ui.measure;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.tencent.smtt.sdk.WebChromeClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.dialog.LandPlantDialog;
import cn.cassia.sugar.land.dialog.LoadingDialog;
import cn.cassia.sugar.land.dialog.LoadingLocationDialog;
import cn.cassia.sugar.land.dialog.searchbox.SearchFragment;
import cn.cassia.sugar.land.http.UrlUtil;
import cn.cassia.sugar.land.location.LocationUtils;
import cn.cassia.sugar.land.model.CurrentSeason;
import cn.cassia.sugar.land.model.LandDetail;
import cn.cassia.sugar.land.model.LandList;
import cn.cassia.sugar.land.model.Plant;
import cn.cassia.sugar.land.mvp.BaseModule;
import cn.cassia.sugar.land.mvp.BaseMvpActivity;
import cn.cassia.sugar.land.ui.connect.ConnectDeviceActivity;
import cn.cassia.sugar.land.ui.measure.adapter.DialogAdapter;
import cn.cassia.sugar.land.ui.measure.adapter.LandListAdapter;
import cn.cassia.sugar.land.ui.mine.MineActivity;
import cn.cassia.sugar.land.utils.ActivityUtils;
import cn.cassia.sugar.land.utils.CharacterParser;
import cn.cassia.sugar.land.utils.FormatUtils;
import cn.cassia.sugar.land.utils.GisUtils;
import cn.cassia.sugar.land.utils.GsonUtil;
import cn.cassia.sugar.land.utils.ScreenUtils;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;
import cn.cassia.sugar.land.utils.SpanUtils;
import cn.cassia.sugar.land.web.WebView;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by qingjie on 2018-09-05.0005.
 */
public class LandManagementActivity extends BaseMvpActivity<MeasureContract.IMeasureView, LandManagerPresenter>
        implements MeasureContract.IMeasureView {

    private static final int RC_PERM = 100;
    private static final int OPEN_SIZE = 4;
    @BindView(R.id.iv)
    AppCompatImageView iv;
    @BindView(R.id.web)
    WebView web;
    @BindView(R.id.ll)
    LinearLayoutCompat ll;
    @BindView(R.id.btn)
    RelativeLayout btn;
    @BindView(R.id.rl)
    RelativeLayout rl;

    @BindView(R.id.tv_box_statu)
    AppCompatTextView tvBoxStatu;
    @BindView(R.id.tv_longitude)
    AppCompatTextView tvLongitude;
    @BindView(R.id.tv_accuracy)
    AppCompatTextView tvAccuracy;
    @BindView(R.id.tv_latitude)
    AppCompatTextView tvLatitude;
    @BindView(R.id.tv_address)
    AppCompatTextView tvAddress;
    @BindView(R.id.tv_provider)
    AppCompatTextView tvProvider;

    @BindView(R.id.btn_right)
    AppCompatImageView btnRight;
    @BindView(R.id.btn_left)
    AppCompatImageView btnLeft;
    @BindView(R.id.tv_content)
    AppCompatTextView tvContent;
    @BindView(R.id.btn_search)
    LinearLayoutCompat btnSearch;
    @BindView(R.id.btn_search_clean)
    AppCompatImageView btnSearchClean;
    @BindView(R.id.tv_search_content)
    AppCompatTextView tvSearchContent;

    @BindView(R.id.btn_1)
    AppCompatTextView btn1;
    @BindView(R.id.btn_2)
    AppCompatTextView btn2;
    @BindView(R.id.btn_3)
    AppCompatTextView btn3;
    @BindView(R.id.btn_4)
    AppCompatTextView btn4;
    @BindView(R.id.et_place_name)
    AppCompatEditText etPlaceName;
    @BindView(R.id.et_area)
    AppCompatTextView etArea;
    @BindView(R.id.tv_name)
    AppCompatTextView tvName;
    @BindView(R.id.tv_time)
    AppCompatTextView tvTime;
    @BindView(R.id.tv_season)
    AppCompatTextView tvSeason;
    @BindView(R.id.tv_plant)
    AppCompatTextView tvPlant;
    @BindView(R.id.ll_period)
    LinearLayoutCompat llperiod;
    @BindView(R.id.tv_period)
    AppCompatTextView tvPeriod;
    @BindView(R.id.et_remarks)
    AppCompatEditText etRemarks;
    @BindView(R.id.rl_form)
    LinearLayoutCompat rlForm;

    @BindView(R.id.tv_latitude_measure)
    AppCompatTextView tvLatitudeMeasure;
    @BindView(R.id.tv_longitud_measure)
    AppCompatTextView tvLongitudMeasure;
    @BindView(R.id.tv_accuracy_measure)
    AppCompatTextView tvAccuracyMeasure;
    @BindView(R.id.tv_box_statu_measure)
    AppCompatTextView tvBoxStatuMeasure;
    @BindView(R.id.tv_area)
    AppCompatTextView tvArea;
    @BindView(R.id.iv1)
    AppCompatImageView iv1;
    @BindView(R.id.iv2)
    AppCompatImageView iv2;
    @BindView(R.id.iv3)
    AppCompatImageView iv3;
    @BindView(R.id.iv4)
    AppCompatImageView iv4;
    @BindView(R.id.ll_measure)
    LinearLayoutCompat llMeasure;
    @BindView(R.id.btn_cancel)
    AppCompatImageView btnCancel;
    @BindView(R.id.btn_save)
    AppCompatImageView btnSave;
    @BindView(R.id.btn_start)
    RelativeLayout btnStart;
    @BindView(R.id.btn_point)
    RelativeLayout btnPoint;
    @BindView(R.id.btn_revoke)
    RelativeLayout btnRevoke;
    @BindView(R.id.btn_close)
    RelativeLayout btnClose;
    @BindView(R.id.tv_start)
    AppCompatTextView tvStart;

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.smart)
    SmartRefreshLayout layout;
    private LandListAdapter myAdapter;
    private final int PAGESIZE = 10;
    private String SEARCH_CONTENT = "";
    private final int REQUEST_TAG_REFRESH = 1;
    private final int REQUEST_TAG_LOAD_MORE = 2;
    private int tag = REQUEST_TAG_REFRESH;

    private int viewHeight;
    private int showHeight;
    private int measureHeight;
    private final int HIDE = 200;//弹窗收缩速度
    private final int SHOW = 200;//弹窗展开速度
    private boolean isShow;//弹窗是否打开
    private boolean isList = true;//是否是列表
    private boolean isMeasure;//是否在测量状态

    private SensorManager sensorManager;
    private Sensor aSensor;
    private Sensor mSensor;
    private boolean compassActive = false;// 是否开启罗盘
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private float[] rotaion = new float[9];
    private Float[] values = new Float[3];

    private String seasonId;
    private String seasonName;

    private List<LandDetail> landDetail = new ArrayList<>();//后台拉取的原始数据
    private String sugarcaneid;// 蔗区
    private String terrainId;//地形
    private String farmerId;//蔗农
    private int landId = -1;
    private String placeName;//地名
    private String agroType;//土壤类型
    private String geoPolygon;//上传的多边形
    private String geo_polygon;//零时多边形
    private String geoArea;//面积
    private String cropId;//农作物
    private String period;//植期
    private List<String[]> filterDateList = new ArrayList<>();
    private CharacterParser characterParser = CharacterParser.getInstance();
    private DialogPlus dialogSugar;
    private DialogPlus dialogAgrotype;
    private DialogPlus dialogTerrain;
    private DialogPlus dialogFarmer;
    private DialogPlus dialogCrop;
    private DialogPlus dialogPeriod;

    @BindColor(R.color.white)
    protected int white;
    private LocationUtils loc;
    private Location myLocation;
    private MagicBoxReceiver receiver = null; //接收 魔盒信息广播
    private String bluetoothAddress = null;  //蓝牙地址
    private boolean OpenMagicBox = false; //如果选择打开魔盒 就只取魔盒的定位信息
    private SearchFragment searchFragment;
    private LandPlantDialog dialog;

    public static void start(Context context) {
        Intent starter = new Intent(context, LandManagementActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getContentViewResId() {
        LoadingLocationDialog.show(this, () -> {
            presenter.getAllLand(SEARCH_CONTENT, 0, PAGESIZE);
            presenter.getTerrain();
            presenter.getSugarcane();
            presenter.getAgrotype();
            presenter.getCrop();
            presenter.getFarmer();
            presenter.getCurrentSeason();
        });
        return R.layout.activity_land_manager;
    }

    @Override
    protected void initView() {
        MeasureSize();
        hideView(0);

        web.addJavascriptInterface(new MapJavaScriptObject(), "android");
        web.loadUrl("file:///android_asset/website/view/measure/Measure.html");
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(com.tencent.smtt.sdk.WebView webView, String s) {
            }

            @Override
            public void onProgressChanged(com.tencent.smtt.sdk.WebView webView, int i) {
                if (i == 100) {
                    if (myLocation != null) {
                        setLocation(myLocation.getLongitude(), myLocation.getLatitude());
                    }
                    presenter.getAllLand(SEARCH_CONTENT, 0, PAGESIZE);
                }
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new LandListAdapter();
        myAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        rv.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener((BaseQuickAdapter ad, View view, int position) -> {
            LandList data = (LandList) ad.getItem(position);
            myAdapter.setSelecter(position);
            if (web != null) {
                web.loadUrl(String.format("javascript:android_call.locate_land(%s)", data.id));
            }
        });

        myAdapter.setOnItemChildClickListener((BaseQuickAdapter ad, View view, int position) -> {
            LandList data = (LandList) ad.getItem(position);
            presenter.getLandDetail(data.id);
            presenter.getPlanting(data.id);
        });

        layout.setOnRefreshListener((RefreshLayout refreshlayout) -> {
            tag = REQUEST_TAG_REFRESH;
            presenter.getAllLand(SEARCH_CONTENT, 0, PAGESIZE);
        });
        layout.setOnLoadmoreListener((RefreshLayout refreshlayout) -> {
            tag = REQUEST_TAG_LOAD_MORE;
            presenter.getAllLand(SEARCH_CONTENT, myAdapter.getData().size(), PAGESIZE);
        });
        presenter.getTerrain();
        presenter.getSugarcane();
        presenter.getAgrotype();
        presenter.getCrop();
        presenter.getFarmer();
        presenter.getCurrentSeason();
        String[] data1 = {"xzz", "sgn1", "sgn2", "sgn3"};
        String[] data2 = {"新植期", "宿根1年", "宿根2年", "宿根3年"};
        List<String[]> list = new ArrayList<>();
        for (int i = 0; i < data1.length; i++) {
            String[] a = new String[2];
            a[0] = data1[i];
            a[1] = data2[i];
            list.add(a);
        }
        initDialogPeriod(list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RC_PERM:
                initGPS();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loc != null) loc.unRegisterListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //判断魔盒是否打开
        if (GisUtils.isServiceRunning(getApplicationContext(), "cn.cassia.sugar.boqing.service" +
                ".MagicBoxService")) {
            OpenMagicBox = true;
            Logger.d("魔盒:已开启");
        } else {
            OpenMagicBox = false;
            Logger.d("魔盒:未开启");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        unregisterSensor();
        web.removeAllViews();
        web.destroy();
    }

    /**
     * 初始化GPS
     */
    private void initGPS() {
        if (Build.VERSION.SDK_INT >= 23) { //需要动态添加权限
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION}, 100);
                return;
            }
        }
        if (!GisUtils.isGpsOPen(this)) {
            GisUtils.toOpenGPS(this);
        } else {
            getLocation(this);
        }
    }

    @OnClick({R.id.iv, R.id.btn, R.id.btn_left, R.id.btn_right, R.id.rl, R.id.tv_box_statu, R.id
            .btn_search_clean, R.id.btn_search})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv:
                MineActivity.start(LandManagementActivity.this);
                break;
            case R.id.btn:
            case R.id.rl:
                if (isShow) {
                    hideView(HIDE);
                } else {
                    showView();
                }
                break;
            case R.id.tv_box_statu:
                if (receiver == null) {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(ConnectDeviceActivity.ACTION_MAGIC_BOX);
                    receiver = new MagicBoxReceiver();
                    registerReceiver(receiver, filter);
                }
                ConnectDeviceActivity.start(mContext);
                break;
            case R.id.btn_left:
                saveForm(landId);
                break;
            case R.id.btn_right:
                if (isList) {
                    if (dialogSugar != null && dialogTerrain != null && dialogAgrotype != null) {
                        openForm();
                        initBotton(false, false, false, false);
                        landId = -1;
                        TextUtils.isEmpty("");
                        if (web != null)
                            web.loadUrl(String.format("javascript:android_call.switch_land_edit" +
                                    "('%s')", "open"));
                    } else showToast("无法初始化表单，请检查网络设置！");
                } else {
                    if (ifFormChange()) {
                        new AlertDialog.Builder(mContext).setTitle("系统提示").setMessage("现处于编辑状态," +
                                "退出将不会为您保存表单内修改的内容，是否退出编辑？").setPositiveButton("是",
                                (DialogInterface dialog, int which) -> {
                                    dialog.dismiss();
                                    closeForm();
                                    if (web != null)
                                        web.loadUrl(String.format("javascript:android_call" +
                                                ".switch_land_edit('%s')", "cancel"));
                                }).setNegativeButton("否", (DialogInterface dialog, int which) -> {
                            dialog.dismiss();
                        }).show();
                    } else {
                        closeForm();
                        if (web != null)
                            web.loadUrl(String.format("javascript:android_call.switch_land_edit" +
                                    "('%s')", "cancel"));
                    }
                }
                break;
            case R.id.btn_search_clean:
                SEARCH_CONTENT = "";
                tvSearchContent.setText(SEARCH_CONTENT);
                tag = REQUEST_TAG_REFRESH;
                presenter.getAllLand(SEARCH_CONTENT, 0, PAGESIZE);
                btnSearchClean.setVisibility(View.GONE);
                break;
            case R.id.btn_search:
                openSearchDialog();
                break;
            default:
        }
    }

    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_more})
    void onCLickForm(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                if (dialogSugar != null) dialogSugar.show();
                else showToast("无法获取该信息，请检查网络设置！");
                break;
            case R.id.btn_2:
                if (dialogAgrotype != null) dialogAgrotype.show();
                else showToast("无法获取该信息，请检查网络设置！");
                break;
            case R.id.btn_3:
                if (dialogTerrain != null) dialogTerrain.show();
                else showToast("无法获取该信息，请检查网络设置！");
                break;
            case R.id.btn_4:
                if (dialogFarmer != null) dialogFarmer.show();
                else showToast("无法获取该信息，请检查网络设置！");
                break;
            case R.id.btn_more:
                if (dialog != null) dialog.show(getSupportFragmentManager(), "plant");
                else showToast("没有更多数据...");
            default:
        }
    }

    @OnClick({R.id.btn_save, R.id.btn_cancel, R.id.btn_start, R.id.btn_point, R.id.btn_revoke, R
            .id.btn_close})
    void onClickMeasure(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                new AlertDialog.Builder(mContext).setTitle("系统提示")
                        .setMessage("退出将不会保存任何取点信息，是否退出？")
                        .setPositiveButton("是", (DialogInterface dialog, int which) -> {
                            dialog.dismiss();
                            btn.setEnabled(true);
                            rl.setEnabled(true);
                            showView();
                            isMeasure = false;
                            llMeasure.setVisibility(View.GONE);
                            if (web != null)
                                web.loadUrl(String.format("javascript:android_call" +
                                        ".switch_land_measure" +
                                        "('%s')", "cancel"));
                        }).setNegativeButton("否", (DialogInterface dialog, int which) -> {
                    dialog.dismiss();
                }).show();
                break;
            case R.id.btn_save:
                btn.setEnabled(true);
                rl.setEnabled(true);
                showView();
                isMeasure = false;
                llMeasure.setVisibility(View.GONE);
                geoArea = tvArea.getText().toString().trim();
                geoPolygon = geo_polygon;
                etArea.setText(geoArea);
                if (web != null)
                    web.loadUrl(String.format("javascript:android_call.switch_land_measure('%s')" +
                            "", "close"));
                break;
            case R.id.btn_start:
                initBotton(false, false, true, false);
                if (web != null)
                    web.loadUrl(String.format("javascript:android_call.land_measure_start()"));
                tvArea.setText("0.00");
                btnSave.setVisibility(View.GONE);
                btnCancel.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_point:
                if (web != null)
                    web.evaluateJavascript("javascript:android_call.land_measure_append()",
                            (String json) -> {
                                try {
                                    JSONObject obj = new JSONObject(json);
                                    boolean success = obj.getBoolean("success");
                                    int addPoint = obj.getInt("point_count");
                                    Logger.d("点：" + addPoint + "****success==" + success);
                                    initBotton(false, addPoint > 0, true, addPoint >= 3);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                break;
            case R.id.btn_revoke:
                if (web != null)
                    web.evaluateJavascript("javascript:android_call.land_measure_undo()", (String json) -> {
                        try {
                            JSONObject obj = new JSONObject(json);
                            boolean success = obj.getBoolean("success");
                            int remainPoint = obj.getInt("point_count");
                            Logger.d("点：" + remainPoint + "****success==" + success);
                            initBotton(false, remainPoint > 0, true, remainPoint >= 3);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                break;
            case R.id.btn_close:
                if (web != null)
                    web.evaluateJavascript("javascript:android_call.land_measure_end()", (String json) -> {
                        try {
                            JSONObject obj = new JSONObject(json);
                            boolean success = obj.getBoolean("success");
                            geo_polygon = obj.getString("geo_polygon");
                            double geo_area = obj.getDouble("geo_area");
                            Logger.d("面积：" + geo_area + "****success==" + success +
                                    "****geo_polygon=" + geo_polygon);
                            geo_area = FormatUtils.format(geo_area, 3);
                            tvArea.setText("" + geo_area);
                            initBotton(true, false, false, false);
                            if (success) {
                                btnSave.setVisibility(View.VISIBLE);
                                btnCancel.setVisibility(View.GONE);
                            } else {
                                btnSave.setVisibility(View.GONE);
                                btnCancel.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                break;
        }
    }

    @OnClick({R.id.tv_plant, R.id.tv_period})
    void onCropClick(View v) {
        switch (v.getId()) {
            case R.id.tv_plant:
                if (dialogCrop != null) dialogCrop.show();
                else showToast("无法获取该信息，请检查网络设置！");
                break;
            case R.id.tv_period:
                if (dialogPeriod != null) dialogPeriod.show();
                else showToast("无法获取该信息，请检查网络设置！");
                break;
        }
    }

    //搜索功能
    private void openSearchDialog() {
        searchFragment = SearchFragment.newInstance();
        searchFragment.setOnSearchClickListener((String keyword) -> {
            SEARCH_CONTENT = keyword;
            if (!TextUtils.isEmpty(SEARCH_CONTENT)) {
                btnSearchClean.setVisibility(View.VISIBLE);
                tag = REQUEST_TAG_REFRESH;
                presenter.getAllLand(SEARCH_CONTENT, 0, PAGESIZE);
                tvSearchContent.setText(SEARCH_CONTENT);
            }
        });
        searchFragment.show(getSupportFragmentManager(), SearchFragment.TAG);
    }

    //保存地块信息
    private void saveForm(int landId) {
        placeName = etPlaceName.getText().toString().trim();
        if (!ifFormChange()) {
            showToast("因为您未填写任何内容，所以系统自动关闭表单。");
            closeForm();
            if (web != null)
                web.loadUrl(String.format("javascript:android_call.switch_land_edit('%s')",
                        "cancel"));
            return;
        }
        //landId为-1说明是新建地块，否则为修改当前地块信息
        if (landId == -1)
            presenter.createRecord(sugarcaneid, agroType, terrainId, farmerId, placeName,
                    geoPolygon, geoArea, cropId, period, seasonId);
        else {
            //是否有当前榨季作物信息
            boolean isNowSeason = TextUtils.equals(seasonId, landDetail.get(0)
                    .getLast_planting_grinding_season_id()[0]);
            //最近一次的作物信息Id
            String planting = "";
            //如果已经录有当前榨季信息，则是修改当前信息
            if (isNowSeason) {
                planting = landDetail.get(0).getPlanting()[0];
            }
            presenter.updateRecord(landId, sugarcaneid, agroType, terrainId, farmerId, placeName,
                    geoPolygon, geoArea, cropId, period, seasonId, planting);
        }
    }

    @Override
    protected LandManagerPresenter initPresenter() {
        return new LandManagerPresenter();
    }

    @Override
    protected BaseModule initModule() {
        return new MeasureModule();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getUid() {
        return SharedPreferencesUtils.getUserId(this);
    }

    @Override
    public String getPwd() {
        return SharedPreferencesUtils.getP(this);
    }

    @Override
    public void showProgress() {
        LoadingDialog.show(this);
    }

    @Override
    public void hideProgress() {
        LoadingDialog.dismiss(this);
    }

    @Override
    public void onFailure(int code, String err) {
        if (code == 1) {
            LoadingLocationDialog.reload(this);
        }
        showToast(err);
    }

    @Override
    public void addRecordSuccess(String jsonStr) {
        showToast("新增成功！");
        tag = REQUEST_TAG_REFRESH;
        presenter.getAllLand(SEARCH_CONTENT, 0, PAGESIZE);
        closeForm();
        if (web != null)
            web.loadUrl(String.format("javascript:android_call.switch_land_edit('%s',%s)",
                    "close", ""));
    }

    @Override
    public void updateRecordSuccess(String jsonStr) {
        showToast("更新成功！");
        tag = REQUEST_TAG_REFRESH;
        presenter.getAllLand(SEARCH_CONTENT, 0, PAGESIZE);
        closeForm();
        if (web != null)
            web.loadUrl(String.format("javascript:android_call.switch_land_edit('%s',%s)",
                    "close", landId));
    }

    @Override
    public void deleteRecordSuccess(String jsonStr) {
        showToast("删除成功！");
    }

    @Override
    public void initLandData(List<LandList> landList, String dataStr) {
        LoadingLocationDialog.dismiss(this);
        switch (tag) {
            case REQUEST_TAG_REFRESH:
                layout.finishRefresh(true);
                myAdapter.setNewData(landList);
                if (web != null)
                    web.loadUrl(String.format("javascript:android_call.load_land_list(%s,%s)",
                            dataStr, false));
                break;
            case REQUEST_TAG_LOAD_MORE:
                layout.finishLoadmore(true);
                myAdapter.addData(landList);
                if (web != null)
                    web.loadUrl(String.format("javascript:android_call.load_land_list(%s,%s)",
                            dataStr, true));
                break;
        }
    }

    @Override
    public void initNearLandData(final String jsonStr) {
        runOnUiThread(() -> {
            if (web != null)
                web.loadUrl(String.format("javascript:android_call.load_land_around(%s)", jsonStr));
        });
    }

    @Override
    public void initLandDetailData(List<LandDetail> list) {
        if (isList) {
            landDetail = list;
            runOnUiThread(() -> {
                if (!isShow) {
                    showView();
                }
                openForm();
                initDatatoForm(landDetail.get(0));
                if (web != null) {
                    web.loadUrl(String.format("javascript:android_call.switch_land_edit('%s',%s)" +
                            "", "open", landId));
                }
            });
        } else {
            showToast("点太快啦！");
        }
    }

    @Override
    public void initTerrainData(List<String[]> data) {
        initDialogTerrain(data);
    }

    @Override
    public void initSugarcaneData(List<String[]> data) {
        initDialogSugar(data);
    }

    @Override
    public void initAgrotype(List<String[]> data) {
        initDialogAgrotype(data);
    }

    @Override
    public void initFarmer(List<String[]> data) {
        initDialogFarmer(data);
    }

    @Override
    public void initPlanting(List<Plant> data) {
        if (data.size() > 0) dialog = LandPlantDialog.newInstance(data);
        else dialog = null;
    }

    @Override
    public void initCurrentSeason(CurrentSeason season) {
        seasonId = season.getId();
        seasonName = season.getName();
    }

    @Override
    public void initCrop(List<String[]> data) {
        initDialogCrop(data);
    }

    //为表单赋值为查询结果
    private void initDatatoForm(LandDetail landDetail) {
        btn1.setText(landDetail.getSugarcane_region_id()[1]);
        etPlaceName.setText(landDetail.getPlace_name());
        btn2.setText(landDetail.getAgrotype_id()[1]);
        btn3.setText(landDetail.getTerrain_id()[1]);
        btn4.setText(landDetail.getRecent_farmer_id()[1]);
        etArea.setText(landDetail.getGeo_area());
        tvName.setText(landDetail.getCreate_uid()[1]);
        tvTime.setText(landDetail.getCreate_date());

        //判断是否是当前榨季
        if (TextUtils.equals(seasonId, landDetail.getLast_planting_grinding_season_id()[0])) {
            tvPlant.setText(landDetail.getLast_planting_crop_id()[1]);
            tvPeriod.setText(landDetail.getLast_planting_time_name());
//            llperiod.setVisibility(TextUtils.isEmpty(landDetail.getLast_planting_time_name()) ?
//                    View.GONE : View.VISIBLE);
            llperiod.setVisibility(View.GONE);
        } else {
            llperiod.setVisibility(View.GONE);
        }

        //临时数据，用于对比
        landId = landDetail.getId();
        placeName = landDetail.getPlace_name();
        agroType = landDetail.getAgrotype_id()[0];
        geoPolygon = landDetail.getGeo_polygon();
        geoArea = landDetail.getGeo_area();
        sugarcaneid = landDetail.getSugarcane_region_id()[0];
        terrainId = landDetail.getTerrain_id()[0];
        farmerId = landDetail.getRecent_farmer_id()[0];
        cropId = landDetail.getLast_planting_crop_id()[0];
//        period = landDetail.getLast_planting_planting_time();
        banPermissions(landDetail, !TextUtils.isEmpty(landDetail.getGeo_area()));
    }

    //设置取点相关按钮的状态以及点击
    private void initBotton(boolean isArea, boolean hasPoint, boolean isStart, boolean canClose) {
        if (isStart) {
            if (hasPoint) {
                tvStart.setText("重测");
                iv1.setBackgroundResource(R.mipmap.btn_resurvey_on);
                iv2.setBackgroundResource(R.mipmap.btn_getpoint_on);
                iv3.setBackgroundResource(R.mipmap.btn_revoke_on);
                btnStart.setEnabled(true);
                btnPoint.setEnabled(true);
                btnRevoke.setEnabled(true);
                if (canClose) {
                    iv4.setBackgroundResource(R.mipmap.btn_closearea_on);
                    btnClose.setEnabled(true);
                } else {
                    iv4.setBackgroundResource(R.mipmap.btn_closearea_off);
                    btnClose.setEnabled(false);
                }
            } else {
                tvStart.setText("开始");
                iv1.setBackgroundResource(R.mipmap.btn_start_off);
                iv2.setBackgroundResource(R.mipmap.btn_getpoint_on);
                iv3.setBackgroundResource(R.mipmap.btn_revoke_off);
                iv4.setBackgroundResource(R.mipmap.btn_closearea_off);
                btnStart.setEnabled(false);
                btnPoint.setEnabled(true);
                btnRevoke.setEnabled(false);
                btnClose.setEnabled(false);
            }
        } else {
            if (isArea) {
                tvStart.setText("重测");
                iv1.setBackgroundResource(R.mipmap.btn_resurvey_on);
            } else {
                tvStart.setText("开始");
                iv1.setBackgroundResource(R.mipmap.btn_start_on);
            }
            iv2.setBackgroundResource(R.mipmap.btn_getpoint_off);
            iv3.setBackgroundResource(R.mipmap.btn_revoke_off);
            iv4.setBackgroundResource(R.mipmap.btn_closearea_off);
            btnStart.setEnabled(true);
            btnPoint.setEnabled(false);
            btnRevoke.setEnabled(false);
            btnClose.setEnabled(false);
        }
    }

    //通过定位API拿到经纬度
    private void getLocation(Context context) {
        loc = new LocationUtils(context);
        loc.addLocationListener((Location location) -> {
            if (OpenMagicBox) return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //对获取的定位信息进行筛选
                    if (loc.isBetterLocation(location, myLocation)) {
                        myLocation = location;
                        setData(location);
                    }
                }
            });
        });
    }

    StringBuffer city = null;
    @BindColor(R.color.red)
    int red;
    @BindColor(R.color.orange)
    int orange;
    @BindColor(R.color.gold)
    int gold;
    @BindColor(R.color.green)
    int green;

    private void setData(Location location) {
        if (location != null) {
            if (TextUtils.isEmpty(city)) {
                city = loc.geocoder(location);
            }
            double longitude = FormatUtils.format(location.getLongitude(), 6);
            double latitude = FormatUtils.format(location.getLatitude(), 6);
            double accuracy = FormatUtils.format(location.getAccuracy(), 1);
            tvLongitude.setText("经度：" + longitude);
            tvLongitudMeasure.setText("经度：" + longitude);
            tvLatitude.setText("纬度：" + latitude);
            tvLatitudeMeasure.setText("纬度：" + latitude);
            //<=0.3 green  0.3~1.0 gold 1.0~3.0 orange >3.0 red
            int accuracyColor;
            if (accuracy <= 0.3) {
                accuracyColor = green;
            } else if (accuracy > 0.3 && accuracy < 1) {
                accuracyColor = gold;
            } else if (accuracy >= 1 && accuracy < 3) {
                accuracyColor = orange;
            } else {
                accuracyColor = red;
            }
            SpannableStringBuilder builder = new SpanUtils(mContext).append("精度：").append
                    (accuracy + "米").setForegroundColor(accuracyColor).create();
            tvAccuracy.setText(builder);
            tvAccuracyMeasure.setText(builder);
            tvProvider.setText(OpenMagicBox ? "方式：北斗定位" : "方式：" + location.getProvider());
            tvAddress.setText(TextUtils.isEmpty(city) ? "城市：正在努力获取" : "城市：" + city);
            setLocation(location.getLongitude(), location.getLatitude());
        } else {
            showToast("获取定位失败，请联系管理员！");
        }
    }

    private void setLocation(double longitude, double latitude) {
        if (web != null)
            web.loadUrl(String.format("javascript:android_call.refresh_beidou_location(%s,%s)",
                    longitude, latitude));
    }

    /**
     * 魔盒广播接收
     */
    public class MagicBoxReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectDeviceActivity.ACTION_MAGIC_BOX)) {
                Bundle bundle = intent.getExtras();
                bluetoothAddress = bundle.getString("bluetoothAddress");
                Location wzOutLocation = (Location) bundle.get("location");
                if (wzOutLocation != null) {
                    OpenMagicBox = true;
                    //在地图上设置当前位置
                    setData(wzOutLocation);
                    tvBoxStatu.setText("魔盒：在线");
                    tvBoxStatuMeasure.setText("魔盒：在线");
                } else {
                    String str = bundle.getString("str");
                    int status = bundle.getInt("status");
                    tvBoxStatu.setText("魔盒：" + str);
                    tvBoxStatuMeasure.setText("魔盒：" + str);
                    OpenMagicBox = false;
                }
            }
        }
    }

    //打开 传感器监听
    private void registerSensor() {
        if (sensorManager == null || aSensor == null || mSensor == null) {
            //获取传感器管理器
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
        //Logger.d("打开 传感器监听");
        sensorManager.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }


    //关闭 传感器监听
    private void unregisterSensor() {
        Logger.d("关闭 传感器监听");
        if (sensorManager != null) {
            sensorManager.unregisterListener(myListener);
            sensorManager = null;
        }
        if (myListener != null) {
            myListener = null;
        }
    }

    private SensorEventListener myListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values;
            }
            //调用getRotaionMatrix获得变换矩阵rotaion[]
            SensorManager.getRotationMatrix(rotaion, null, accelerometerValues,
                    magneticFieldValues);

            float[] newValues = new float[3];
            SensorManager.getOrientation(rotaion, newValues);//得到的newValues值为弧度
            newValues[0] = (float) Math.toRadians((int) Math.toDegrees(newValues[0]));//角度取整

            //角度改变大于1°时才旋转地图
            if (values[0] == null ||
                    //四舍五入后进行整数判断
                    new BigDecimal(Math.abs(Math.toDegrees(newValues[0]) - Math.toDegrees
                            (values[0]))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() >= 5) {

                values[0] = newValues[0];
                values[1] = newValues[1];
                values[2] = newValues[2];
                if (compassActive && web != null) {
                    runOnUiThread(() -> {
                        //旋转地图
                        web.loadUrl(String.format("javascript:android_call.rotate(%s)", values[0]
                                .floatValue()));
                    });
                }
            }
        }
    };

    //测量弹窗高度
    private void MeasureSize() {
        RelativeLayout.LayoutParams paramLL = (RelativeLayout.LayoutParams) ll.getLayoutParams();
        viewHeight = paramLL.height;
        LinearLayoutCompat.LayoutParams parambtn = (LinearLayoutCompat.LayoutParams) btn
                .getLayoutParams();
        LinearLayoutCompat.LayoutParams paramrl = (LinearLayoutCompat.LayoutParams) rl
                .getLayoutParams();
        showHeight = parambtn.height + paramrl.height;
        RelativeLayout.LayoutParams paramLM = (RelativeLayout.LayoutParams) llMeasure
                .getLayoutParams();
        measureHeight = paramLM.height + parambtn.height;
    }

    //动态改变webview高度
    private void changeViewHeight() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) web.getLayoutParams();
        int maxHeight = ScreenUtils.getScreenHeight(this);
        //Logger.d("maxHeight==" + maxHeight);
        if (isShow) {
            params.height = maxHeight - viewHeight;
        } else {
            if (isMeasure) params.height = maxHeight - measureHeight;
            else params.height = maxHeight - showHeight;
        }
        web.setLayoutParams(params);
    }

    private void hideView(int duration) {
        isShow = false;
        changeViewHeight();
        ValueAnimator animator = ValueAnimator.ofFloat(0, viewHeight - showHeight);
        animator.setTarget(ll);
        animator.setDuration(duration);
        animator.addUpdateListener((ValueAnimator animation) -> {
            ll.setTranslationY((Float) animation.getAnimatedValue());
        });
        animator.start();
    }

    private void showView() {
        isShow = true;
        ValueAnimator animator = ValueAnimator.ofFloat(viewHeight - showHeight, 0);
        animator.setTarget(ll);
        animator.setDuration(SHOW);
        animator.addUpdateListener((ValueAnimator animation) -> {
            ll.setTranslationY((Float) animation.getAnimatedValue());
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                changeViewHeight();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void initDialogSugar(List<String[]> list) {
        DialogAdapter adapter = new DialogAdapter(this);
        adapter.setDatatoAdapter(list);
        dialogSugar = DialogPlus.newDialog(this).setAdapter(adapter).setContentHolder(new
                ListHolder()).setGravity(Gravity.CENTER).setCancelable(true).setHeader(R.layout
                .dialog_plus_header).setOnItemClickListener((dialog, item, view, position) -> {
            String[] data = adapter.getItem(position);
            sugarcaneid = data[0];
            btn1.setText(data[1]);
            dialogSugar.dismiss();
        }).setExpanded(list.size() >= OPEN_SIZE ? true : false).create();
        View head = dialogSugar.getHeaderView();
        AppCompatTextView tv = head.findViewById(R.id.tv_title);
        AppCompatEditText et = head.findViewById(R.id.et);
        AppCompatTextView btn = head.findViewById(R.id.btn);
        btn.setVisibility(View.VISIBLE);
        tv.setText("请选择烟区");
        btn.setOnClickListener(v -> {
            String filterStr = et.getText().toString().trim();
            filterDateList = new ArrayList<>();
            if (TextUtils.isEmpty(filterStr)) {
                filterDateList = list;
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String character = characterParser.getSelling(list.get(i)[1]);
                    if (list.get(i)[1].contains(filterStr) || character.contains(filterStr)) {
                        filterDateList.add(list.get(i));
                    }
                }
            }
            adapter.setDatatoAdapter(filterDateList);
        });
    }

    private void initDialogAgrotype(List<String[]> list) {
        DialogAdapter adapter = new DialogAdapter(this);
        adapter.setDatatoAdapter(list);
        dialogAgrotype = DialogPlus.newDialog(this).setAdapter(adapter).setContentHolder(new ListHolder())
                .setGravity(Gravity.CENTER).setCancelable(true).setHeader(R.layout.dialog_plus_header)
                .setOnItemClickListener((dialog, item, view, position) -> {
                    String[] data = adapter.getItem(position);
                    agroType = data[0];
                    btn2.setText(data[1]);
                    dialogAgrotype.dismiss();
                }).setExpanded(list.size() >= OPEN_SIZE ? true : false).create();
        View head = dialogAgrotype.getHeaderView();
        AppCompatTextView tv = head.findViewById(R.id.tv_title);
        AppCompatEditText et = head.findViewById(R.id.et);
        AppCompatTextView btn = head.findViewById(R.id.btn);
        btn.setVisibility(View.GONE);
        tv.setText("请选择土壤类型");
        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filterStr = s.toString();
                filterDateList = new ArrayList<>();
                if (TextUtils.isEmpty(filterStr)) {
                    filterDateList = list;
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        String character = characterParser.getSelling(list.get(i)[1]);
                        if (list.get(i)[1].contains(filterStr) || character.contains(filterStr)) {
                            filterDateList.add(list.get(i));
                        }
                    }
                }
                adapter.setDatatoAdapter(filterDateList);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initDialogTerrain(List<String[]> list) {
        DialogAdapter adapter = new DialogAdapter(this);
        adapter.setDatatoAdapter(list);
        dialogTerrain = DialogPlus.newDialog(this).setAdapter(adapter).setContentHolder(new ListHolder())
                .setGravity(Gravity.CENTER).setCancelable(true).setHeader(R.layout.dialog_plus_header)
                .setOnItemClickListener((dialog, item, view, position) -> {
                    String[] data = adapter.getItem(position);
                    terrainId = data[0];
                    btn3.setText(data[1]);
                    dialogTerrain.dismiss();
                }).setExpanded(list.size() >= OPEN_SIZE ? true : false).create();
        View head = dialogTerrain.getHeaderView();
        AppCompatTextView tv = head.findViewById(R.id.tv_title);
        AppCompatEditText et = head.findViewById(R.id.et);
        AppCompatTextView btn = head.findViewById(R.id.btn);
        btn.setVisibility(View.GONE);
        tv.setText("请选择地形");
        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filterStr = s.toString();
                filterDateList = new ArrayList<>();
                if (TextUtils.isEmpty(filterStr)) {
                    filterDateList = list;
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        String character = characterParser.getSelling(list.get(i)[1]);
                        if (list.get(i)[1].contains(filterStr) || character.contains(filterStr)) {
                            filterDateList.add(list.get(i));
                        }
                    }
                }
                adapter.setDatatoAdapter(filterDateList);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initDialogFarmer(List<String[]> list) {
        DialogAdapter adapter = new DialogAdapter(this);
        adapter.setDatatoAdapter(list);
        dialogFarmer = DialogPlus.newDialog(this).setAdapter(adapter).setContentHolder(new
                ListHolder()).setGravity(Gravity.CENTER).setCancelable(true).setHeader(R.layout
                .dialog_plus_header).setOnItemClickListener((dialog, item, view, position) -> {
            String[] data = adapter.getItem(position);
            btn4.setText(data[1]);
            farmerId = data[0];
            dialogFarmer.dismiss();
        }).setExpanded(list.size() >= OPEN_SIZE ? true : false).create();
        View head = dialogFarmer.getHeaderView();
        AppCompatTextView tv = head.findViewById(R.id.tv_title);
        AppCompatEditText et = head.findViewById(R.id.et);
        AppCompatTextView btn = head.findViewById(R.id.btn);
        btn.setVisibility(View.VISIBLE);
        tv.setText("请选择烟农");
        btn.setOnClickListener(v -> {
            String filterStr = et.getText().toString().trim();
            filterDateList = new ArrayList<>();
            if (TextUtils.isEmpty(filterStr)) {
                filterDateList = list;
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String character = characterParser.getSelling(list.get(i)[1]);
                    if (list.get(i)[1].contains(filterStr) || character.contains(filterStr)) {
                        filterDateList.add(list.get(i));
                    }
                }
            }
            adapter.setDatatoAdapter(filterDateList);
        });
    }

    private void initDialogCrop(List<String[]> list) {
        DialogAdapter adapter = new DialogAdapter(this);
        adapter.setDatatoAdapter(list);
        dialogCrop = DialogPlus.newDialog(this).setAdapter(adapter).setContentHolder(new
                ListHolder()).setGravity(Gravity.CENTER).setCancelable(true).setHeader(R.layout
                .dialog_plus_header).setOnItemClickListener((dialog, item, view, position) -> {
            String[] data = adapter.getItem(position);
            cropId = data[0];
            tvPlant.setText(data[1]);
            dialogCrop.dismiss();
            if (TextUtils.equals(data[2], "true")) {
                llperiod.setVisibility(View.GONE);
                period = "xzz";
                tvPeriod.setText("新植期");
            } else {
                period = "";
                llperiod.setVisibility(View.GONE);
            }
        }).setExpanded(list.size() >= OPEN_SIZE ? true : false).create();
        View head = dialogCrop.getHeaderView();
        AppCompatTextView tv = head.findViewById(R.id.tv_title);
        AppCompatEditText et = head.findViewById(R.id.et);
        AppCompatTextView btn = head.findViewById(R.id.btn);
        btn.setVisibility(View.GONE);
        tv.setText("请选择农作物");
        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filterStr = s.toString();
                filterDateList = new ArrayList<>();
                if (TextUtils.isEmpty(filterStr)) {
                    filterDateList = list;
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        String character = characterParser.getSelling(list.get(i)[1]);
                        if (list.get(i)[1].contains(filterStr) || character.contains(filterStr)) {
                            filterDateList.add(list.get(i));
                        }
                    }
                }
                adapter.setDatatoAdapter(filterDateList);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initDialogPeriod(List<String[]> list) {
        DialogAdapter adapter = new DialogAdapter(this);
        adapter.setDatatoAdapter(list);
        dialogPeriod = DialogPlus.newDialog(this).setAdapter(adapter).setContentHolder(new
                ListHolder()).setGravity(Gravity.CENTER).setCancelable(true).setHeader(R.layout
                .dialog_plus_header).setOnItemClickListener((dialog, item, view, position) -> {
            String[] data = adapter.getItem(position);
            period = data[0];
            tvPeriod.setText(data[1]);
            dialogPeriod.dismiss();
        }).setExpanded(list.size() >= OPEN_SIZE ? true : false).create();
        View head = dialogPeriod.getHeaderView();
        AppCompatTextView tv = head.findViewById(R.id.tv_title);
        AppCompatEditText et = head.findViewById(R.id.et);
        AppCompatTextView btn = head.findViewById(R.id.btn);
        btn.setVisibility(View.GONE);
        tv.setText("请选择植期");
        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filterStr = s.toString();
                filterDateList = new ArrayList<>();
                if (TextUtils.isEmpty(filterStr)) {
                    filterDateList = list;
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        String character = characterParser.getSelling(list.get(i)[1]);
                        if (list.get(i)[1].contains(filterStr) || character.contains(filterStr)) {
                            filterDateList.add(list.get(i));
                        }
                    }
                }
                adapter.setDatatoAdapter(filterDateList);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    //编辑表单
    private void openForm() {
        rlForm.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        btnRight.setImageResource(R.mipmap.btn_close);
        btnLeft.setVisibility(View.VISIBLE);
        tvContent.setVisibility(View.VISIBLE);
        btnSearch.setVisibility(View.GONE);
        llperiod.setVisibility(View.GONE);
        tvSeason.setText(seasonName + "年榨季");
        isList = false;
    }

    //关闭表单,并重置所有表单内容
    private void closeForm() {
        layout.setVisibility(View.VISIBLE);
        rlForm.setVisibility(View.GONE);
        btnRight.setImageResource(R.mipmap.btn_add);
        btnLeft.setVisibility(View.GONE);
        tvContent.setVisibility(View.GONE);
        btnSearch.setVisibility(View.VISIBLE);
        isList = true;

        tvContent.setText("地块列表");
        etPlaceName.setText("");
        btn1.setHint("请选择烟区");
        btn1.setText("");
        btn2.setHint("请选择土壤类型");
        btn2.setText("");
        btn3.setHint("请选择地形");
        btn3.setText("");
        etArea.setText("");
        tvName.setText("");
        tvTime.setText("");
        tvPlant.setHint("请选择农作物");
        tvPlant.setText("");
        tvPeriod.setHint("请选择植期");
        tvPeriod.setText("");

        landId = -1;
        placeName = "";
        agroType = "";
        geoPolygon = "";
        geoArea = "";
        sugarcaneid = "";
        terrainId = "";
        farmerId = "";
        cropId = "";
        period = "";
    }

    //判断用户有没有修改表单的信息
    private boolean ifFormChange() {
        LandDetail data = new LandDetail();
        if (landDetail.size() != 0) data = landDetail.get(0);
        //如果所有信息都为空，说明没有填写任何内容，则直接返回
        if (TextUtils.isEmpty(sugarcaneid) && TextUtils.isEmpty(terrainId) && TextUtils.isEmpty
                (farmerId) && TextUtils.isEmpty(agroType) && TextUtils.isEmpty(placeName) &&
                TextUtils.isEmpty(geoPolygon) && TextUtils.isEmpty(geoArea) && TextUtils.isEmpty
                (period) && TextUtils.isEmpty(cropId))
            return false;
        //校验每一个提交的参数是否修改，无变动，则直接返回
        if (TextUtils.equals(sugarcaneid, data.getSugarcane_region_id()[0])
                && TextUtils.equals(farmerId, data.getRecent_farmer_id()[0])
                && TextUtils.equals(terrainId, data.getTerrain_id()[0])
                && TextUtils.equals(agroType, data.getAgrotype_id()[0])
                && TextUtils.equals(placeName, data.getPlace_name())
//                && TextUtils.equals(period, data.getLast_planting_planting_time())
                && TextUtils.equals(cropId, data.getLast_planting_crop_id()[0])
                && TextUtils.equals(geoPolygon, data.getGeo_polygon()))
            return false;
        else return true;

    }

    public class MapJavaScriptObject {
        /**
         * 罗盘开关
         *
         * @param active 是否打开罗盘
         */
        @JavascriptInterface
        public void switch_compass(boolean active) {
            compassActive = active;//true的时候开启罗盘
            if (compassActive) {
                //开启罗盘传感器监听
                registerSensor();
            } else {
                //关闭罗盘传感器监听
                unregisterSensor();
            }
        }

        /**
         * 点击地图地块，打开表单
         *
         * @param id 地块id
         */
        @JavascriptInterface
        public void getForm(int id) {
            presenter.getLandDetail(id);
            presenter.getPlanting(id);
        }

        /**
         * 获取附近地块
         *
         * @param minX
         * @param minY
         * @param maxX
         * @param maxY
         */
        @JavascriptInterface
        public void loadNearbyLand(double minX, double minY, double maxX, double maxY) {
            presenter.getNearbyLand(minX, minY, maxX, maxY);
        }

        /**
         * 开始测量
         *
         * @param isArea 是否测量过
         * @param area   面积
         */
        @JavascriptInterface
        public void startMeasure(boolean isArea, float area) {
            Logger.d("是否测量过：" + isArea + "******面积为：" + area);
            runOnUiThread(() -> {
                isMeasure = true;
                btn.setEnabled(false);
                rl.setEnabled(false);
                hideView(0);
                llMeasure.setVisibility(View.VISIBLE);
                if (isArea) tvArea.setText("" + area);
                else tvArea.setText("0.00");
            });
        }

        /**
         * JS调用系统提示
         *
         * @param msg 消息
         */
        @JavascriptInterface
        public void alert(String msg) {
            showToast(msg);
        }

        // TODO: 2018-09-12.0012 如果前缀为https时，应修改此处
        @JavascriptInterface
        public String getIp() {
            String ip = UrlUtil.getInstance().getApiUrl().substring(7).split(":")[0];
            return ip;
        }

        @JavascriptInterface
        public String getPort() {
            String port = UrlUtil.getInstance().getApiUrl().substring(7).split(":")[1];
            return port;
        }

        @JavascriptInterface
        public String getCode() {
            String code = SharedPreferencesUtils.getCode(getApplicationContext());
            return code;
        }

        @JavascriptInterface
        public String getLocation() {
            double latitude = 0;
            double longitude = 0;
            if (myLocation != null) {
                latitude = myLocation.getLatitude();
                longitude = myLocation.getLongitude();
            }
            HashMap map = new HashMap();
            map.put("latitude", latitude);
            map.put("longitude", longitude);
            return GsonUtil.getInstance().getGson().toJson(map);
        }

    }

    private void banPermissions(LandDetail landDetail, boolean isArea) {
        switch (landDetail.getState()) {
            case "confirm":
            case "accept":
                btnLeft.setVisibility(View.VISIBLE);
                btn1.setEnabled(true);
                btn2.setEnabled(true);
                btn3.setEnabled(true);
                etPlaceName.setEnabled(true);
                tvPlant.setEnabled(true);
                tvPeriod.setEnabled(true);
                etRemarks.setEnabled(true);
                tvStart.setText("开始");
                iv1.setBackgroundResource(R.mipmap.btn_start_off);
                iv2.setBackgroundResource(R.mipmap.btn_getpoint_off);
                iv3.setBackgroundResource(R.mipmap.btn_revoke_off);
                iv4.setBackgroundResource(R.mipmap.btn_closearea_off);
                btnStart.setEnabled(false);
                btnPoint.setEnabled(false);
                btnRevoke.setEnabled(false);
                btnClose.setEnabled(false);
                showToast("处于审核或者待审核状态的地块，无法测量。");
                break;
            case "history":
                btnLeft.setVisibility(View.GONE);
                btn1.setEnabled(false);
                btn2.setEnabled(false);
                btn3.setEnabled(false);
                etPlaceName.setEnabled(false);
                tvPlant.setEnabled(false);
                tvPeriod.setEnabled(false);
                etRemarks.setEnabled(false);
                iv1.setBackgroundResource(R.mipmap.btn_start_off);
                iv2.setBackgroundResource(R.mipmap.btn_getpoint_off);
                iv3.setBackgroundResource(R.mipmap.btn_revoke_off);
                iv4.setBackgroundResource(R.mipmap.btn_closearea_off);
                btnStart.setEnabled(false);
                btnPoint.setEnabled(false);
                btnRevoke.setEnabled(false);
                btnClose.setEnabled(false);
                showToast("该地块属于历史信息，无法测量。");
                break;
            default:
                btnLeft.setVisibility(View.VISIBLE);
                btn1.setEnabled(true);
                btn2.setEnabled(true);
                btn3.setEnabled(true);
                etPlaceName.setEnabled(true);
                tvPlant.setEnabled(true);
                tvPeriod.setEnabled(true);
                etRemarks.setEnabled(true);
                initBotton(isArea, false, false, false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (isList) {
            close();
        } else if (isMeasure) {
            new AlertDialog.Builder(mContext).setTitle("系统提示")
                    .setMessage("退出将不会保存任何取点信息，是否退出？")
                    .setPositiveButton("是", (DialogInterface dialog, int which) -> {
                        dialog.dismiss();
                        btn.setEnabled(true);
                        rl.setEnabled(true);
                        showView();
                        isMeasure = false;
                        llMeasure.setVisibility(View.GONE);
                        if (web != null)
                            web.loadUrl(String.format("javascript:android_call" + "" +
                                    ".switch_land_measure" + "('%s')", "cancel"));
                    }).setNegativeButton("否", (DialogInterface dialog, int which) -> {
                dialog.dismiss();
            }).show();
        } else {
            new AlertDialog.Builder(mContext).setTitle("系统提示").setMessage("现在处于编辑状态," +
                    "退出将不会为您保存表单内修改的内容，是否退出编辑？")
                    .setPositiveButton("是", (DialogInterface dialog, int which) -> {
                        closeForm();
                        dialog.dismiss();
                    }).setNegativeButton("否", (DialogInterface dialog, int which) -> {
                dialog.dismiss();
            }).show();
        }
    }

    // 两次退出
    private boolean isFirstExit = true;

    private void close() {
        if (isFirstExit) {
            Toast.makeText(this, "再按一次退出!", Toast.LENGTH_SHORT).show();
            isFirstExit = false;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        isFirstExit = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            ActivityUtils.getInstance().exit();
        }
    }
}