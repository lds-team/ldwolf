package bingo.com.controller.wrapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bingo.com.enumtype.LotoType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.ContactColumnMaps;
import bingo.com.model.ContactModel;
import bingo.com.utils.JSONAnalyzeBuilder;

/**
 * This object will keep value that isn't care value remain(maybe negative value if it was sent before)
 */
public class ObjectWrapperNegative {

    Map<String, int[]> current;
    Map<String, int[]> toInsert;

    Map<String, int[]> keep;

    ContactModel config;

    public ObjectWrapperNegative(ContactModel config, Cursor cNumber, Cursor cKeep) {
        this.toInsert = new HashMap<>();
        this.current = getCurrent(cNumber);
        this.keep = getCurrentKeep(cKeep);
        this.config = config;
    }

    public ObjectWrapperNegative(ContactModel config) {
        this.toInsert = new HashMap<>();
        this.current = new HashMap<>();
        this.keep = new HashMap<>();
        this.config = config;
    }

    public HashMap<String, int[]> getCurrent(Cursor cNumber) {
        HashMap<String, int[]> current = new HashMap<>();

        if (cNumber.moveToFirst())
        {
            while (!cNumber.isAfterLast())
            {
                String number = cNumber.getString(cNumber.getColumnIndex("number"));
                String type = cNumber.getString(cNumber.getColumnIndex("type"));
                int received = cNumber.getInt(cNumber.getColumnIndex("received"));
                int keep = cNumber.getInt(cNumber.getColumnIndex("keep"));
                int sent = cNumber.getInt(cNumber.getColumnIndex("sent"));

                String key = buildKey(number, type);

                if (current.containsKey(key))
                {
                    int[] cur = current.get(key);

                    cur[0] += received;
                    cur[1] += keep;
                    cur[2] += sent;

                    current.put(key, cur);
                }
                else
                {
                    current.put(key, new int[]{received, keep, sent});
                }

                cNumber.moveToNext();
            }
        }

        return current;
    }

    public HashMap<String, int[]> getCurrentKeep(Cursor cKeep) {
        HashMap<String, int[]> current = new HashMap<>();

        if (cKeep.moveToFirst())
        {
            while (!cKeep.isAfterLast())
            {
                String number = cKeep.getString(cKeep.getColumnIndex("number"));
                String type = cKeep.getString(cKeep.getColumnIndex("type"));
                int keep = cKeep.getInt(cKeep.getColumnIndex("sumkeep"));

                String key = buildKey(number, type);

                if (current.containsKey(key))
                {
                    int[] cur = current.get(key);

                    cur[0] += keep;

                    current.put(key, cur);
                }
                else
                {
                    current.put(key, new int[]{keep});
                }

                cKeep.moveToNext();
            }
        }

        return current;
    }

    public String buildKey(String num, String type) {
        return type + ":" + num;
    }

    public String parseNumber(String key, String type) {
        return key.replace(type + ":", "").trim();
    }

    public String parseType(String key, String number) {
        return key.replace(":" + number, "").trim();
    }

    public void putTo(int[] price, String number, String type) {
        String key = buildKey(number, type);

        Log.d("InsertWrapper", "putTo: " + key);

        if (current.containsKey(key))
        {
            int[] cur = current.get(key);

            cur[0] += price[0];
            cur[1] += price[1];
            cur[2] += price[2];

            current.put(key, cur);
        }
        else if (toInsert.containsKey(key))
        {
            int[] cur = toInsert.get(key);

            cur[0] += price[0];
            cur[1] += price[1];
            cur[2] += price[2];

            toInsert.put(key, cur);
        }
        else
        {
            toInsert.put(key, new int[]{price[0], price[1], price[2]});
        }
    }

    public int putKeep(int price, String number, String type) {
        String key = buildKey(number, type);
        Log.d("InsertWrapper", "putKeep: " + key);
        int max = (int) config.getMax(LotoType.convert(type));

        int currentPrice;

        if (keep.containsKey(key))
        {
            int[] current = keep.get(key);
            currentPrice = current[0];
        }
        else
        {
            currentPrice = 0;
        }

        if (max != 0)
        {
            if (max > currentPrice)
            {
                max = max - currentPrice;

                if (price > max)
                {
                    price = max;
                }
            }
            else
            {
                return 0;
            }
        }

        keep.put(key, new int[]{price + currentPrice});
        if (current.containsKey(key))
        {
            int[] cur = current.get(key);
            cur[1] += price;
            current.put(key, cur);
        }
        else if (toInsert.containsKey(key))
        {
            int[] cur = toInsert.get(key);
            cur[1] += price;
            toInsert.put(key, cur);
        }

        return price;
    }

    public void putKeepAllInsert() {
        for (String key : toInsert.keySet())
        {
            String type = key.substring(0, key.indexOf(":"));
            String number = key.substring(key.indexOf(":") + 1);
            int[] prices = toInsert.get(key);

            Double dou = null;

            if (type.equals("xien2"))
            {
                dou = config.getGiudiemxien2();
            }
            else if (type.equals("xien3"))
            {
                dou = config.getGiudiemxien3();
            }
            else if (type.equals("xien4"))
            {
                dou = config.getGiudiemxien4();
            }
            else if (type.equals("bacang"))
            {
                dou = config.getGiudiembacang();
            }

            int price = dou == null ? 0 : dou.intValue();

            if (price != 0)
            {
                prices[1] += price;
            }

            toInsert.put(key, prices);
        }
    }

    public void putKeepAllCurrent() {
        for (String key : current.keySet())
        {
            String type = key.substring(0, key.indexOf(":"));
            String number = key.substring(key.indexOf(":") + 1);
            int[] prices = current.get(key);

            Double dou = null;

            if (type.equals("xien2"))
            {
                dou = config.getGiudiemxien2();
            }
            else if (type.equals("xien3"))
            {
                dou = config.getGiudiemxien3();
            }
            else if (type.equals("xien4"))
            {
                dou = config.getGiudiemxien4();
            }
            else if (type.equals("bacang"))
            {
                dou = config.getGiudiembacang();
            }

            int price = dou == null ? 0 : dou.intValue();

            if (price != 0)
            {
                prices[1] += price;
            }

            current.put(key, prices);
        }
    }

    public void deleteNumber(JSONArray arrayAnalyze, TypeMessage typeMessage) {

        try {
            int sizeAnalyze = arrayAnalyze.length();
            for (int m = 0; m < sizeAnalyze; m++)
            {
                JSONArray array = arrayAnalyze.getJSONArray(m);

                int size = array.length();
                for (int i = 0; i < size; i++)
                {
                    JSONObject object = array.getJSONObject(i);
                    String number = object.getString(JSONAnalyzeBuilder.KEY_NUMBER);
                    String type = object.getString(JSONAnalyzeBuilder.KEY_TYPE);
                    String sPrice = object.getString(JSONAnalyzeBuilder.KEY_PRICE);

                    int price = Integer.parseInt(sPrice);

                    String key = buildKey(number, type);

                    if (current.containsKey(key))
                    {
                        int[] prices = current.get(key);

                        if (typeMessage == TypeMessage.RECEIVED)
                        {
                            prices[0] -= price;
                        }
                        else
                        {
                            prices[2] -= price;
                        }

                        current.put(key, prices);
                    }
                }
            }

        } catch (JSONException e) {
            Log.d("ObjectWrapper", "deleteNumber error");
        }
    }

    public List<ContentValues> buildToImport(String date) {
        List<ContentValues> valuesList = new ArrayList<>();
        for (String key : toInsert.keySet())
        {
            String type = key.substring(0, key.indexOf(":"));
            String number = key.substring(key.indexOf(":") + 1);
            int[] prices = toInsert.get(key);

            ContentValues values = new ContentValues();
            values.put("number", number);
            values.put("date", date);
            values.put("type", type);
            values.put("received", prices[0]);
            values.put("keep", prices[1]);
            values.put("sent", prices[2]);
            values.put("win", "0");
            values.put("countwin", "0");

            valuesList.add(values);
        }

        toInsert.clear();

        return valuesList;
    }

    public List<ContentValues> buildToUpdate(String date) {
        List<ContentValues> valuesList = new ArrayList<>();
        for (String key : current.keySet())
        {
            String type = key.substring(0, key.indexOf(":"));
            String number = key.substring(key.indexOf(":") + 1);
            int[] prices = current.get(key);

            ContentValues values = new ContentValues();
            values.put("number", number);
            values.put("date", date);
            values.put("type", type);
            values.put("received", prices[0]);
            values.put("keep", prices[1]);
            values.put("sent", prices[2]);

            valuesList.add(values);
        }

        current.clear();

        return valuesList;
    }

    public Map<String, int[]> getInsert() {
        return toInsert;
    }

    public Map<String, int[]> getCurrent() {
        return current;
    }

    public void setCurrent(Map<String, int[]> current) {
        this.current = current;
    }

    public void setCurrentKeep(Map<String, int[]> keep) {
        this.keep = keep;
    }

    public void setConfig(ContactModel config) {
        this.config = config;
    }

    @SuppressWarnings("Dangerous! May be brick data.")
    @Deprecated
    public void forceDeleteKeep() {
        for (String key : current.keySet())
        {
            int keepToDelete = 0;

            if (keep.containsKey(key))
            {
                keepToDelete = keep.get(key)[0];
            }

            int[] prices = current.get(key);
            prices[1] = prices[1] - keepToDelete;

            if (prices[1] < 0)
            {
                prices[1] = 0;
            }

            current.put(key, prices);
        }

        keep.clear();
    }

    public void clear() {
        try {
            current.clear();
            toInsert.clear();
            keep.clear();

            current = null;
            toInsert = null;
            keep = null;
        } catch (NullPointerException e) {/*Nothing.*/}
    }
}
