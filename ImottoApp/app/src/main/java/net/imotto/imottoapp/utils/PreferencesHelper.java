package net.imotto.imottoapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sunht on 16/10/28.
 */

public class PreferencesHelper {

    public static String PREFERENCE_NAME = "ImottoAppAndroid";

    /**
     * put string preferences
     *
     * @param context
     * @param key
     *            The name of the preference to modify
     * @param value
     *            The new value for the preference
     * @return True if the new values were successfully written to persistent
     *         storage.
     */
    public static boolean putString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * get string preferences
     *
     * @param context
     * @param key
     *            The name of the preference to retrieve
     * @return The preference value if it exists, or null. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a string
     * @see #getString(Context, String, String)
     */
    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    /**
     *
     * @Title: getAll
     * @Description: 读取所有配置
     * @param @param context
     * @param @return 设定文件
     * @return String 返回类型
     * @author 吕军伟
     * @date 2015年2月12日 上午9:56:22
     * @评审人:
     * @维护人:
     * @version V1.0
     */
    public static String getAll(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getAll().toString();
    }

    /**
     * get string preferences
     *
     * @param context
     * @param key
     *            The name of the preference to retrieve
     * @param defaultValue
     *            Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a string
     */
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    /**
     *
     * @Title: hasString
     * @Description: 是否含有指定键值，且不为空字符串
     * @param @param context
     * @param @param key
     * @param @return 设定文件
     * @return boolean 返回类型
     * @author 吕军伟
     * @date 2015年1月28日 上午11:43:25
     * @评审人:
     * @维护人:
     * @version V1.0
     */
    public static boolean hasString(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (settings.getString(key, null) != null && !"".equals(settings.getString(key, null))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * put int preferences
     *
     * @param context
     * @param key
     *            The name of the preference to modify
     * @param value
     *            The new value for the preference
     * @return True if the new values were successfully written to persistent
     *         storage.
     */
    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * get int preferences
     *
     * @param context
     * @param key
     *            The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a int
     * @see #getInt(Context, String, int)
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * get int preferences
     *
     * @param context
     * @param key
     *            The name of the preference to retrieve
     * @param defaultValue
     *            Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a int
     */
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    /**
     * put long preferences
     *
     * @param context
     * @param key
     *            The name of the preference to modify
     * @param value
     *            The new value for the preference
     * @return True if the new values were successfully written to persistent
     *         storage.
     */
    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * 删除 TODO(这里用一句话描述这个方法的作用)
     *
     * @Title: removeVal
     * @param context
     * @param key
     *            设定文件
     * @return void 返回类型
     * @author 牛丰产
     * @date 2015年2月28日 下午2:45:17
     * @评审人:
     * @维护人:
     * @version V1.0
     */
    public static void removeKey(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key
     *            The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a long
     * @see #getLong(Context, String, long)
     */
    public static long getLong(Context context, String key) {
        return getLong(context, key, -1);
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key
     *            The name of the preference to retrieve
     * @param defaultValue
     *            Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a long
     */
    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    /**
     * put float preferences
     *
     * @param context
     * @param key
     *            The name of the preference to modify
     * @param value
     *            The new value for the preference
     * @return True if the new values were successfully written to persistent
     *         storage.
     */
    public static boolean putFloat(Context context, String key, float value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    /**
     * get float preferences
     *
     * @param context
     * @param key
     *            The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a float
     * @see #getFloat(Context, String, float)
     */
    public static float getFloat(Context context, String key) {
        return getFloat(context, key, -1);
    }

    /**
     * get float preferences
     *
     * @param context
     * @param key
     *            The name of the preference to retrieve
     * @param defaultValue
     *            Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a float
     */
    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    /**
     * put boolean preferences
     *
     * @param context
     * @param key
     *            The name of the preference to modify
     * @param value
     *            The new value for the preference
     * @return True if the new values were successfully written to persistent
     *         storage.
     */
    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * get boolean preferences, default is false
     *
     * @param context
     * @param key
     *            The name of the preference to retrieve
     * @return The preference value if it exists, or false. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a boolean
     * @see #getBoolean(Context, String, boolean)
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * get boolean preferences
     *
     * @param context
     * @param key
     *            The name of the preference to retrieve
     * @param defaultValue
     *            Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a boolean
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    /**
     * @Title: containsKey
     * @Description: TODO( 是否包含指定的 key )
     * @param context
     * @param key
     * @return boolean
     * @throws
     * @date 2015年11月26日 下午2:22:01
     */
    public static boolean containsKey(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        if(settings == null) {
            return false;
        }
        return settings.contains(key);
    }
}
