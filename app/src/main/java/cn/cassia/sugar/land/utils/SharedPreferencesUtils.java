package cn.cassia.sugar.land.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import cn.cassia.sugar.land.AppContext;

/**
 * @author QJ
 * @ClassName: SharedPreferencesUtils
 * @date 日期2016年11月3日 下午4:39:29 (SharedPreferences存储工具类)
 */
public class SharedPreferencesUtils {
    public static boolean isAutologon = false;

    /**
     * @param context 保存用户数据
     */
    public static void saveUserId(Context context, int userJson) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt("user_id", userJson);
        editor.apply();
    }

    /**
     * 获取用户数据
     *
     * @param context
     * @return
     */
    public static int getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getInt("user_id", -1);
    }


    /**
     * @param context 清除用户数据
     */
    public static void clearUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        AppContext.setLoginstate(false);
        isAutologon = true;
    }

    /**
     * 获取token
     *
     * @param context
     * @return
     */
    public static String getP(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("password", "");
    }

    /**
     * 获取token
     *
     * @param context
     * @return
     */
    public static void setP(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("password", token);
        editor.apply();
    }

    /**
     * 获取tel
     *
     * @param context
     * @return
     */
    public static String getTel(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("tel", "");
    }

    /**
     * 获取tel
     *
     * @param context
     * @return
     */
    public static void setTel(Context context, String tel) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("tel", tel);
        editor.apply();
    }

    /**
     * 获取tel
     *
     * @param context
     * @return
     */
    public static String getM(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("m", "");
    }

    /**
     * 获取tel
     *
     * @param context
     * @return
     */
    public static void setM(Context context, String m) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("m", m);
        editor.apply();
    }

    /**
     * 获取code
     *
     * @param context
     * @return
     */
    public static String getCode(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("code", "");
    }

    /**
     * 获取code
     *
     * @param context
     * @return
     */
    public static void setCode(Context context, String code) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("code", code);
        editor.apply();
    }

    /**
     * 获取用户头像地址ַ
     *
     * @param context
     * @return String[] 0 --- username 1 --photo
     */
    public static String getPhoto(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("headImage", "");
    }

    /**
     * 保存用户头像地址ַ
     *
     * @param context
     * @return String[] 0 --- username 1 --photo
     */
    public static void savePhoto(Context context, String img) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("headImage", img);
        editor.apply();
    }

    /**
     * 获取昵称
     *
     * @param context
     * @return
     */
    public static String getName(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("nickname", "");
    }

    /**
     * 获取昵称
     *
     * @param context
     * @return
     */
    public static void setName(Context context, String m) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("nickname", m);
        editor.apply();
    }

    /**
     * 获取sex
     *
     * @param context
     * @return
     */
    public static String getSex(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("sex", "");
    }

    /**
     * 获取sex
     *
     * @param context
     * @return
     */
    public static void setSex(Context context, String m) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("sex", m);
        editor.apply();
    }

    /**
     * 获取用户名
     *
     * @param context
     * @return
     */
    public static String getUserName(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("user_name", "");
    }

    /**
     * 获取用户名
     *
     * @param context
     * @return
     */
    public static void setUserName(Context context, String m) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("user_name", m);
        editor.apply();
    }

    public static String getIP(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("ip", "");
    }

    public static void setIP(Context context, String m) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("ip", m);
        editor.apply();
    }

    public static String getDB(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("db", "");
    }


    public static void setDB(Context context, String m) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("db", m);
        editor.apply();
    }
    public static String getAppKey(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("AppKey", "");
    }

    public static void setAppKey(Context context, String m) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("AppKey", m);
        editor.apply();
    }

    public static String getAppSecret(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("AppSecret", "");
    }


    public static void setAppSecret(Context context, String m) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("AppSecret", m);
        editor.apply();
    }
}
