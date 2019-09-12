package cn.cassia.sugar.land.http;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpRequest {
    private ThreadPoolExecutor threadPool;

    public HttpRequest() {
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public void post(XmlRpcClientConfigImpl config, String pMethodName, List list, AsyncResponseHandler responsehandler) {
        threadPool.submit(new AsyncHttpRequest(config, pMethodName, list, responsehandler));
    }
}