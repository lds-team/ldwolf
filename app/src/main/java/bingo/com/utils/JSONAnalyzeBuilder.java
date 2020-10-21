package bingo.com.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONAnalyzeBuilder {

    public static final String KEY_TYPE = "KEY_TYPE";
    public static final String KEY_NUMBER = "KEY_NUMBER";
    public static final String KEY_PRICE = "KEY_PRICE";

    public static JSONObject getObjectValue(String type, String number, String price) {
        JSONObject object = new JSONObject();

        try {
            object.put(KEY_TYPE, type);
            object.put(KEY_NUMBER, number);
            object.put(KEY_PRICE, price);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }
}
