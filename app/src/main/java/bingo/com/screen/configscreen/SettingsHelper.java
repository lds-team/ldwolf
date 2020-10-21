package bingo.com.screen.configscreen;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import bingo.com.utils.Keysaved;

public class SettingsHelper {

    public static final int CHOT_SO_DU = 0;
    public static final int CHOT_SO_DU_VA_CONG_NO_CU = 1;

    public static final int OK_TIN_VA_NOI_DUNG = 0;
    public static final int OK_TIN = 1;
    public static final int KHONG_GUI = 2;
    public static final int OK_TIN_DA_NHAN = 3;

    public static final int DELIVER_LONG_MESSAGE = 0;
    public static final int DELIVER_MESSAGE_160 = 1;
    public static final int DELIVER_MESSAGE_320 = 2;
    public static final int DELIVER_MESSAGE_480 = 3;

    public static boolean saveTimeReceiveLoXien(SharedPreferences pref, String phone, String time) {
        String saveTime = pref.getString(Keysaved.TIME_RECEIVE_LOXIEN, "");

        JSONObject jsonObject;
        try {
            jsonObject = saveTime.isEmpty() ? new JSONObject() : new JSONObject(saveTime);

            jsonObject.put(phone, time);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Keysaved.TIME_RECEIVE_LOXIEN, jsonObject.toString());
            editor.apply();
            return true;
        } catch (JSONException e) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Keysaved.TIME_RECEIVE_LOXIEN, "");
            editor.apply();
            return false;
        }
    }

    public static String getTimeReceiveLoXien(SharedPreferences pref, String phone) {

        String saveTime = pref.getString(Keysaved.TIME_RECEIVE_LOXIEN, "");

        JSONObject jsonObject;
        try {
            jsonObject = saveTime.isEmpty() ? new JSONObject() : new JSONObject(saveTime);

            return jsonObject.getString(phone);
        } catch (JSONException e) {
            return "18:32";
        }
    }

    public static boolean saveTimeReceiveDe(SharedPreferences pref, String phone, String time) {
        String saveTime = pref.getString(Keysaved.TIME_RECEIVE_DE, "");

        JSONObject jsonObject;
        try {
            jsonObject = saveTime.isEmpty() ? new JSONObject() : new JSONObject(saveTime);

            jsonObject.put(phone, time);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Keysaved.TIME_RECEIVE_DE, jsonObject.toString());
            editor.apply();
            return true;
        } catch (JSONException e) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Keysaved.TIME_RECEIVE_DE, "");
            editor.apply();
            return false;
        }
    }

    public static String getTimeReceiveDe(SharedPreferences pref, String phone) {

        String saveTime = pref.getString(Keysaved.TIME_RECEIVE_DE, "");

        JSONObject jsonObject;
        try {
            jsonObject = saveTime.isEmpty() ? new JSONObject() : new JSONObject(saveTime);

            return jsonObject.getString(phone);
        } catch (JSONException e) {
            return "18:32";
        }
    }

    //------
    public static void saveChotSoDuConfig(SharedPreferences pref, int type) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Keysaved.CHOT_SO_DU, type);
        editor.apply();
    }

    public static int getChotSoDuConfig(SharedPreferences pref) {

        return pref.getInt(Keysaved.CHOT_SO_DU, CHOT_SO_DU);
    }

    //-----
    public static void saveReplyMessageConfig(SharedPreferences pref, int type) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Keysaved.REPLY_MESSAGE, type);
        editor.apply();
    }

    public static int getReplyMessageConfig(SharedPreferences pref) {

        return pref.getInt(Keysaved.REPLY_MESSAGE, KHONG_GUI);
    }

    //-----
    public static void saveDeliveryConfig(SharedPreferences pref, int type) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Keysaved.MESSAGE_DELIVERY, type);
        editor.apply();
    }

    public static int getDeliveryConfig(SharedPreferences pref) {

        return pref.getInt(Keysaved.MESSAGE_DELIVERY, DELIVER_LONG_MESSAGE);
    }

    //----
    public static void savePointDeliverConfig(SharedPreferences pref, boolean isChecked) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Keysaved.DELIVERY_BY_POINT, isChecked);
        editor.apply();
    }

    public static boolean getPointDeliverConfig(SharedPreferences pref) {

        return pref.getBoolean(Keysaved.DELIVERY_BY_POINT, false);
    }

    public static void save3CangConfig(SharedPreferences pref, int count) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Keysaved.CONFIG_3CANG, count);
        editor.apply();
    }

    public static int get3CangConfig(SharedPreferences pref) {

        return pref.getInt(Keysaved.CONFIG_3CANG, 0);
    }

    //-----
    public static void saveReceiveDupplicate(SharedPreferences pref, boolean receive) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Keysaved.RECEIVE_DUPPLICATE, receive);
        editor.apply();
    }

    public static boolean getReceiveDupplicate(SharedPreferences pref) {

        return pref.getBoolean(Keysaved.RECEIVE_DUPPLICATE, false);
    }
}
