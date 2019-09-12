package cn.cassia.sugar.land.http;

import android.text.TextUtils;

import cn.cassia.sugar.land.AppContext;
import cn.cassia.sugar.land.Constant;
import cn.cassia.sugar.land.utils.SharedPreferencesUtils;

/**
 * Written by Mr.QingJie on 2018-1-10 0010.
 */
public class UrlUtil {

    private BaseUrl mUrl;
    private static UrlUtil mInstance;
    private static String dynamic_ip;

    private UrlUtil() {
        if (Constant.STABLE_SERVER) {
            mUrl = new StableUrl();
        } else {
            mUrl = new TestUrl();
        }
    }

    public static void init(String pn) {
        switch (pn) {
            case "cn.cassia.sugar.land.boqing":
                dynamic_ip = Constant.BOQING_URL;
                break;
            case "cn.cassia.sugar.land.guitang":
                dynamic_ip = Constant.GUITANG_URL;
                break;
            case "cn.cassia.sugar.land":
                dynamic_ip = Constant.BASE_URL;
                break;
            default:
                dynamic_ip = "http://192.168.1.113:8069";
                break;
        }
        if (mInstance == null) mInstance = new UrlUtil();
    }

    public static UrlUtil getInstance() {
        return mInstance;
    }

    public String getApiUrl() {
        return mUrl.getApiUrl();
    }

    public String getDBName() {
        return mUrl.getDBName();
    }

    public void setApiUrl(String ip) {
        mUrl.setApiUrl(ip);
    }

    public void setDBName(String db) {
        mUrl.setDBName(db);
    }

    public interface BaseUrl {

        String getApiUrl();

        String getDBName();

        void setApiUrl(String ip);

        void setDBName(String db);

    }

    public class TestUrl implements BaseUrl {
        private String ip = "http://sugarland.demo.rungui.top:80";
        private String db = "sugarland";

        @Override
        public String getApiUrl() {
            return ip;
        }

        @Override
        public String getDBName() {
            return db;
        }

        @Override
        public void setApiUrl(String ip) {
            if (!TextUtils.isEmpty(ip)) {
                this.ip = ip;
            }
        }

        @Override
        public void setDBName(String db) {
            if (!TextUtils.isEmpty(db)) {
                this.db = db;
            }
        }
    }

    public class StableUrl implements BaseUrl {
        private String ip = dynamic_ip;
        private String db = "";

        @Override
        public String getApiUrl() {
            return ip;
        }

        @Override
        public String getDBName() {
            return db;
        }

        @Override
        public void setApiUrl(String ip) {
            if (!TextUtils.isEmpty(ip)) {
                this.ip = ip;
            }
        }

        @Override
        public void setDBName(String db) {
            if (!TextUtils.isEmpty(db)) {
                this.db = db;
            }
        }
    }

}
