package bingo.com.helperdb;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.bingo.analyze.Analyze;
import com.bingo.analyze.AnalyzeSMS;

import java.util.ArrayList;

import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.db.TempleMsgDatabase;
import bingo.com.model.ContactModel;
import bingo.com.utils.Constant;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.StatisticControlImport;

@Deprecated
public class TempleMessageHelper {

    private static TempleMsgDatabase db;

    public static TempleMsgDatabase instance() {
        return db;
    }

    public static void init(Context context) {
        synchronized (MessageDBHelper.class)
        {
            if (db == null)
            {
                db = new TempleMsgDatabase(context);
            }
        }
    }

    @Deprecated
    public static void importTempleMessage(String phone, long time) {
        //TODO that is redundant to analyze message. Only validate syntax message.
        Log.d("Templedatabase", "Starting import temple.");

        db.syncDatabase(phone, /*time*/0); //TODO this's mean remove config timeEnd => get all message today.

        Cursor cursor = db.getAlls(phone, time);

        if (cursor == null) return;

        ContactModel config = LoaderSQLite.getConfigParameter(phone);
        StatisticControlImport.setConfigForUser(config);

        try {

            cursor.moveToFirst();

            while (!cursor.isAfterLast())
            {
                AnalyzeSMS analyzeSMS = new AnalyzeSMS();
                ArrayList<Analyze> listPoint = analyzeSMS.bingoAnalyze(cursor.getString(cursor.getColumnIndex(TempleMsgDatabase.CONTENT)), "", "");

                String error = null;

                for (Analyze analyze : listPoint)
                {
                    if (!TextUtils.isEmpty(analyze.errorMessage))
                    {
                        String e = analyze.errorMessage;

                        if (e != null)
                        {
                            if (error == null)
                            {
                                error = "".concat(e);
                            }
                            else
                            {
                                error = error.concat(Constant.SEPARATE).concat(e);
                            }
                        }
                    }
                }

                if (error != null)
                {
                    int row = db.updateError(phone, cursor.getString(cursor.getColumnIndex(TempleMsgDatabase.CONTENT)), error);
                    Log.d("Templedatabase", "Updated error for row " + row);
                }

                cursor.moveToNext();
            }

            Log.d("Templedatabase", "Done import temple.");
        } finally {
            cursor.close();
        }

    }

    public static Cursor getMessageByPhoneWithDate(String phone, TypeMessage type, String date) {
        return db.getMessageByPhone(phone, type, date);
    }

    public static void syncToMainMessageDb(String phone, TypeMessage type, String date) {
        Cursor cursor = db.getMessageByPhone(phone, type, date);
        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            MessageDBHelper.importFromTempleDB(
                cursor.getString(cursor.getColumnIndex(TempleMsgDatabase.NAME)),
                cursor.getString(cursor.getColumnIndex(TempleMsgDatabase.PHONE)),
                cursor.getString(cursor.getColumnIndex(TempleMsgDatabase.CONTENT)),
                cursor.getString(cursor.getColumnIndex(TempleMsgDatabase.ERROR)),
                cursor.getString(cursor.getColumnIndex(TempleMsgDatabase.TIME)),
                cursor.getString(cursor.getColumnIndex(TempleMsgDatabase.DATE)),
                cursor.getString(cursor.getColumnIndex(TempleMsgDatabase.TYPE))
            );

            cursor.moveToNext();
        }


    }

    public static void deleteMessageWithId(String _id) {
        db.deleteMessageWithId(_id);
    }
}
