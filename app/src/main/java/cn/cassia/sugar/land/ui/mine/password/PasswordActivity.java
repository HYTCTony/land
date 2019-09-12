package cn.cassia.sugar.land.ui.mine.password;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.cassia.sugar.land.R;
import cn.cassia.sugar.land.mvp.BaseModule;
import cn.cassia.sugar.land.mvp.BaseMvpViewActivity;
import cn.cassia.sugar.land.ui.login.LoginActivity;
import cn.cassia.sugar.land.ui.main.MainActivity;
import cn.cassia.sugar.land.ui.measure.LandManagementActivity;
import cn.cassia.sugar.land.ui.mine.MineActivity;
import cn.cassia.sugar.land.utils.ActivityUtils;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;

public class PasswordActivity extends BaseMvpViewActivity<PasswordContract.IPasswordView,
        PasswordPresenter> implements PasswordContract.IPasswordView {

    @BindView(R.id.tv_username)
    AppCompatTextView tvUsername;
    @BindView(R.id.et_old_password)
    AppCompatEditText etOldPassword;
    @BindView(R.id.et_new_password)
    AppCompatEditText etNewPassword;
    @BindView(R.id.et_confirm)
    AppCompatEditText etConfirm;
    @BindView(R.id.btn)
    Button btn;

    public static void start(Context context) {
        Intent itStart = new Intent(context, PasswordActivity.class);
        context.startActivity(itStart);
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle("修改密码");
        super.initToolbar(toolbar);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_password;
    }

    @Override
    protected void initView() {
        tvUsername.setText(SharedPreferencesUtils.getUserName(this));
    }

    @Override
    protected PasswordPresenter initPresenter() {
        return new PasswordPresenter();
    }

    @Override
    protected BaseModule initModule() {
        return new PasswordModule();
    }

    @Override
    public void onDataSuccess(String result) {
        if (result != null && !"false".equals(result)) {
            showToast("修改密码成功,请重新登录");
            SharedPreferencesUtils.clearUser(this);
            LoginActivity.start(this);
            ActivityUtils.getInstance().finishActivityclass(MineActivity.class);
            ActivityUtils.getInstance().finishActivityclass(LandManagementActivity.class);
            finish();
        }
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


    @OnClick(R.id.btn)
    public void onViewClicked() {
        String password = SharedPreferencesUtils.getP(this);
        String oldPassword = etOldPassword.getText().toString();
        if (TextUtils.isEmpty(oldPassword) || !password.equals(oldPassword)) {
            showToast("原密码输入错误");
            return;
        }
        String newPassword = etNewPassword.getText().toString();
        String newPassword1 = etConfirm.getText().toString();
        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(newPassword1) || !newPassword
                .equals(newPassword1)) {
            showToast("新密码两次输入不一致");
            return;
        }
        if (!isLetterDigit(newPassword)) {
            showToast("请按照下列提示输入正确格式的密码");
            return;
        }
        presenter.postData(oldPassword, newPassword, newPassword1);
    }

    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        boolean isOverLength = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            } else if (Character.isLetter(str.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        if (str.length() > 8 && str.length() < 16) {
            isOverLength = true;
        } else {
            isOverLength = false;
        }
        boolean isRight = isDigit && isLetter && str.matches(regex) && isOverLength;
        return isRight;
    }

}
