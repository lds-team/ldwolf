package bingo.com.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bingo.com.async.TaskUpdate;
import bingo.com.controller.wrapper.ObjectWrapper;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db2.FollowDBHelper2;
import bingo.com.model.ContactModel;
import bingo.com.model.ResultReturnModel;
import bingo.com.utils.Constant;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.Utils;
import bingo.com.utils.Validate;

public class UpdateController {

    public static void updateMessageHasAnalyze(Context context, String name, String phone, String time, String content, String typeMessage, ResultReturnModel[] results) {

        MessageDBHelper.updateMessageAdNormal(phone, time, content);

        DeleteController.deleteMessageHasAnalyze(name, phone, time, TypeMessage.convert(typeMessage));

        String canAnalyze = Validate.canAnalyze(context, content);

        if (canAnalyze == null)
        {
            LoaderSQLite.startForceAnalyzing(context, name, phone, content, (Long.parseLong(time) + 1), typeMessage, null);

            Toast.makeText(context, "Đã update tin nhắn thành công.", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(context, "Lỗi cú pháp.", Toast.LENGTH_LONG).show();
        }
    }

    public static void updateKeepValue(Context context, String name, String phone, String date, String[] typeChanged) {

        new TaskUpdate.TaskUpdateKeepValueS2(context, name, phone, date, typeChanged).execute();
    }

    public static void updateKeepXienValue(String date) {

        Cursor cursor = FollowDBHelper2.getDatabase().getAllXienBacang();
        Cursor cursorKeep = FollowDBHelper2.getDatabase().getAllKeepXienBacangFromOther();

        try {

            ContactModel config = ContactDBHelper.getFullContact(ContactDBHelper.getRandomPhone());
            ObjectWrapper wrapper = new ObjectWrapper(config, cursor, cursorKeep);

            List<ContentValues> valuesList = new ArrayList<>();
            Map<String, int[]> current = wrapper.getCurrent();
            wrapper.forceDeleteKeep();
            wrapper.putKeepAllCurrent();

            String time = Utils.getTimeWithCurrentConfigDate();

            for (String key : current.keySet())
            {
                String type = key.substring(0, key.indexOf(":"));
                String number = key.substring(key.indexOf(":") + 1);
                String phone = type.contains("xien") ? Constant.UNKNOWN_PHONE_GIUXIEN : Constant.UNKNOWN_PHONE_GIUBACANG;

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

                int keepAll = dou == null ? 0 : dou.intValue();

                if (keepAll != 0)
                {
                    ContentValues values = new ContentValues();
                    values.put("name", Constant.ROOT_NAME);
                    values.put("phone", phone);
                    values.put("number", number);
                    values.put("date", Utils.convertDate(time));
                    values.put("time", time);
                    values.put("type", type);
                    values.put("parenttype", type.contains("xien") ? "xien" : "bacang");
                    values.put("price", keepAll);
                    values.put("actualcollect", "0");

                    valuesList.add(values);
                }
            }

            FollowDBHelper2.getDatabase().deleteKeepValueByDate(Constant.UNKNOWN_PHONE_GIUXIEN, date);
            FollowDBHelper2.getDatabase().deleteKeepValueByDate(Constant.UNKNOWN_PHONE_GIUBACANG, date);

            FollowDBHelper2.getDatabase().updateArray(wrapper.buildToUpdate(date), null);
            FollowDBHelper2.getDatabase().insertKeepArray(valuesList, null);

            wrapper.clear();
        } finally {
            cursor.close();
            cursorKeep.close();
        }
    }
}
