package bingo.com.helperdb;

import android.content.Context;
import android.database.Cursor;

import bingo.com.base.BaseDBHelper;
import bingo.com.controller.DeleteController;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.db.MessageDatabase;
import bingo.com.helperdb.db2.FollowDBHelper2;
import bingo.com.model.AnalyzeModel;
import bingo.com.model.ChooseMessage;
import bingo.com.model.ResultReturnModel;

public class MessageDBHelper extends BaseDBHelper{

    private static MessageDatabase db;

    public static MessageDatabase instance() {
        return db;
    }

    public static void init(Context context) {
        synchronized (MessageDBHelper.class)
        {
            if (db == null)
            {
                db = new MessageDatabase(context);
            }
        }
    }

    public static MessageDatabase getDb() {
        return db;
    }

    public static Cursor getMessageByPhoneWithTimeEnd(String phone, TypeMessage type, long timeEnd) {
        return db.getMessageByPhone(phone, type, timeEnd, true);
    }

    public static Cursor getMessageByPhoneWithDate(String phone, TypeMessage type, String date) {
        return db.getMessageByPhone(phone, type, date);
    }

    public static Cursor getMessageOnly(String phone, TypeMessage type, long time) {

        return db.getMessageByPhone(phone, type, time, false);
    }

    public static int updateSingleMessageAnalyze(boolean hasAnalyze, String phone, String time) {
        return db.updateSingleMessageAnalyze(hasAnalyze, phone, time);
    }

    public static Cursor getCursorStattisticNoAnalyze(String phone) {
        return db.getCursorStattisticNoAnalyze(phone);
    }

    public static Cursor getCursorStattisticNoAnalyze() {
        return db.getCursorStattisticNoAnalyze();
    }

    public static void updateAllAnalyzeStatus(boolean status) {
        db.updateAllAnalyzeStatus(status);
    }

    public static int updateContent(String phone, String time, String content) {
        int row = db.updateContentNornal(phone, time, content);
        return row;
    }

    public static void updateError(String phone, String time, String error) {
        db.updateError(phone, time, error);
    }

    public static void addMessage(ChooseMessage message) {
        db.addMessage(message);
    }

    public static void addMessageSent(ChooseMessage message) {
        db.addMessageSent(message);
    }

    public static void deleteMessage(String phone, String time) {
        db.deleteMessage(phone, time);
    }

    public static int updateMessageAdNormal(String phone, String time, String content) {
        int row = db.updateContentEditted(phone, time, content);

        return row;
    }

    public static void deleteDataUser(String phone, String date, boolean delUnAnalyzeData) {
        if (delUnAnalyzeData)
            db.deleteMessageByDay(phone, date);
        else
            db.deleteDataUser(phone, date);
    }

    public static void deleteAlls() {
        db.deleteAlls();
    }

    public static void importFromTempleDB(String name, String phone, String content, String error, String time, String date, String type) {
        db.addFromTemple(name, phone, content, error, time, date, type);
    }

    public static Cursor getAllMessage(String phone) {
        return db.getAllMessage(phone);
    }

    public static boolean hasResponse(String phone, String time) {
        return db.hasResponse(phone, time);
    }

    public static int updateResponseStatus(String phone, String time, boolean hasResponse) {
        return db.updateResponseStatus(phone, time, hasResponse);
    }

    public static int updateResponseStatusOnDay(String phone, String date, boolean hasResponse) {
        return db.updateResponseStatus(phone, date, hasResponse);
    }

    public static String getError(String phone, String time) {
        return db.getError(phone, time);
    }

    public static void loadMessageSync(String phone, TypeMessage typeMessage) {
        db.loadMessage(phone, typeMessage, 0);
    }

    public static int updateAnalyzeContent(String phone, String time, String analyzeContent) {
        return db.updateAnalyzeContent(phone, time, analyzeContent);
    }

    public static void updateWinNumberSyntax(String phone, String time, String winnumberSyntax) {
        db.updateWinNumber(phone, time, winnumberSyntax);
    }

    public static void updateWinMessage(String phone, String time, String messageType, String winnumberSyntax) {
        db.updateWinMessage(phone, time, messageType, winnumberSyntax);
    }

    public static void removeAllWinnumber(String phone, String time) {
        db.removeAllWinNumber(phone, time);
    }

    public static int updateAnalyzeResult(String phone, String time, AnalyzeModel obj) {
        return db.updateAnalyzeResult(phone, time, obj);
    }
}
