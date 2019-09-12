package cn.cassia.sugar.land.http;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.util.List;

public class AppClient {

    private static HttpRequest mHttpRequest;

    static {
        mHttpRequest = new HttpRequest();
    }

    public static void post(XmlRpcClientConfigImpl config, String pMethodName, List list, AsyncResponseHandler responsehandler) {
        mHttpRequest.post(config, pMethodName, list, responsehandler);
    }
}
