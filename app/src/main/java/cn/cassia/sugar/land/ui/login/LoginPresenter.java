package cn.cassia.sugar.land.ui.login;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.mvp.BasePresenter;
import cn.cassia.sugar.land.utils.GsonUtil;

/**
 * Created by qingjie on 2018-09-05.0005.
 */
public class LoginPresenter extends BasePresenter<LoginModule, LoginActivity> implements LoginContract.ILoginPresenter {
    @Override
    public void validateCredentials(String username, String password) {
        checkViewAttached();
        module.login(username, password, new AsyncResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (isViewAttached())
                    getMvpView().showProgress();
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                if (isViewAttached()) {
                    if (TextUtils.equals(content, "false")) {
                        getMvpView().hideProgress();
                        getMvpView().onFailure(FAILURE_MESSAGE, "用户名或者密码错误！");
                        return;
                    }
                    int uid = Integer.parseInt(content);
                    getCode(username, uid, password);
                }
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().onFailure(FAILURE_MESSAGE, SERVER_ERROR_STRING);
                }
            }
        });
    }

    @Override
    public void getCode(String uname, int uid, String password) {
        checkViewAttached();
        module.getCode(uid, password, new AsyncResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (isViewAttached())
                    getMvpView().showProgress();
            }

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
                readUser(uname, uid, password, json);
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().onFailure(FAILURE_MESSAGE, SERVER_ERROR_STRING);
                    readUser(uname, uid, password, "");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();

            }
        });
    }

    @Override
    public void readUser(String username, int uid, String password, String code_json) {
        checkViewAttached();
        module.readUser(uid, password, new AsyncResponseHandler() {

            @Override
            public void onFinish() {
                super.onFinish();
                if (isViewAttached())
                    getMvpView().hideProgress();
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Logger.json(content);
                if (isViewAttached()) {

                    String name = "";
                    try {
                        JSONArray array = new JSONArray(content);
                        JSONObject object = array.getJSONObject(0);
                        if (object.has("name")) {
                            name = object.getString("name");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getMvpView().navigateToHome(username, uid, password, name, code_json);
                }
            }

            @Override
            public void onFailure(int error, Object content) {
                super.onFailure(error, content);
                if (isViewAttached()) {
                    getMvpView().onFailure(FAILURE_MESSAGE, SERVER_ERROR_STRING);
                }
            }
        });
    }


}
