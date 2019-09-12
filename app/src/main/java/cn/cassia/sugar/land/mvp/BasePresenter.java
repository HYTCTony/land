package cn.cassia.sugar.land.mvp;

/**
 * Created by qingjie on 2018-05-25.0025.
 */
public class BasePresenter<M extends BaseModule, V extends BaseContract.IBaseView> {
    public M module;
    public V view;

    void attatchWindow(M m, V v) {
        this.module = m;
        this.view = v;
    }

    void detachWindow() {
        this.module = null;
        this.view = null;
    }

    public boolean isViewAttached() {
        return view != null;
    }

    public V getMvpView() {
        return view;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) {
            throw new MvpViewNotAttachedException();
        }
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("请求数据前请先调用 attachView(MvpView) 方法与View建立连接");
        }
    }
}
