package bingo.com.controller;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bingo.com.controller.wrapper.ObjectWrapper;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db2.FollowDBHelper2;
import bingo.com.utils.JSONAnalyzeBuilder;
import bingo.com.utils.Utils;

public class DeleteController {

    public static void deleteMessageHasAnalyze(String name, final String phone, final String time, final TypeMessage typeMessage) {
        String date = Utils.convertDate(time);
        Cursor cNumber = FollowDBHelper2.getDatabase().getAllNumber(date);
        Cursor cKeep = FollowDBHelper2.getDatabase().getAllKeepFromMessage(phone, time);
        JSONArray array = MessageDBHelper.getDb().getMapNumberFromMessage(phone, time);

        ObjectWrapper wrapper = new ObjectWrapper(null, cNumber, cKeep);
        wrapper.deleteNumber(array, typeMessage);
        wrapper.forceDeleteKeep();

        FollowDBHelper2.getDatabase().updateArray(wrapper.buildToUpdate(date), null);
        FollowDBHelper2.getDatabase().deleteAllBlankValue(date);
        MessageDBHelper.deleteMessage(phone, time);
        int row = FollowDBHelper2.getDatabase().deleteKeepValue(phone, time);

        /*if (row != 0)
        {
            //TODO may be slow down.
            UpdateController.updateKeepValue(name, phone, Utils.convertDate(time));
        }*/
    }

    public static void deleteDataUserHasAnalyze(final String phone, final String date, boolean deleteUnanalyzeData) {
        List<ContentValues> valuesList = new ArrayList<>();
        Cursor cursor = MessageDBHelper.getDb().getAllMessageFromPhone(phone, date);

        if (cursor.moveToFirst())
        {
            try {
                while (!cursor.isAfterLast())
                {
                    String typeMessage = cursor.getString(cursor.getColumnIndex("typemessage"));

                    String detail = cursor.getString(cursor.getColumnIndex("detail"));
                    JSONArray arrayAnalyze = new JSONArray(detail);

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
                            String price = object.getString(JSONAnalyzeBuilder.KEY_PRICE);

                            ContentValues values = new ContentValues();
                            if (typeMessage.equalsIgnoreCase("RECEIVED"))
                            {
                                values.put("number", number);
                                values.put("type", type);
                                values.put("received", price);
                            }
                            else
                            {
                                values.put("number", number);
                                values.put("type", type);
                                values.put("sent", price);
                            }

                            valuesList.add(values);
                        }
                    }

                    cursor.moveToNext();
                }
            } catch (JSONException e) {
                cursor.close();
            }
        }

        cursor.close();

        FollowDBHelper2.getDatabase().updateValueReceivedAndSent(valuesList, date);

        //Update keep.
        List<ContentValues> values = new ArrayList<>();
        Cursor cKeep = FollowDBHelper2.getDatabase().getAllNumberKeepFromPhone(phone, date);

        if (cKeep.moveToFirst())
        {
            while (!cKeep.isAfterLast())
            {
                String number = cKeep.getString(cKeep.getColumnIndex("number"));
                String type = cKeep.getString(cKeep.getColumnIndex("type"));
                int keep = cKeep.getInt(cKeep.getColumnIndex("sumkeep"));
                int keepNumber = cKeep.getInt(cKeep.getColumnIndex("keep"));

                ContentValues value = new ContentValues();
                value.put("number", number);
                value.put("type", type);
                value.put("date", date);
                value.put("keep", keepNumber - keep);

                values.add(value);

                cKeep.moveToNext();
            }
        }

        cKeep.close();

        FollowDBHelper2.getDatabase().updateKeepValueOnNumberTable(values);
        FollowDBHelper2.getDatabase().deleteAllBlankValue(date);
        MessageDBHelper.deleteDataUser(phone, date, deleteUnanalyzeData);
        FollowDBHelper2.getDatabase().deleteKeepValueByDate(phone, date);
    }

    public static void deleteKeepDeLo(JSONArray oldMessage, String type, String date) {
        int size = oldMessage.length();

        try {
            for (int i = 0; i < size; i++)
            {
                JSONObject object = oldMessage.getJSONObject(i);

                String number = object.getString(JSONAnalyzeBuilder.KEY_NUMBER);
                String price = object.getString(JSONAnalyzeBuilder.KEY_PRICE);

                FollowDBHelper2.getDatabase().deleteKeepNumberFromMessage(type.trim() + "_table", number, price, date);
            }

            FollowDBHelper2.getDatabase().deleteKeepDelo(type.trim(), date);
        } catch (JSONException e) {

        }
    }
}
