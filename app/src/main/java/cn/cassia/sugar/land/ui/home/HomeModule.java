package cn.cassia.sugar.land.ui.home;

import com.orhanobut.logger.Logger;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import cn.cassia.sugar.land.http.AppClient;
import cn.cassia.sugar.land.http.AsyncResponseHandler;
import cn.cassia.sugar.land.mvp.BaseModule;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class HomeModule extends BaseModule implements HomeContract.IHomeLoginModule {

    @Override
    public void getData(int uid, String pwd, AsyncResponseHandler callback) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            URL url = null;
            url = new URL("http://192.168.1.127:8269" + "/xmlrpc/2/object");
            config.setServerURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        AppClient.post(config, "execute_kw", Arrays.asList("shibie", uid, pwd, "kxy.land", "test"
                , Arrays.asList(new HashMap())), callback);
    }
}
