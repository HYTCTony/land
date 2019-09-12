package cn.cassia.sugar.land.ui.mine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.cassia.sugar.land.AppContext;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.mvp.BaseModule;
import cn.cassia.sugar.land.mvp.BaseMvpFragment;
import cn.cassia.sugar.land.ui.login.LoginActivity;
import cn.cassia.sugar.land.ui.main.MainActivity;
import cn.cassia.sugar.land.utils.ActivityUtils;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class MineFragment extends BaseMvpFragment<MineContract.IMineView, MinePresenter> implements MineContract.IMineView {
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

    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void init() {

    }

    @OnClick(R.id.btn_auto_login)
    void onClick() {
        if (AppContext.isLoginstate()) {
            new AlertDialog.Builder(getActivity()).setTitle("系统提示").setMessage("是否确认退出登录？")
                    .setPositiveButton("是", (dialog, which) -> {
                        dialog.dismiss();
                        SharedPreferencesUtils.clearUser(getActivity());
                        ivHead.setBackgroundResource(R.mipmap.ic_default_head_img);
                        tvName.setText("");
                        tvPhone.setText("");
                        tvAccount.setText("");
                        LoginActivity.start(getActivity());
                        ActivityUtils.getInstance().finishActivityclass(MainActivity.class);
                    }).setNegativeButton("否", (dialog, which) -> dialog.dismiss()).show();
        } else {
            Toast.makeText(getActivity(), "您未登录!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected boolean hasEvent() {
        return false;
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
    public void showToast(String msg) {
        showNotice(msg);
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

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onFailure(int code,String err) {

    }


    @Override
    public void onResult(String s) {

    }
}
