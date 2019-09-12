package cn.cassia.sugar.land.ui.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zhouwei.library.CustomPopWindow;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.cassia.sugar.land.AppContext;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.dialog.LoadingDialog;
import cn.cassia.sugar.land.http.UrlUtil;
import cn.cassia.sugar.land.mvp.BaseModule;
import cn.cassia.sugar.land.mvp.BaseMvpActivity;
import cn.cassia.sugar.land.ui.measure.LandManagementActivity;
import cn.cassia.sugar.land.ui.measure.adapter.PopListAdapter;
import cn.cassia.sugar.land.ui.set.SettingActivity;
import cn.cassia.sugar.land.ui.test.DialogTestActivity;
import cn.cassia.sugar.land.ui.test.TestActivity;
import cn.cassia.sugar.land.utils.GsonUtil;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;

/**
 * Created by qingjie on 2018-05-24.0024.
 */
public class LoginActivity extends BaseMvpActivity<LoginContract.ILoginView, LoginPresenter> implements LoginContract.ILoginView {
    @BindView(R.id.et_username)
    AppCompatEditText etUserName;
    @BindView(R.id.et_password)
    AppCompatEditText etPassword;
    @BindView(R.id.btn_login)
    AppCompatTextView btn;
    @BindView(R.id.iv_logo)
    AppCompatImageView logo;

    CustomPopWindow dbNamePop;
    @BindView(R.id.tv_db_name)
    AppCompatTextView tvDbName;

    private String DB_NAME_TOAST = "由于网络等原因，无法获取数据库列表！";

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        etUserName.setText(SharedPreferencesUtils.getUserName(this));
        tvDbName.setText(UrlUtil.getInstance().getDBName());
        HttpURLConnectionPost();
    }

    @Override
    protected LoginPresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected BaseModule initModule() {
        return new LoginModule();
    }

    @Override
    public int getUid() {
        return 0;
    }

    @Override
    public String getPwd() {
        return null;
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
        showToast(err);
    }

    @Override
    public void showToast(String msg) {
        Snackbar.make(btn, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void navigateToHome(String username, int uid, String pwd, String name, String code_json) {
        SharedPreferencesUtils.setUserName(getApplicationContext(), username);
        SharedPreferencesUtils.saveUserId(getApplicationContext(), uid);
        SharedPreferencesUtils.setP(getApplicationContext(), pwd);
        SharedPreferencesUtils.setName(getApplicationContext(), name);
        SharedPreferencesUtils.setCode(getApplicationContext(), code_json);
        LandManagementActivity.start(this);
        AppContext.setLoginstate(true);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @OnClick(R.id.btn_login)
    void onLoginClick() {
        String un = etUserName.getText().toString().trim();
        String pwd = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(un)) {
            Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        presenter.validateCredentials(un, pwd);
    }

    private void HttpURLConnectionPost() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                StringBuilder sb = new StringBuilder();
                try {
                    HashMap map = new HashMap();
                    map.put("id", "1");
                    map.put("jsonrpc", "2.0");
                    map.put("Method", "call");
                    String param = GsonUtil.getInstance().getGson().toJson(map);

                    String path = UrlUtil.getInstance().getApiUrl() + "/web/database/list";
                    URL url = new URL(path);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.connect();
                    DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                    dos.writeBytes(param);
                    dos.flush();
                    dos.close();
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
                        String msg = "code：" + connection.getResponseCode() + "，数据错误，请联系管理员！";
                        showToast(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("无法连接地址，请稍后重试！");
                    DB_NAME_TOAST = "无法连接地址，请稍后重试！";
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
                JSONArray array = obj.getJSONArray("result");
                if (array.length() == 1) {
                    String dbName = array.get(0).toString();
                    UrlUtil.getInstance().setDBName(dbName);
                    tvDbName.setText(dbName);
                    tvDbName.setVisibility(View.GONE);
                    SharedPreferencesUtils.setDB(LoginActivity.this, dbName);
                    DB_NAME_TOAST = "这已经是唯一的数据库了！";
                } else if (array.length() < 1) {
                    tvDbName.setVisibility(View.GONE);
                    showToast("检测到您还没有创建数据库，请创建数据库之后，重试！");
                    DB_NAME_TOAST = "检测到您还没有创建数据库，请创建数据库之后，重试！";
                } else {
                    tvDbName.setVisibility(View.VISIBLE);
                    List<String[]> data = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        String[] child = new String[2];
                        child[0] = "";
                        child[1] = array.get(i).toString();
                        data.add(child);
                    }
                    String dbName = array.get(0).toString();
                    UrlUtil.getInstance().setDBName(dbName);
                    tvDbName.setText(dbName);
                    SharedPreferencesUtils.setDB(LoginActivity.this, dbName);
                    initPopSugar(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                DB_NAME_TOAST = "服务器异常，无法获取数据！";
            }
        }
    };

    @OnClick(R.id.tv_db_name)
    void onDbClick() {
        if (dbNamePop != null) dbNamePop.showAsDropDown(tvDbName, 0, 10);//显示PopupWindow
        else {
            showToast(DB_NAME_TOAST);
        }
    }

    private void initPopSugar(List<String[]> list) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_layout, null);
        dbNamePop = new CustomPopWindow.PopupWindowBuilder(this).setView(contentView)//显示的布局
                .setAnimationStyle(R.style.CustomPopWindowStyle).create();//创建PopupWindow
        RecyclerView recyclerView = contentView.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PopListAdapter adapter = new PopListAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setNewData(list);
        adapter.setOnItemClickListener((BaseQuickAdapter ad, View view, int position) -> {
            String[] data = (String[]) ad.getItem(position);
            tvDbName.setText(data[1]);
            UrlUtil.getInstance().setDBName(data[1]);
            SharedPreferencesUtils.setDB(LoginActivity.this, data[1]);
            dbNamePop.dissmiss();
        });
    }

    @OnClick(R.id.iv_logo)
    void onLogoClick() {
        exit();
    }

    private boolean isFirstExit = true;

    private void exit() {
        if (isFirstExit) {
            isFirstExit = false;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        isFirstExit = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            SettingActivity.start(LoginActivity.this);
        }
    }
}