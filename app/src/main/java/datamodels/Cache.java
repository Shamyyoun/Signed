package datamodels;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Shamyyoun on 3/29/2015.
 */
public class Cache {
    /**
     * method used to update active user response in SP
     */
    public static void updateActiveUserResponse(Context context, String value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SP_RESPONSES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.SP_KEY_ACTIVE_USER_RESPONSE, value);
        editor.commit();
    }

    /**
     * method used to get active user response from SP
     */
    public static String getActiveUserResponse(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SP_RESPONSES, Context.MODE_PRIVATE);
        String response = sp.getString(Constants.SP_KEY_ACTIVE_USER_RESPONSE, null);

        return response;
    }

    /**
     * method used to update user logged in SP
     */
    public static void setLoggedIn(Context context, boolean loggedIn) {
        updateCachedBoolean(context, Constants.SP_CONFIG, Constants.SP_KEY_LOGGED_IN, loggedIn);
    }

    /**
     * method used to get logged in flag from SP
     */
    public static boolean isLoggedIn(Context context) {
        return getCachedBoolean(context, Constants.SP_CONFIG, Constants.SP_KEY_LOGGED_IN);
    }

    /**
     * method used to update waiting_login in SP
     */
    public static void setWaitingLogin(Context context, boolean waiting) {
        updateCachedBoolean(context, Constants.SP_CONFIG, Constants.SP_KEY_WAITING_LOGIN, waiting);
    }

    /**
     * method used to get waiting_login flag from SP
     */
    public static boolean isWaitingLogin(Context context) {
        return getCachedBoolean(context, Constants.SP_CONFIG, Constants.SP_KEY_WAITING_LOGIN);
    }

    /**
     * method, used to update disable bluetooth time in sp
     */
    public static void updateDisableBluetoothTime(Context context, long time) {
        updateCachedLong(context, Constants.SP_CONFIG, Constants.SP_KEY_DISABLE_BLUETOOTH_TIME, time);
    }

    /**
     * method, used to get last disable bluetooth time from sp
     */
    public static long getDisableBluetoothTime(Context context) {
        return getCachedLong(context, Constants.SP_CONFIG, Constants.SP_KEY_DISABLE_BLUETOOTH_TIME);
    }

    /**
     * method, used to update stop service time in sp
     */
    public static void updateStopServiceTime(Context context, long time) {
        updateCachedLong(context, Constants.SP_CONFIG, Constants.SP_KEY_STOP_SERVICE_TIME, time);
    }

    /**
     * method, used to get stop service time from sp
     */
    public static long getStopServiceTime(Context context) {
        return getCachedLong(context, Constants.SP_CONFIG, Constants.SP_KEY_STOP_SERVICE_TIME);
    }

    /**
     * method used to update started_normally in SP
     */
    public static void setStartedNormally(Context context, boolean normally) {
        updateCachedBoolean(context, Constants.SP_CONFIG, Constants.SP_KEY_STARTED_NORMALLY, normally);
    }

    /**
     * method used to get started_normally flag from SP
     */
    public static boolean isStartedNormally(Context context) {
        return getCachedBoolean(context, Constants.SP_CONFIG, Constants.SP_KEY_STARTED_NORMALLY);
    }

    /**
     * method used to update stopped_normally in SP
     */
    public static void setStoppedNormally(Context context, boolean normally) {
        updateCachedBoolean(context, Constants.SP_CONFIG, Constants.SP_KEY_STOPPED_NORMALLY, normally);
    }

    /**
     * method used to get stopped_normally flag from SP
     */
    public static boolean isStoppedNormally(Context context) {
        return getCachedBoolean(context, Constants.SP_CONFIG, Constants.SP_KEY_STOPPED_NORMALLY);
    }


    /**
     * method used to update boolean in SP
     */
    private static void updateCachedBoolean(Context context, String spName, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * method used to get cached boolean from SP
     */
    private static boolean getCachedBoolean(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        boolean value = sp.getBoolean(key, false);

        return value;
    }

    /**
     * method used to update long in SP
     */
    private static void updateCachedLong(Context context, String spName, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * method used to get cached long from SP
     */
    private static long getCachedLong(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        long value = sp.getLong(key, 0);

        return value;
    }
}
