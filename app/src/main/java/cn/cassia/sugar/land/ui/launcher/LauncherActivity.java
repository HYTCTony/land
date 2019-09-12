package cn.cassia.sugar.land.ui.launcher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.orhanobut.logger.Logger;
import com.tencent.smtt.sdk.WebChromeClient;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import cn.cassia.sugar.land.AppContext;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseActivity;
import cn.cassia.sugar.land.dialog.UpdateDialog;
import cn.cassia.sugar.land.http.AppClient;
import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.http.UrlUtil;
import cn.cassia.sugar.land.ui.login.LoginActivity;
import cn.cassia.sugar.land.ui.measure.LandManagementActivity;
import cn.cassia.sugar.land.utils.GisUtils;
import cn.cassia.sugar.land.utils.GsonUtil;
import cn.cassia.sugar.land.utils.HttpDownloadListener;
import cn.cassia.sugar.land.utils.PackageUtil;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;
import cn.cassia.sugar.land.web.WebView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static java.util.Arrays.asList;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class LauncherActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.tv)
    AppCompatTextView tv;
    private long mWait = 500;
    private static final int RC_PERM = 100;
    private static final String SP_NAME = "launcher";
    private static final String SP_TAG = "first_1_2_0_0";
    private Timer mTimer;
    private TimerTask mTimerTask;
    private WebView webView;
    private boolean isForcedUpdate;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_wellcome;
    }

    private void startActivity() {
        if (AppContext.isLoginstate()) {
            //startActivity(new Intent(mContext, MainActivity.class));
            LandManagementActivity.start(mContext);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    protected void initView() {
        tv.setText("Ver " + PackageUtil.getVersionName(this));
        int uId = SharedPreferencesUtils.getUserId(this);
        String pwd = SharedPreferencesUtils.getP(this);
        String ip = SharedPreferencesUtils.getIP(this);
        String db = SharedPreferencesUtils.getDB(this);
        UrlUtil.getInstance().setApiUrl(ip);
        UrlUtil.getInstance().setDBName(db);
        if (uId == -1 || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(db)) {
            SharedPreferencesUtils.clearUser(this);
            HttpURLConnectionGet();
        } else {
            AppContext.setLoginstate(true);
            getCode(uId, pwd);
        }
        webView = new WebView(this);
        webView.setDownloadListener(new HttpDownloadListener(this, new Handler()));
    }

    private void HttpURLConnectionGet() {
        new Thread(new Runnable() {
            public void run() {
                HttpURLConnection connection = null;
                StringBuilder sb = new StringBuilder();
                try {
                    String path = UrlUtil.getInstance().getApiUrl() + "/app/version/android?version=" + PackageUtil.getVersionCode(LauncherActivity.this);
                    URL url = new URL(path);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.connect();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        //得到输入流
                        InputStream is = connection.getInputStream();
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = is.read(buffer)) != -1) {
                            sb.append(new String(buffer, 0, len, "utf-8"));
                        }
                        is.close();
                        Logger.json(sb.toString());
                        Message message = handler.obtainMessage(1, sb.toString());
                        handler.sendMessage(message);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermission();
                        } else {
                            startActivity();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermission();
                    } else {
                        startActivity();
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String s = (String) msg.obj;
            JSONObject obj = null;
            try {
                obj = new JSONObject(s);
                if (obj.has("url")) {
                    String downloadurl = obj.getString("url");
                    String versionName = obj.getString("version_name");
                    String upDateDetail = obj.getString("detail");
                    int currentVersion = PackageUtil.getVersionCode(LauncherActivity.this);
                    int minVersion = obj.getInt("min_version_code");
                    isForcedUpdate = currentVersion < minVersion;
                    UpdateDialog dialog = UpdateDialog.newInstance(versionName, upDateDetail, isForcedUpdate, new UpdateDialog.onClickListener() {
                        @Override
                        public void onConfirm(Dialog dialog) {
                            webView.loadUrl(UrlUtil.getInstance().getApiUrl() + downloadurl);
                        }

                        @Override
                        public void onCancel(Dialog dialog) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission();
                            } else {
                                startActivity();
                            }
                        }
                    });
                    dialog.show(getSupportFragmentManager(), "update");
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermission();
                    } else {
                        startActivity();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void getCode(int uid, String pwd) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setServerURL(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(), uid, pwd, "kxy.mapserver.instance", "search_read", asList(asList(asList("active", "=", true))), new HashMap() {{
            put("fields", asList("code"));
        }}), new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                String json = "";
                try {
                    JSONArray arr = new JSONArray(content);
                    String[] array = new String[arr.length()];
                    for (int i = 0; i < arr.length(); i++) {
                        array[i] = arr.getJSONObject(i).getString("code");
                    }
                    json = GsonUtil.getInstance().getGson().toJson(array);
                } catch (JSONException e) {
                    e.printStackTrace();
                    String[] array = new String[0];
                    json = GsonUtil.getInstance().getGson().toJson(array);
                }
                SharedPreferencesUtils.setCode(getApplicationContext(), json);
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                HttpURLConnectionGet();
            }
        });
    }

    @AfterPermissionGranted(RC_PERM)
    private void requestPermission() {
        List<String> list = new ArrayList<>();
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {
            list.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            list.add(Manifest.permission.CAMERA);
        }
        int size = list.size();
        if (size > 0) {
            String[] perms = list.toArray(new String[size]);
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, RC_PERM, perms).setRationale("为了给您带来更好的体验，本智慧平台需要使用您的一些手机权限，点击确定授予权限。").setNegativeButtonText("取消").setPositiveButtonText("确定").build());
        } else {
            startActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            requestPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == RC_PERM && !EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            startActivity();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == RC_PERM && EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).setTitle("权限通知").setRationale("您当前限制了一些权限，为了给您带来更好的体验，本智慧平台需要使用您的这些手机权限，点击确定去授权。").setNegativeButton("取消").setPositiveButton("确定").build().show();
        } else {
            String[] permsStr = perms.toArray(new String[perms.size()]);
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, RC_PERM, permsStr).setRationale("了给您带来更好的体验，本智慧平台需要使用您的一些手机权限，点击确定授予权限。").setNegativeButtonText("取消").setPositiveButtonText("确定").build());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
