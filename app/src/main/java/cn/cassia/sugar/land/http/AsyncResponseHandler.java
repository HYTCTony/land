package cn.cassia.sugar.land.http;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.gson.Gson;


public class AsyncResponseHandler {

    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;

    protected static final String JSON_EXCEPTION_ERROR_STRING = "数据出现异常，请稍后再试";
    protected static final String NETWORK_ERROR_STRING = "请检查您的网络连接是否正常";
    protected static final String SERVER_ERROR_STRING = "服务器出现异常，请稍后再试 ";

    private Handler handler;

    public AsyncResponseHandler() {
        // Set up a handler to post events back to the correct thread if possible
        if (Looper.myLooper() != null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    AsyncResponseHandler.this.handleMessage(msg);
                }
            };
        }
    }

    /**
     * Fired when the request is started, override to handle in your own code
     */
    public void onStart() {
    }

    /**
     * Fired in all cases when the request is finished, after both success and failure, override to handle in your own code
     */
    public void onFinish() {
    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param content the body of the HTTP response from the server
     */
    public void onSuccess(Object content) {
    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     * @param statusCode the status code of the response
     * @param headers the headers of the HTTP response
     * @param content the body of the HTTP response from the server
     */
//    public void onSuccess(int statusCode, Header[] headers, String content) {
//        onSuccess(statusCode, content);
//    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param statusCode the status code of the response
     * @param content    the body of the HTTP response from the server
     */
    public void onSuccess(int statusCode, String content) {
        onSuccess(content);
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     *
     * @param error the underlying cause of the failure
     * @deprecated use {@link #onFailure(int, Object)}
     */
    @Deprecated
    public void onFailure(int error) {
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     *
     * @param error   the underlying cause of the failure
     * @param content the response body, if any
     */
    public void onFailure(int error, Object content) {
        // By default, call the deprecated onFailure(Throwable) for compatibility
        onFailure(error);
    }

    protected void sendSuccessMessage(int statusCode, Object result) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, result));
    }

    protected void sendFailureMessage(int status, Object result) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, result));
    }

    protected void sendFailureMessage(Throwable e, byte[] responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
    }

    protected void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null));
    }

    protected void sendFinishMessage() {
        sendMessage(obtainMessage(FINISH_MESSAGE, null));
    }

    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    protected void handleSuccessMessage(int statusCode, String responseBody) {
        onSuccess(statusCode, responseBody);
    }

    protected void handleFailureMessage(int e, Object responseBody) {
        onFailure(e, responseBody);
    }


    // Methods which emulate android's Handler and Message methods
    protected void handleMessage(Message msg) {
        Object response;
        switch (msg.what) {
            case SUCCESS_MESSAGE:
                response = msg.obj;
                Gson gson = new Gson();
                String jsonStr = gson.toJson(response);
                handleSuccessMessage(msg.what, jsonStr);
                break;
            case FAILURE_MESSAGE:
                response = msg.obj;
                handleFailureMessage(-1, response);
                break;
            case START_MESSAGE:
                onStart();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
        }
    }

    protected void sendMessage(Message msg) {
        if (handler != null) {
            handler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }

    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg = null;
        if (handler != null) {
            msg = this.handler.obtainMessage(responseMessage, response);
        } else {
            msg = Message.obtain();
            msg.what = responseMessage;
            msg.obj = response;
        }
        return msg;
    }

    // Interface to AsyncHttpRequest
    void sendResponseMessage(int status, Object result) {
        if (status <= 200 && status >= 300) {
            sendFailureMessage(status, result);
        } else {
            sendSuccessMessage(status, result);
        }
    }
}
