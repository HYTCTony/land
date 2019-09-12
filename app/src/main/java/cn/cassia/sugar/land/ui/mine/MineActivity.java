package cn.cassia.sugar.land.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.OnClick;
import cn.cassia.sugar.land.AppContext;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.mvp.BaseModule;
import cn.cassia.sugar.land.mvp.BaseMvpViewActivity;
import cn.cassia.sugar.land.ui.login.LoginActivity;
import cn.cassia.sugar.land.ui.measure.LandManagementActivity;
import cn.cassia.sugar.land.ui.mine.password.PasswordActivity;
import cn.cassia.sugar.land.utils.ActivityUtils;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;

/**
 * Created by qingjie on 2018-09-05.0005.
 */
public class MineActivity extends BaseMvpViewActivity<MineContract.IMineView, MinePresenter> implements MineContract.IMineView {
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.btn_setting)
    ImageView btnSetting;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.ll_info)
    LinearLayout llInfo;
    @BindView(R.id.rl_re_passwd)
    LinearLayoutCompat rlRePasswd;
    @BindView(R.id.btn_modify_password)
    RelativeLayout btnModifyPassword;
    @BindView(R.id.rl_re_about_1)
    LinearLayoutCompat rlReAbout1;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.rl_re_about_2)
    RelativeLayout rlReAbout2;
    @BindView(R.id.btn_introduction)
    RelativeLayout btnIntroduction;
    @BindView(R.id.btn_help)
    RelativeLayout btnHelp;
    @BindView(R.id.btn_suggest)
    RelativeLayout btnSuggest;
    @BindView(R.id.btn_auto_login)
    AppCompatTextView btnAutoLogin;

    @BindColor(R.color.white)
    protected int white;

    public static void start(Activity context) {
        Intent starter = new Intent(context, MineActivity.class);
        context.startActivity(starter);
        context.overridePendingTransition(R.anim.slide_in_down, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this,
                null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        Drawable drawable = a.getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
        drawable.setColorFilter(white, PorterDuff.Mode.MULTIPLY);
        toolbar.setNavigationIcon(drawable);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("个人中心");
        super.initToolbar(toolbar);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        tvName.setText(SharedPreferencesUtils.getName(this));
    }

    @Override
    protected MinePresenter initPresenter() {
        return new MinePresenter();
    }

    @Override
    protected BaseModule initModule() {
        return new MineModule();
    }

    @Override
    public void onResult(String s) {

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

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onFailure(int code, String err) {

    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.btn_modify_password)
    void onChangPwdClick() {
        PasswordActivity.start(this);
    }

    @OnClick(R.id.btn_auto_login)
    void onAutoLoginClick() {
        if (AppContext.isLoginstate()) {
            new AlertDialog.Builder(MineActivity.this).setTitle("系统提示").setMessage("是否确认退出登录？")
                    .setPositiveButton("是", (dialog, which) -> {
                        dialog.dismiss();
                        SharedPreferencesUtils.clearUser(MineActivity.this);
                        ivHead.setBackgroundResource(R.mipmap.ic_default_head_img);
                        tvName.setText("");
                        tvPhone.setText("");
                        tvAccount.setText("");
                        LoginActivity.start(MineActivity.this);
                        ActivityUtils.getInstance().finishActivityclass(LandManagementActivity.class);
                        finish();
                    }).setNegativeButton("否", (dialog, which) -> dialog.dismiss()).show();
        } else {
            Toast.makeText(MineActivity.this, "您未登录!", Toast.LENGTH_SHORT).show();
        }
    }
}
