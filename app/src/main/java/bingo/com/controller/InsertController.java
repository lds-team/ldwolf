package bingo.com.controller;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.bingo.analyze.Analyze;
import com.bingo.analyze.AnalyzeSMSNew;
import com.bingo.analyze.NumberAndUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bingo.com.callbacks.ErrorListener;
import bingo.com.controller.wrapper.ObjectWrapper;
import bingo.com.enumtype.LotoType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db2.FollowDBHelper2;
import bingo.com.model.AnalyzeModel;
import bingo.com.model.ContactModel;
import bingo.com.model.KeepModel;
import bingo.com.model.ReceivedModel;
import bingo.com.pref.ConfigPreference;
import bingo.com.utils.Constant;
import bingo.com.utils.Formats;
import bingo.com.utils.JSONAnalyzeBuilder;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.Utils;

public class InsertController {

    public static String analyzeSingleMessage(String name, String phone, String content, String time, TypeMessage type,
                                              Map<String, String> customRegex,
                                              boolean receiveDe, boolean receiveLo, boolean noKeep, ErrorListener hasError) {
        try {
            ContactModel config = LoaderSQLite.getConfigParameter(phone);
            ConfigControl.setConfigForUser(config);

            String error;

            AnalyzeModel analyzeModel = new AnalyzeModel();
            ReceivedModel receivedModel = new ReceivedModel();
            receivedModel.setPhone(phone);
            receivedModel.setTime(time);
            receivedModel.setTypemessage(type.name());
            receivedModel.setDate(Utils.convertDate(time));
            receivedModel.setDetail(new JSONArray());

            String date = Utils.convertDate(time);

            Cursor cNumber = FollowDBHelper2.getDatabase().getAllNumber(date);
            Cursor cKeep = FollowDBHelper2.getDatabase().getAllKeepFromPhone(phone, date);
            ObjectWrapper wrapper = new ObjectWrapper(config, cNumber, cKeep);
            cNumber.close();
            cKeep.close();

            List<ContentValues> valuesList = new ArrayList<>();

            AnalyzeSMSNew analyzeSMS = new AnalyzeSMSNew();

            ArrayList<Analyze> listPoint = analyzeSMS.analyzeMessageWithOutResult(content, customRegex);

            if (listPoint.size() == 0)
            {
                //Core analyze message hasn't detect error but can not analyze to number.
                MessageDBHelper.updateSingleMessageAnalyze(false, phone, time);
            }

            for (final Analyze analyze : listPoint)
            {
                if (analyze.error || analyze.numberAndUnit.Type.equalsIgnoreCase("Unknow")
                        || analyze.numberAndUnit.Numbers == null /*|| analyze.numberAndUnit.Numbers.size() == 0*/)
                {
                    error = analyze.errorMessage;

                    MessageDBHelper.updateError(phone, time, error);

                    int row = MessageDBHelper.updateSingleMessageAnalyze(false, phone, time);
                    Log.d("StatisticControlImport", "Update Analyze Status = false for " + phone + " in row " + row);

                    if (hasError != null)
                    {
                        hasError.errorNotify(true, receiveDe && receiveLo);
                        hasError = null;
                    }

                    return error;
                }
                else
                {
                    MessageDBHelper.updateError(phone, time, "");

                    if (!receiveDe && (analyze.numberAndUnit.Type.contains(LotoType.de.name()) || analyze.numberAndUnit.Type.contains(LotoType.bacang.name())))
                    {
                        MessageDBHelper.updateSingleMessageAnalyze(false, phone, time);
                        return "Hết giờ nhận số.";
                    }
                    else
                    {
                        //Update Status Analyze for each Message.
                        int row = MessageDBHelper.updateSingleMessageAnalyze(true, phone, time);
                        Log.d("StatisticControlImport", "Update Analyze Status = true for " + phone + " in row " + row);

                        if (hasError != null)
                        {
                            hasError.errorNotify(false, receiveLo);
                            hasError = null;
                        }
                    }
                }

                importResultAnalyze(wrapper, valuesList, name, phone, content, time, type, analyze, config, analyzeModel, receivedModel, receiveDe, receiveLo, noKeep);
            }

            wrapper.putKeepAllInsert();

            importData(wrapper, valuesList, date);

            importCacheDatabase(phone, time, analyzeModel, receivedModel, receiveLo);
        } catch (Exception e) {
            MessageDBHelper.updateSingleMessageAnalyze(false, phone, time);
            return content;
        }

        return null;
    }

    private static String importResultAnalyze(ObjectWrapper wrapper, List<ContentValues> valuesList,
                                              String name, String phone, String content, String time, TypeMessage typeMessage,
                                              Analyze analyze, ContactModel config, AnalyzeModel analyzeModel, ReceivedModel receivedModel,
                                              boolean receiveDe, boolean receiveLo, boolean noKeep) {
        NumberAndUnit numberAndUnit = analyze.numberAndUnit;
        JSONArray jsonArray = new JSONArray();

        if (numberAndUnit == null)
        {
            return "Error";
        }

        String type = numberAndUnit.Type;
        //Check time to import number.
        if (type.contains("de") || type.contains("bacang"))
        {
            if (!receiveDe)
            {
                return null;
            }
        }
        else
        {
            if (!receiveLo)
                return null;
        }


        ArrayList<String> numberUnitList = numberAndUnit.Numbers;
        for (String arrNum : numberUnitList)
        {
            if (type.contains("xq") || type.contains("xien"))
            {
                arrNum = arrNum.trim();
                Log.d("StatisticControlImport", "Import num: ---" + arrNum + "---");

                int countNum = arrNum.split(Constant.REGEX_SEPARATE_NUMBER_ANALYZE).length;
                String typeNum = "xien" + countNum;

                int received = 0;
                int sent = 0;

                if (typeMessage == TypeMessage.RECEIVED)
                {
                    received = Integer.parseInt(numberAndUnit.Price);
                }
                else if (typeMessage == TypeMessage.SENT)
                {
                    sent = Integer.parseInt(numberAndUnit.Price);
                }

                wrapper.putTo(new int[]{received, 0, sent}, arrNum, typeNum);

                //Update sum cache.
                if (countNum == 2)
                {
                    int current = analyzeModel.getXien2() + Integer.parseInt(numberAndUnit.Price);
                    analyzeModel.setXien2(current);

                    receivedModel.setActualcollect_xien2(current * config.getXIEN2() / 1000d);
                }
                else if (countNum == 3)
                {
                    int current = analyzeModel.getXien3() + Integer.parseInt(numberAndUnit.Price);
                    analyzeModel.setXien3(current);

                    receivedModel.setActualcollect_xien3(current * config.getXIEN3() / 1000d);
                }
                else
                {
                    int current = analyzeModel.getXien4() + Integer.parseInt(numberAndUnit.Price);
                    analyzeModel.setXien4(current);

                    receivedModel.setActualcollect_xien4(current * config.getXIEN4() / 1000d);
                }

                //
                jsonArray.put(JSONAnalyzeBuilder.getObjectValue(typeNum, arrNum, numberAndUnit.Price));

                //Just keep each message come when not deliver.
                if (!noKeep)
                {
                    double hs = ConfigControl.getHeSo(config, typeNum);

                    if (hs != 0d)
                    {
                        int keepPrice = (int) (Integer.parseInt(numberAndUnit.Price) * hs);
                        int actKeep = wrapper.putKeep(keepPrice, arrNum, typeNum);

                        if (actKeep > 0)
                        {
                            ContentValues values = new ContentValues();
                            values.put("name", name);
                            values.put("phone", phone);
                            values.put("number", arrNum);
                            values.put("date", Utils.convertDate(time));
                            values.put("time", time);
                            values.put("type", typeNum);
                            values.put("parenttype", "xien");
                            values.put("price", actKeep);
                            values.put("actualcollect", "0");

                            valuesList.add(values);
                        }
                    }

                    Double dou = null;

                    if (typeNum.equals("xien2"))
                    {
                        dou = config.getGiudiemxien2();
                    }
                    else if (typeNum.equals("xien3"))
                    {
                        dou = config.getGiudiemxien3();
                    }
                    else if (typeNum.equals("xien4"))
                    {
                        dou = config.getGiudiemxien4();
                    }

                    int keepAll = dou == null ? 0 : dou.intValue();

                    if (keepAll != 0)
                    {
                        ContentValues values = new ContentValues();
                        values.put("name", Constant.ROOT_NAME);
                        values.put("phone", Constant.UNKNOWN_PHONE_GIUXIEN);
                        values.put("number", arrNum);
                        values.put("date", Utils.convertDate(time));
                        values.put("time", time);
                        values.put("type", typeNum);
                        values.put("parenttype", "xien");
                        values.put("price", keepAll);
                        values.put("actualcollect", "0");

                        valuesList.add(values);
                    }
                }
            }
            else
            {
                final String[] arr = arrNum.split(Constant.REGEX_SEPARATE_NUMBER_ANALYZE);

                for (String num : arr) {

                    if (num.isEmpty())
                    {
                        continue;
                    }

                    Log.d("StatisticControlImport", "Import num: ---" + num + "---");

                    int received = 0;
                    int sent = 0;

                    if (typeMessage == TypeMessage.RECEIVED)
                    {
                        received = Integer.parseInt(numberAndUnit.Price);
                    }
                    else if (typeMessage == TypeMessage.SENT)
                    {
                        sent = Integer.parseInt(numberAndUnit.Price);
                    }

                    wrapper.putTo(new int[]{received, 0, sent}, num, type);

                    if (type.contains("de"))
                    {
                        int current = analyzeModel.getDe() + Integer.parseInt(numberAndUnit.Price);
                        analyzeModel.setDe(current);

                        receivedModel.setActualcollect_de(current * config.getDITDB() / 1000d);
                    }
                    else if (type.contains("lo"))
                    {
                        int current = analyzeModel.getLo() + Integer.parseInt(numberAndUnit.Price);
                        analyzeModel.setLo(current);

                        receivedModel.setActualcollect_lo(current * config.getLO() / 1000d);
                    }
                    else
                    {
                        int current = analyzeModel.getBacang() + Integer.parseInt(numberAndUnit.Price);
                        analyzeModel.setBacang(current);

                        receivedModel.setActualcollect_bacang(current * config.getBACANG() / 1000d);
                    }

                    jsonArray.put(JSONAnalyzeBuilder.getObjectValue(type, num, numberAndUnit.Price));

                    //Just keep each message come when not deliver.
                    if (!noKeep)
                    {
                        double hs = ConfigControl.getHeSo(config, type);

                        if (hs != 0d)
                        {
                            int keepPrice = (int) (Integer.parseInt(numberAndUnit.Price) * hs);
                            int actKeep = wrapper.putKeep(keepPrice, num, type);

                            if (actKeep > 0)
                            {
                                ContentValues values = new ContentValues();
                                values.put("name", name);
                                values.put("phone", phone);
                                values.put("number", num);
                                values.put("date", Utils.convertDate(time));
                                values.put("time", time);
                                values.put("type", type);
                                values.put("parenttype", type);
                                values.put("price", actKeep);
                                values.put("actualcollect", "0");

                                valuesList.add(values);
                            }
                        }

                        Double dou = null;
                        if (type.equals("bacang"))
                        {
                            dou = config.getGiudiembacang();

                            int keepAll = dou.intValue();

                            if (keepAll != 0)
                            {
                                ContentValues values = new ContentValues();
                                values.put("name", Constant.ROOT_NAME);
                                values.put("phone", Constant.UNKNOWN_PHONE_GIUBACANG);
                                values.put("number", arrNum);
                                values.put("date", Utils.convertDate(time));
                                values.put("time", time);
                                values.put("type", "bacang");
                                values.put("parenttype", "bacang");
                                values.put("price", keepAll);
                                values.put("actualcollect", "0");

                                valuesList.add(values);
                            }
                        }
                    }
                }
            }
        }

        JSONArray analyzeArray = receivedModel.getDetail();
        analyzeArray.put(jsonArray);

        return null;
    }

    private static void importData(ObjectWrapper wrapper, List<ContentValues> keeps, String date) {
        FollowDBHelper2.getDatabase().insertArray(wrapper.buildToImport(date));
        FollowDBHelper2.getDatabase().updateArray(wrapper.buildToUpdate(date), null);
        FollowDBHelper2.getDatabase().insertKeepArray(keeps, null);

        wrapper.clear();
    }

    private static void importCacheDatabase(String phone, String time, AnalyzeModel analyzeModel, ReceivedModel receivedModel, boolean receiveLo) {
        //Sort by type.
        JSONArray array = receivedModel.getDetail();
        /*array = Utils.sortByType(array);*/
        receivedModel.setDetail(array);

        MessageDBHelper.getDb().updateAnalyzeResult(phone, time, analyzeModel);
        MessageDBHelper.getDb().insertReceivedTable(receivedModel);

        /*String messageAnalyze = array.toString()
                .replaceAll("\\W", "")
                .replaceAll(JSONAnalyzeBuilder.KEY_TYPE, " ")
                .replaceAll(JSONAnalyzeBuilder.KEY_NUMBER, " ")
                .replaceAll(JSONAnalyzeBuilder.KEY_PRICE, "x").trim();*/

        String message = buildAnalyzeMessage(array, receiveLo);
        MessageDBHelper.updateAnalyzeContent(phone, time, message);
    }

    private static String buildAnalyzeMessage(JSONArray arrayAnalyze, boolean receiveLo) {
        String message = "";
        String oldType = null;

        try {
            int sizeAnalyze = arrayAnalyze.length();
            for (int j = 0; j < sizeAnalyze; j++)
            {
                JSONArray array = arrayAnalyze.getJSONArray(j);
                Utils.sortByType(array);

                int size = array.length();
                for (int i = 0; i < size; i++)
                {
                    JSONObject object = array.getJSONObject(i);
                    String type = object.getString(JSONAnalyzeBuilder.KEY_TYPE);
                    String number = object.getString(JSONAnalyzeBuilder.KEY_NUMBER);
                    String price = object.getString(JSONAnalyzeBuilder.KEY_PRICE);

                    if (type.contains("xien"))
                    {
                        type = "xien";
                    }

                    if (oldType == null)
                    {
                        message = message.concat(type).concat("<br>");
                    }
                    else
                    {
                        if (!oldType.equals(type))
                        {
                            message = message.concat("<br>").concat(type).concat("<br>");
                        }
                    }
                    oldType = type;

                    //Cat number.
                    if (i == size - 1)
                    {
                        message = message.concat(number).concat("x").concat(price);
                    }
                    else
                    {
                        JSONObject nextObject = array.getJSONObject(i + 1);
                        String nextType = nextObject.getString(JSONAnalyzeBuilder.KEY_TYPE);
                        String nextPrice = nextObject.getString(JSONAnalyzeBuilder.KEY_PRICE);

                        if (!nextType.equals(type) || !nextPrice.equals(price))
                        {
                            message = message.concat(number).concat("x").concat(price).concat("<br>");
                        }
                        else
                        {
                            message = message.concat(number).concat(", ");
                        }
                    }
                }

                message = message.concat("<br>");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!receiveLo)
        {
            message = message.concat("<br>").concat(Formats.getStringHtmlText("", "Bỏ lô xiên vì quá giờ!", "#FF0000"));
        }

        return message;
    }

    public static String insertKeepMessageDelo(String date, String content, JSONArray oldContent, SharedPreferences saved) {
        if (content.contains("xien") || content.contains("xq") || content.contains("bc") || content.contains("bacang")
                || (content.contains("de") && content.contains("lo"))
                || (content.indexOf("de") != content.lastIndexOf("de"))
                || (content.indexOf("lo") != content.lastIndexOf("lo")))
        {
            return content;
        }

        String typeContent;

        if (content.startsWith("de"))
        {
            typeContent = "de";
        }
        else if (content.startsWith("lo"))
        {
            typeContent = "lo";
        }
        else
        {
            return "Lỗi định dạng.";
        }

        if (oldContent != null)
        {
            DeleteController.deleteKeepDeLo(oldContent, typeContent, date);
        }

        AnalyzeSMSNew analyzeSMS = new AnalyzeSMSNew();
        ArrayList<Analyze> listPoint = analyzeSMS.analyzeMessageWithOutResult(content, null);

        JSONArray jsonArray = new JSONArray();

        for (Analyze analyze : listPoint)
        {
            if (analyze.error)
            {
                return analyze.errorMessage;
            }
            else
            {
                String type = analyze.numberAndUnit.Type;
                String price = analyze.numberAndUnit.Price;
                ArrayList<String> numbers = analyze.numberAndUnit.Numbers;

                for (String number : numbers)
                {
                    final String[] arr = number.split(Constant.REGEX_SEPARATE_NUMBER_ANALYZE);

                    for (final String num : arr) {

                        KeepModel keepModel = new KeepModel();
                        keepModel.setName(Constant.ROOT_NAME);
                        keepModel.setPhone(Constant.UNKNOWN_PHONE_GIUDE);
                        keepModel.setDate(date);
                        keepModel.setNumber(num);
                        keepModel.setTime(Utils.getNewTimeFromDate(date));
                        keepModel.setType(type);
                        keepModel.setParenttype(type);
                        keepModel.setPrice(price);
                        keepModel.setMax(0);
                        keepModel.setActualcollect("0");

                        jsonArray.put(JSONAnalyzeBuilder.getObjectValue(type, num, price));

                        FollowDBHelper2.getDatabase().insertKeepDelo(keepModel);
                    }
                }
            }
        }

        if (typeContent.contains(LotoType.de.name()))
        {
            ConfigPreference.saveMessageGiuDe(saved, jsonArray.toString(), content);
        }
        else if (typeContent.contains(LotoType.lo.name()))
        {
            ConfigPreference.saveMessageGiuLo(saved, jsonArray.toString(), content);
        }

        return null;
    }
}
