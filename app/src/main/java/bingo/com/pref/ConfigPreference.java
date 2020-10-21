package bingo.com.pref;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import bingo.com.base.BaseActivity;
import bingo.com.utils.JSONAnalyzeBuilder;
import bingo.com.utils.Utils;

public class ConfigPreference {

    public static final String KEEP_DE_MESSAGE = "KEEP_DE_MESSAGE";
    public static final String KEEP_LO_MESSAGE = "KEEP_LO_MESSAGE";

    public static final String KEEP_DE_RAW_MESSAGE = "KEEP_DE_RAW_MESSAGE";
    public static final String KEEP_LO_RAW_MESSAGE = "KEEP_LO_RAW_MESSAGE";

    public static final String HAS_RELOAD_TODAY = "HAS_RELOAD_TODAY";

    public static final String REGEX_CUSTOM = "";

    public static void saveMessageGiuDe(SharedPreferences pref, String message, String rawMessage) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEEP_DE_MESSAGE, message);
        editor.putString(KEEP_DE_RAW_MESSAGE, rawMessage);
        editor.apply();
    }

    public static String getMessageGiuDe(SharedPreferences pref) {

        return pref.getString(KEEP_DE_MESSAGE, "");
    }

    public static String formatMessageSaved(SharedPreferences pref, String type) {
        /*try {
            String text = "";

            JSONArray array = new JSONArray(content);
            int size = array.length();
            for (int i = 0; i < size; i++)
            {
                JSONObject object = array.getJSONObject(i);

                String num = object.getString(JSONAnalyzeBuilder.KEY_NUMBER);
                String price = object.getString(JSONAnalyzeBuilder.KEY_PRICE);

                text = text.concat(num).concat("x").concat(price).concat(" ");
            }

            return text;
        } catch (JSONException e)
        {
            return "";
        }*/

        if (type.trim().equals("de"))
            return pref.getString(KEEP_DE_RAW_MESSAGE, "").replaceAll(type + "\\s*", "");
        else if (type.trim().equals("lo"))
            return pref.getString(KEEP_LO_RAW_MESSAGE, "").replaceAll(type + "\\s*", "");
        else
            return "";
    }

    public static void saveMessageGiuLo(SharedPreferences pref, String message, String rawMessage) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEEP_LO_MESSAGE, message);
        editor.putString(KEEP_LO_RAW_MESSAGE, rawMessage);
        editor.apply();
    }

    public static String getMessageGiuLo(SharedPreferences pref) {

        return pref.getString(KEEP_LO_MESSAGE, "");
    }

    public static void saveRegexCustom(SharedPreferences pref, Map<String, String> regex) {
        JSONObject object = new JSONObject(regex);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(REGEX_CUSTOM, object.toString());
        editor.apply();
    }

    public static Map<String, String> getRegexCustom(Context context) {
        return getRegexCustom(context.getSharedPreferences(BaseActivity.PREF_NAME, Context.MODE_PRIVATE));
    }

    public static Map<String, String> getRegexCustom(SharedPreferences pref) {
        String save = pref.getString(REGEX_CUSTOM, "");
        Map<String, String> maps = new HashMap<>();
        try {
            JSONObject object = new JSONObject(save);
            Iterator<String> keys = object.keys();
            while (keys.hasNext())
            {
                String key = keys.next();
                maps.put(key, object.getString(key));
            }
        } catch (JSONException e) {

        }

        return maps;
    }

    public static void saveHasReloadToday(SharedPreferences pref, String phone) {
        String current = pref.getString(HAS_RELOAD_TODAY, "");
        if (current.contains(Utils.today()))
        {
            current = current + phone + ";";
        }
        else
        {
            current = Utils.today() + ":" + phone + ";";
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(HAS_RELOAD_TODAY, current);
        editor.apply();
    }

    public static boolean hasReloadToday(SharedPreferences pref, String phone) {
        if (phone != null)
        {
            String current = pref.getString(HAS_RELOAD_TODAY, "");

            if (!current.contains(Utils.today()))
            {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(HAS_RELOAD_TODAY, Utils.today() + ":");
                editor.apply();
                return false;
            }
            else
            {
                return current.contains(phone);
            }
        }
        else
            return false;
    }
}
