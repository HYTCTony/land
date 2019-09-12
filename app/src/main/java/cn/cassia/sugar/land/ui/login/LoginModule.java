package cn.cassia.sugar.land.ui.login;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import cn.cassia.sugar.land.http.AppClient;
import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.http.UrlUtil;
import cn.cassia.sugar.land.mvp.BaseModule;

import static java.util.Arrays.asList;

/**
 * Created by qingjie on 2018-09-05.0005.
 */
public class LoginModule extends BaseModule implements LoginContract.ILoginModule {
    @Override
    public void login(String uname, String pwd, AsyncResponseHandler callback) {
        //配置
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.isEnabledForExtensions();
        try {
            URL url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/common");
            config.setServerURL(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "authenticate", asList(UrlUtil.getInstance().getDBName(), uname, pwd, new HashMap<>())
                , callback);
    }

    @Override
    public void readUser(int uid, String pwd, AsyncResponseHandler callback) {
        //配置
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw",
                asList(UrlUtil.getInstance().getDBName(), uid, pwd, "res.users", "read",
                        asList(uid), new HashMap() {{
                            put("fields", asList(
                                    "id",
                                    "type",
                                    "mobile",
                                    "email",
                                    "name",
                                    "image",
                                    "phone",
                                    "image_medium"
                            ));
                        }}), callback);
    }

    @Override
    public void getCode(int uid, String pwd, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = new URL(UrlUtil.getInstance().getApiUrl() + "/xmlrpc/2/object");
            config.setServerURL(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", asList(UrlUtil.getInstance().getDBName(),
                uid, pwd, "kxy.mapserver.instance", "search_read", asList(asList(asList("active", "=", true))),
                new HashMap() {{
                    put("fields", asList(
                            "code"
                    ));
                }}), callback);
    }

}
