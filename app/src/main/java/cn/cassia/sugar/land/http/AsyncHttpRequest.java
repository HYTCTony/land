package cn.cassia.sugar.land.http;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.util.List;

public class AsyncHttpRequest implements Runnable {
    private XmlRpcClient client;
    AsyncResponseHandler responsehandler;
    XmlRpcClientConfigImpl config;
    String pMethodName;
    List list;

    private static final int SUCCESS = 220;
    private static final int ERRO = -1;

    public AsyncHttpRequest(XmlRpcClientConfigImpl config, String pMethodName, List list, AsyncResponseHandler responsehandler) {
        this.config = config;
        this.pMethodName = pMethodName;
        this.list = list;
        this.responsehandler = responsehandler;
        client = new XmlRpcClient();
    }

    @Override
    public void run() {
        try {
            if (responsehandler != null) {
                responsehandler.sendStartMessage();
            }
            makeRequest();
            if (responsehandler != null) {
                responsehandler.sendFinishMessage();
            }
        } catch (Exception e) {
            if (responsehandler != null) {
                responsehandler.sendFinishMessage();
            }
        }
    }

    private void makeRequest() {
        try {
            Object obj = client.execute(config, pMethodName, list);
            if (responsehandler != null) {
                responsehandler.sendResponseMessage(SUCCESS, obj);
            }
        } catch (XmlRpcException e) {
            e.printStackTrace();
            responsehandler.sendFailureMessage(e, null);
        }
    }
}