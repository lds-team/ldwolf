package bingo.com.model;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultModel {

    public static String NGAY = "MB";
    public static String GIAI_DB = "DB";
    public static String GIAI_NHAT = "1";
    public static String GIAI_NHI = "2";
    public static String GIAI_BA = "3";
    public static String GIAI_TU = "4";
    public static String GIAI_NAM = "5";
    public static String GIAI_SAU = "6";
    public static String GIAI_BAY = "7";

    /**
     * Build String to put into Database.
     * @param message content message result.
     * @return String.
     */
    public static String buildFrom(@NonNull String message) {
        JSONObject result = new JSONObject();

        String[] mPrize = {NGAY, GIAI_DB, GIAI_NHAT, GIAI_NHI, GIAI_BA, GIAI_TU, GIAI_NAM, GIAI_SAU, GIAI_BAY};

        String lastPrize = "";

        for (String giai: mPrize) {
            lastPrize = updateResult(giai, message, result);
        }

        if (lastPrize == null)
        {
            return "";
        }

        return result.toString();
    }

    /**
     * Tra ve ngay cua cac giai trong tin nhan.
     * @param content noi dung tin nhan.
     * @return ngay.
     */
    public static String getDateFrom(String content) {
        return updateResult(NGAY, content, null);
    }

    @SuppressLint("NewApi")
    private static String updateResult(String loaiGiai, String content, JSONObject object) {
        String reg = getRegular(loaiGiai);

        Pattern patternJ = Pattern.compile(reg);
        Matcher matcherJ = patternJ.matcher(content);

        if (matcherJ.find()) {
            String group1 = matcherJ.group(1).trim();
            String group2 = matcherJ.group(2).trim();

            if(loaiGiai.equals("MB"))
            {
                return group2;
            }

            String[] arrSo = group2.split("-");

            if (!checkEnoughPrize(loaiGiai, arrSo))
                return null;

            try {object.put(group1, new JSONArray(arrSo));} catch (JSONException e) {e.printStackTrace();}
        }
        else
        {
            return null;
        }

        return reg;
    }

    private static boolean checkEnoughPrize(String loaiGiai, String[] arrSo) {
        //Maybe just check case 7 is enough.
        switch (loaiGiai) {
            case "1":
                return arrSo.length == 1;
            case "2":
                return arrSo.length == 2;
            case "3":
                return arrSo.length == 6;
            case "4":
                return arrSo.length == 4;
            case "5":
                return arrSo.length == 6;
            case "6":
                return arrSo.length == 3;
            case "7":
                return arrSo.length == 4;
            case "DB":
                return true;
            case "MB":
                return true;
            default:
                return false;
        }
    }

    public static String getRegular(String loaiGiai) {
        String reg = "";
        switch (loaiGiai) {
            case "1":
                reg = "("+loaiGiai+"):([0-9]{5})";
                break;
            case "2":
                reg = "("+loaiGiai+"):([0-9\\-]{11})";
                break;
            case "3":
                reg = "("+loaiGiai+"):([0-9\\-]{35})";
                break;
            case "4":
                reg = "("+loaiGiai+"):([0-9\\-]{19})";
                break;
            case "5":
                reg = "("+loaiGiai+"):([0-9\\-]{29})";
                break;
            case "6":
                reg = "("+loaiGiai+"):([0-9\\-]{11})";
                break;
            case "7":
                reg = "("+loaiGiai+"):([0-9\\-]{11})";
                break;
            case "DB":
                reg = "("+loaiGiai+"):([0-9]{5})";
                break;
            case "MB":
                reg = "("+loaiGiai+")[^w]([0-9\\/]{5})";
                break;
            default://DEFAUL FOR GET DATE
                reg = "("+loaiGiai+")[^w]([0-9\\/]{5})";
                break;
        }

        return reg;
    }

    /**
     * Lay ket qua giai.
     * @param json data.
     * @param prizeIndex giai.
     * @param split tach lay 2 so cuoi.
     * @return Mang cac so trong giai.
     */
    public static String[] extract(String json, String prizeIndex, boolean split) {
        Map<String, String[]> maps = extractAlls(json, split);

        if (maps != null && maps.containsKey(prizeIndex))
        {
            return maps.get(prizeIndex);
        }

        return null;
    }

    /**
     * Tat ca cac giai. Tham so la Data get tu Database. Map<Giai, So trong giai>.
     * @param json data get from Database.
     * @param split tach lay 2 so cuoi.
     * @return map.
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static Map<String, String[]> extractAlls(String json, boolean split) {
        JSONObject result = null;

        Map<String, String[]> maps = new HashMap<>();

        String[] mPrize = {GIAI_DB, GIAI_NHAT, GIAI_NHI, GIAI_BA, GIAI_TU, GIAI_NAM, GIAI_SAU, GIAI_BAY};

        try {
            result = new JSONObject(json);


            for (int i = 0; i < result.length(); i++) {

                if (result.has(String.valueOf(i))) {

                    JSONArray jsonArray = result.getJSONArray(String.valueOf(i));

                    String[] num = splitResult(split, jsonArray);

                    maps.put(mPrize[i], num);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return maps;
    }

    private static String[] splitResult(boolean split, JSONArray array) throws JSONException {
        int index = split ? 2 : 0;

        String[] num = new String[array.length()];

        for (int i = 0; i < array.length(); i++)
        {
            String so = array.getString(i);
            num[i] = so.substring(so.length() - index);
        }

        return num;
    }

}
