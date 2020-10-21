package bingo.com.controller;

import android.content.SharedPreferences;
import android.util.Log;

import com.bingo.analyze.Analyze;
import com.bingo.analyze.AnalyzeSMSNew;
import com.bingo.analyze.NumberAndUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bingo.com.callbacks.ErrorListener;
import bingo.com.enumtype.LotoType;
import bingo.com.enumtype.TableType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db2.FollowDBHelper2;
import bingo.com.model.AnalyzeModel;
import bingo.com.model.ContactModel;
import bingo.com.model.KeepModel;
import bingo.com.model.NumberTypeModel;
import bingo.com.model.ReceivedModel;
import bingo.com.model.ResultReturnModel;
import bingo.com.pref.ConfigPreference;
import bingo.com.utils.Constant;
import bingo.com.utils.JSONAnalyzeBuilder;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.Utils;

public class InsertControllerBackUp {

    public static String analyzeSingleMessage(String name, String phone, String content, String time, TypeMessage type,
                                              ResultReturnModel[] results, boolean forceAdd,
                                              boolean receiveDe, boolean receiveLo, boolean noKeep, ErrorListener hasError) {
        ContactModel config = LoaderSQLite.getConfigParameter(phone);
        ConfigControl.setConfigForUser(config);

        /*String todayResult = results[0].getBody();
        String yesterdayResult = results[1].getBody();*/

        String error;

        AnalyzeModel analyzeModel = new AnalyzeModel();
        ReceivedModel receivedModel = new ReceivedModel();
        receivedModel.setPhone(phone);
        receivedModel.setTime(time);
        receivedModel.setTypemessage(type.name());
        receivedModel.setDate(Utils.convertDate(time));
        receivedModel.setDetail(new JSONArray());

        AnalyzeSMSNew analyzeSMS = new AnalyzeSMSNew();

        ArrayList<Analyze> listPoint = analyzeSMS.analyzeMessageWithOutResult(content, null);

        if (listPoint.size() == 0)
        {
            //Core analyze message hasn't detect error but can not analyze to number.
            MessageDBHelper.updateSingleMessageAnalyze(false, phone, time);
        }

        /*ArrayList<NumberTypeModel> listImportNum = new ArrayList<>();
        ArrayList<KeepModel> listImportKeep = new ArrayList<>();*/

        for (final Analyze analyze : listPoint)
        {
            if (analyze.error || analyze.numberAndUnit.Type.equalsIgnoreCase("Unknow"))
            {
                error = analyze.errorMessage;

                MessageDBHelper.updateError(phone, time, error);

                int row = MessageDBHelper.updateSingleMessageAnalyze(false, phone, time);
                Log.d("StatisticControlImport", "Update Analyze Status = false for " + phone + " in row " + row);

                if (hasError != null)
                {
                    hasError.errorNotify(true, forceAdd);
                    hasError = null;
                }

                return error;
            }
            else
            {
                MessageDBHelper.updateError(phone, time, "");

                //Update Status Analyze for each Message.
                int row = MessageDBHelper.updateSingleMessageAnalyze(true, phone, time);
                Log.d("StatisticControlImport", "Update Analyze Status = true for " + phone + " in row " + row);

                if (hasError != null)
                {
                    hasError.errorNotify(false, forceAdd);
                    hasError = null;
                }
            }

            importResultAnalyze(/*listImportNum, listImportKeep, */name, phone, content, time, type, analyze, config, analyzeModel, receivedModel, receiveDe, receiveLo, noKeep);
        }

        /*importData(type, listImportNum, listImportKeep);*/
        importCacheDatabase(phone, time, analyzeModel, receivedModel);

        /*listImportNum.clear();
        listImportKeep.clear();*/

        return null;
    }

    private static String importResultAnalyze(/*ArrayList<NumberTypeModel> listImportNum, ArrayList<KeepModel> listImportKeep, */
                                              String name, String phone, String content, String time, TypeMessage typeMessage,
                                              Analyze analyze, ContactModel config, AnalyzeModel analyzeModel, ReceivedModel receivedModel,
                                              boolean receiveDe, boolean receiveLo, boolean noKeep) {
        NumberAndUnit numberAndUnit = analyze.numberAndUnit;

        if (numberAndUnit == null)
        {
            return "Error";
        }

        String type = numberAndUnit.Type;
        //Check time to import number.
        if (type.contains("de"))
        {
            if (!receiveDe)
                return null;
        }
        else
        {
            if (!receiveLo)
                return null;
        }

        ArrayList<String> numberUnitList = numberAndUnit.Numbers;
        for (final String arrNum : numberUnitList)
        {
            if (type.contains("xq") || type.contains("xien"))
            {
                Log.d("StatisticControlImport", "Import num: ---" + arrNum + "---");

                int countNum = arrNum.split(Constant.REGEX_SEPARATE_NUMBER_ANALYZE).length;

                NumberTypeModel model = new NumberTypeModel();
                model.setNumber(arrNum);
                model.setDate(Utils.convertDate(time));
                model.setType("xien" + countNum);
                model.setPrice(Integer.parseInt(numberAndUnit.Price));
                model.setWin("0");

                FollowDBHelper2.getDatabase().insert(model, TableType.xien_table, typeMessage);
                /*listImportNum.add(model);*/

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
                JSONArray xienArr = receivedModel.getDetail();
                xienArr.put(JSONAnalyzeBuilder.getObjectValue(model.getType(), arrNum, numberAndUnit.Price));

                //Just keep each message come when not deliver.
                if (!noKeep)
                {
                    double hs = ConfigControl.getHeSo(config, model.getType());

                    int max = (int) config.getMax(LotoType.convert(model.getType()));

                    if (hs != 0d)
                    {
                        int keepPrice = (int) (Integer.parseInt(numberAndUnit.Price) * hs);

                        KeepModel keepModel = new KeepModel();
                        keepModel.setName(name);
                        keepModel.setPhone(phone);
                        keepModel.setDate(Utils.convertDate(time));
                        keepModel.setNumber(arrNum);
                        keepModel.setTime(time);
                        keepModel.setType("xien" + countNum);
                        keepModel.setParenttype("xien");
                        keepModel.setPrice(String.valueOf(keepPrice));
                        keepModel.setMax(max);
                        keepModel.setActualcollect("0");

                        FollowDBHelper2.getDatabase().insert(keepModel, TableType.keep_table, TypeMessage.KEEP);
                        /*listImportKeep.add(keepModel);*/
                    }
                }
            }
            else
            {
                final String[] arr = arrNum.split(Constant.REGEX_SEPARATE_NUMBER_ANALYZE);

                for (final String num : arr) {

                    if (num.isEmpty())
                    {
                        continue;
                    }

                    Log.d("StatisticControlImport", "Import num: ---" + num + "---");

                    NumberTypeModel model = new NumberTypeModel();
                    model.setNumber(num);
                    model.setDate(Utils.convertDate(time));
                    model.setType(type);
                    model.setPrice(Integer.parseInt(numberAndUnit.Price));
                    model.setWin("0");

                    FollowDBHelper2.getDatabase().insert(model, TableType.getTableName(type), typeMessage);
                    /*listImportNum.add(model);*/

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

                    JSONArray xienArr = receivedModel.getDetail();
                    xienArr.put(JSONAnalyzeBuilder.getObjectValue(type, num, numberAndUnit.Price));

                    //Just keep each message come when not deliver.
                    if (!noKeep)
                    {
                        double hs = ConfigControl.getHeSo(config, model.getType());

                        int max = (int) config.getMax(LotoType.convert(model.getType()));

                        if (hs != 0d)
                        {
                            int keepPrice = (int) (Integer.parseInt(numberAndUnit.Price) * hs);

                            KeepModel keepModel = new KeepModel();
                            keepModel.setName(name);
                            keepModel.setPhone(phone);
                            keepModel.setDate(Utils.convertDate(time));
                            keepModel.setNumber(num);
                            keepModel.setTime(time);
                            keepModel.setType(type);
                            keepModel.setParenttype(type);
                            keepModel.setPrice(String.valueOf(keepPrice));
                            keepModel.setMax(max);
                            keepModel.setActualcollect("0");

                            FollowDBHelper2.getDatabase().insert(keepModel, TableType.keep_table, TypeMessage.KEEP);
                            /*listImportKeep.add(keepModel);*/
                        }
                    }
                }
            }
        }

        return null;
    }

    private static void importData(TypeMessage type, ArrayList<NumberTypeModel> listNumber, ArrayList<KeepModel> listKeep) {
        ArrayList<Object> list = new ArrayList<>();
        list.addAll(listNumber);
        list.addAll(listKeep);

//        FollowDBHelper2.getDatabase().insertArray(type, list);
    }

    private static void importCacheDatabase(String phone, String time, AnalyzeModel analyzeModel, ReceivedModel receivedModel) {
        //Sort by type.
        JSONArray array = receivedModel.getDetail();
        array = Utils.sortByType(array);
        receivedModel.setDetail(array);

        MessageDBHelper.getDb().updateAnalyzeResult(phone, time, analyzeModel);
        MessageDBHelper.getDb().insertReceivedTable(receivedModel);

        /*String messageAnalyze = array.toString()
                .replaceAll("\\W", "")
                .replaceAll(JSONAnalyzeBuilder.KEY_TYPE, " ")
                .replaceAll(JSONAnalyzeBuilder.KEY_NUMBER, " ")
                .replaceAll(JSONAnalyzeBuilder.KEY_PRICE, "x").trim();*/

        String message = buildAnalyzeMessage(array);
        MessageDBHelper.updateAnalyzeContent(phone, time, message);

        if (array.toString().contains("xien"))
        {
            UpdateController.updateKeepXienValue(Utils.convertDate(time));
        }
    }

    private static String buildAnalyzeMessage(JSONArray array) {
        String message = "";
        String oldType = null;
        int size = array.length();

        try {
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
        } catch (JSONException e) {
            e.printStackTrace();
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
        else
        {
            typeContent = "lo";
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
//            ConfigPreference.saveMessageGiuDe(saved, jsonArray.toString());
        }
        else if (typeContent.contains(LotoType.lo.name()))
        {
//            ConfigPreference.saveMessageGiuLo(saved, jsonArray.toString());
        }

        return null;
    }
}
