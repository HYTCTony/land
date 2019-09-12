package cn.cassia.sugar.land.ui.mine.password;

import android.text.TextUtils;

import java.util.HashMap;

import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.mvp.BasePresenter;

public class PasswordPresenter extends BasePresenter<PasswordModule, PasswordActivity> implements PasswordContract.IPasswordPresenter {
    @Override
    public void postData(String oldPassword, String newPassword, String confirmPassword) {
        checkViewAttached();
        if (TextUtils.isEmpty(oldPassword)) {
            getMvpView().showToast("请输入原密码");
            return;
        }
        if (TextUtils.isEmpty(oldPassword)) {
            getMvpView().showToast("请输入新密码");
            return;
        }
        if (TextUtils.isEmpty(oldPassword)) {
            getMvpView().showToast("请输入确认新密码");
            return;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("password", newPassword);
        module.postData(getMvpView().getUid(), getMvpView().getPwd(), hashMap, new AsyncResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                if (isViewAttached()) {
                    getMvpView().onDataSuccess(content);
                }
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached())
                    getMvpView().showToast(SERVER_ERROR_STRING);
            }
        });
    }
}
