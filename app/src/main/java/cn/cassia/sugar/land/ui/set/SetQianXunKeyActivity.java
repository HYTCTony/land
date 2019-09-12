package cn.cassia.sugar.land.ui.set;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.base.BaseViewActivity;
import cn.cassia.sugar.land.http.UrlUtil;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;

/**
 * Created by qingjie on 2019-09-06.0006.
 */
public class SetQianXunKeyActivity extends BaseViewActivity {

    @BindView(R.id.et_set_key)
    AppCompatEditText et1;
    @BindView(R.id.et_set_secret)
    AppCompatEditText et2;

    public static void start(Context context) {
        Intent starter = new Intent(context, SetQianXunKeyActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle("魔盒配置");
        super.initToolbar(toolbar);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_set_key;
    }

    @Override
    protected void initView() {

    }

    @OnClick(R.id.btn)
    void onClick() {
        String appkey = et1.getText().toString().trim();
        String appSecret = et2.getText().toString().trim();
        if (TextUtils.isEmpty(appkey)) {
            Toast.makeText(this, "请输入App Key!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(appSecret)) {
            Toast.makeText(this, "请输入App Secret!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "修改成功!", Toast.LENGTH_SHORT).show();
        SharedPreferencesUtils.setAppKey(this, appkey);
        SharedPreferencesUtils.setAppSecret(this, appSecret);
    }
}
