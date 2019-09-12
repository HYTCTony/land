package cn.cassia.sugar.land.ui.set;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import cn.cassia.sugar.land.AppContext;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseViewActivity;
import cn.cassia.sugar.land.http.UrlUtil;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;

/**
 * Created by qingjie on 2018-07-25.0025.
 */
public class SettingActivity extends BaseViewActivity {

    @BindView(R.id.et_set_ip)
    AppCompatEditText et1;
    @BindView(R.id.et_set_db)
    AppCompatEditText et2;

    public static void start(Context context) {
        Intent starter = new Intent(context, SettingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle("设置IP");
        super.initToolbar(toolbar);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        et1.setText(UrlUtil.getInstance().getApiUrl());
        et2.setText(UrlUtil.getInstance().getDBName());
    }

    @OnClick(R.id.btn)
    void onClick() {
        String ip = et1.getText().toString().trim();
        String db = et2.getText().toString().trim();
        if (TextUtils.isEmpty(ip)) {
            Toast.makeText(this, "请输入IP!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(db)) {
            Toast.makeText(this, "请输入dbName!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "修改成功!", Toast.LENGTH_SHORT).show();
        SharedPreferencesUtils.setIP(this, ip);
        SharedPreferencesUtils.setDB(this, db);
        UrlUtil.getInstance().setApiUrl(ip);
        UrlUtil.getInstance().setDBName(db);
    }
}
