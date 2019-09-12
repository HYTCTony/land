package cn.cassia.sugar.land.ui.mine.password;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import cn.cassia.sugar.land.http.AppClient;
import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.http.UrlUtil;
import cn.cassia.sugar.land.mvp.BaseModule;

import static java.util.Arrays.asList;

public class PasswordModule extends BaseModule implements PasswordContract.IPasswordModule {

    @Override
    public void postData(int uid, String pwd, HashMap hashMap, AsyncResponseHandler callback) {
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
                asList(UrlUtil.getInstance().getDBName(), uid, pwd,
                        "res.users", "write",
                        asList(asList(uid), hashMap)
                ), callback);
    }
}
