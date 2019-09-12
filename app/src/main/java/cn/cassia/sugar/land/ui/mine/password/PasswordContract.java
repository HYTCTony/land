package cn.cassia.sugar.land.ui.mine.password;


import java.util.HashMap;

import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.mvp.BaseContract;

public class PasswordContract {

    interface IPasswordView extends BaseContract.IBaseView {
        void onDataSuccess(String result);
    }


    interface IPasswordPresenter extends BaseContract.IBasePresenter {

        void postData(String oldPassword, String newPassword, String confirmPassword);
    }

    interface IPasswordModule extends BaseContract.IBaseModule {
        void postData(int uid, String pwd, HashMap hashMap, AsyncResponseHandler callback);
    }
}
