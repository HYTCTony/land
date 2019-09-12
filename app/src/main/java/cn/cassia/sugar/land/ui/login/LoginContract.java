package cn.cassia.sugar.land.ui.login;

import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.mvp.BaseContract;
import cn.cassia.sugar.land.ui.mine.MineContract;

/**
 * Created by qingjie on 2018-09-05.0005.
 */
public class LoginContract {
    public interface ILoginView extends BaseContract.IBaseView {
        void navigateToHome(String userName, int uid, String pwd, String nickname, String code_json);
    }

    public interface ILoginPresenter extends BaseContract.IBasePresenter {
        void validateCredentials(String username, String password);

        void readUser(String username, int uid, String password, String code_json);

        void getCode(String uname, int uid, String password);
    }

    public interface ILoginModule extends BaseContract.IBaseModule {
        void login(String uname, String pwd, AsyncResponseHandler callback);

        void readUser(int uid, String pwd, AsyncResponseHandler callback);

        void getCode(int uid, String pwd, AsyncResponseHandler callback);
    }
}
