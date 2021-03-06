package bingo.com.helperdb;

import android.content.Context;
import android.database.Cursor;

import bingo.com.base.BaseDBHelper;
import bingo.com.enumtype.LotoType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.db.FollowDatabase;
import bingo.com.model.FollowModel;

public class FollowDBHelper extends BaseDBHelper {

    private static FollowDatabase database;

    public static FollowDatabase init(Context context) {

        synchronized (FollowDBHelper.class)
        {
            if (database == null)
            {
                database = new FollowDatabase(context);
            }
        }

        return database;
    }

    public static void closeDB() {

        synchronized (FollowDBHelper.class)
        {
            if(database != null )
            {
                database.close();
            }

            database = null;
        }
    }

    /**
     * Insert Result number of User to DataBase.
     *
     * @param model Interface Model data for insert.
     */
    public static void insertToFollowDatabase(FollowModel model, boolean manualAdd) {
        database.insert(model);
    }

    /*public static String syncDatabase(Context context, ResultReturnModel[] resultTodayandYesterday) {
        String error;

        Cursor cursor = MessageDBHelper.getCursorStattisticNoAnalyze("");

        Log.d("FollowDatabase", "syncDatabase: has: " + cursor.getCount());

        error = StatisticControlImport.doStatistic(getListStaticInfoModel(context, cursor), resultTodayandYesterday, null, true, false);

        cursor.close();

        return error;
    }*/

    /*public static List<StatisticInfoModel> getListStaticInfoModel(Context context, Cursor cursor) {
        return StatisticControlImport.getListStatisticInfoModelFromStatisticCursor(context, cursor);
    }*/

    public static Cursor getAllTypeNumber() {
        return database.getAllNumber();
    }

    public static Cursor getAllTypeNumber(String type, TypeMessage sortType) {
        return database.getAllNumberFilterByType(type, sortType);
    }

    public static Cursor getAllNumberFilterByPhone(String phone, String... typeChanged) {
        return database.getAllNumber(phone, typeChanged);
    }

    public static Cursor getAllNumberSortByPhone() {
        return database.getAllNumberSortByPhone();
    }

    public static Cursor getAllNumberSortByPhone(String phone) {
        return database.getAllNumberSortByPhone(phone);
    }

    public static Cursor getAllNumberReceived() {
        return database.getAllNumberReceived();
    }

    public static Cursor getAllNumberSent() {
        return database.getAllNumberSent();
    }

    public static Cursor getAllNumberKeep() {
        return database.getAllNumberKeep();
    }

    public static Cursor getAllXien() {
        return database.getAllXien();
    }

    public static Cursor getAllXien(String phone) {
        return database.getAllXien(phone);
    }

    public static Cursor getSumAllParentType(TypeMessage type) {
        return database.getSumAllParentType(type);
    }

    public static Cursor getSumAllParentType(TypeMessage type, String parentType) {
        return database.getSumAllParentType(type, parentType);
    }

    public static Cursor getSumAllParentType(TypeMessage type, String parentType, String phone) {
        return database.getSumAllParentType(type, parentType, phone);
    }

    public static Cursor getSumAllParentTypeWithPhoneAndType(String phone, String type) {
        return database.getSumAllParentTypeWithPhone(phone, type);
    }

    public static Cursor getSumAllParentTypeWithPhone(String phone, String time) {
        return database.getSumAllParentTypeWithPhoneAndTime(phone, time);
    }

    public static Cursor getSumSentParentTypeWithPhone(String phone, String time) {
        return database.getSumSentParentTypeWithPhoneAndTime(phone, time);
    }

    public static Cursor getAllPhone() {
        return database.getAllPhone();
    }

    public static Cursor getAllPhoneKhach() {
        return database.getAllPhoneKhach();
    }

    public static void deleteMessage(String phone, String time) {
        database.deleteMessage(phone, time);
    }

    public static void deleteByDate(String phone, String date) {
        database.deleteByDate(phone, date);
    }

    public static void deleteAlls() {
        database.deleteAlls();
    }

    public static void insertKeepValueNotConflict(FollowModel model) {
        database.insertKeepValue(model);
    }

    public static void insertSuaDanValue(FollowModel model) {
        database.insertSuaDanValue(model);
    }

    public static Cursor getNumberForXuatSo(LotoType type) {
        return database.getNumberForXuatSo(type);
    }

    public static int deleteKeepValue(String phone, TypeMessage typeMessage) {
        return database.deleteValue(phone, typeMessage);
    }

    public static void deleteKeepValue(String phone, String number, TypeMessage typeMessage, LotoType type) {
        database.deleteValue(phone, number, typeMessage, type);
    }

    public static int getSumReceivedValue(String numType) {
        return database.getSumValueByType(numType, TypeMessage.RECEIVED);
    }

    public static int getSumKeepValue(String numType) {
        return database.getSumValueByType(numType, TypeMessage.KEEP);
    }

    public static int getSumSentValue(String numType) {
        return database.getSumValueByType(numType, TypeMessage.SENT);
    }

    public static int getSumReceivedValueOfPhone(String numType, String phone) {
        return database.getSumValueByTypeOfPhone(numType, TypeMessage.RECEIVED, phone);
    }

    public static int getSumKeepValueOfPhone(String numType, String phone) {
        return database.getSumValueByTypeOfPhone(numType, TypeMessage.KEEP, phone);
    }

    public static int getSumSentValueOfPhone(String numType, String phone) {
        return database.getSumValueByTypeOfPhone(numType, TypeMessage.SENT, phone);
    }

    public static int getSumReceivedValueOfNumber(String num) {
        return database.getSumValueOfNumberByType(num, TypeMessage.RECEIVED);
    }

    public static int getSumKeepValueOfNumber(String num) {
        return database.getSumValueOfNumberByType(num, TypeMessage.KEEP);
    }

    public static int getSumSentValueOfNumber(String num) {
        return database.getSumValueOfNumberByType(num, TypeMessage.SENT);
    }

    public static int getSumReceivedValueOfNumber(String num, String numberType) {
        return database.getSumValueOfNumberByType(num, TypeMessage.RECEIVED, numberType);
    }

    public static int getSumKeepValueOfNumber(String num, String numberType) {
        return database.getSumValueOfNumberByType(num, TypeMessage.KEEP, numberType);
    }

    public static int getSumSentValueOfNumber(String num, String numberType) {
        return database.getSumValueOfNumberByType(num, TypeMessage.SENT, numberType);
    }

    public static int getSumGuestWinByType(String numType) {
        return database.getSumGuestWinByType(numType, TypeMessage.RECEIVED);
    }

    public static int getSumSentGuestWinByType(String numType) {
        return database.getSumGuestWinByType(numType, TypeMessage.SENT);
    }

    public static int getSumGuestWinByTypeOfPhone(String numType, String phone) {
        return database.getSumGuestWinByType(numType, TypeMessage.RECEIVED, phone);
    }

    public static int getSumSentGuestWinByTypeOfPhone(String numType, String phone) {
        return database.getSumGuestWinByType(numType, TypeMessage.SENT, phone);
    }

    public static Cursor calculateCongno(String phone) {
        return database.calculateCongnoKhach(phone);
    }

    public static int getSumSentValueOfNumberMF(String num, String numberType, String phone) {
        return database.getSumValueOfNumberByTypeMF(num, TypeMessage.SENT, numberType, phone);
    }

    public static int getSumKeepValueOfNumberMF(String num, String numberType, String phone) {
        return database.getSumValueOfNumberByTypeMF(num, TypeMessage.KEEP, numberType, phone);
    }

    public static int getSumReceivedValueOfNumberMF(String num, String numberType, String phone) {
        return database.getSumValueOfNumberByTypeMF(num, TypeMessage.RECEIVED, numberType, phone);
    }

    public static int getSumValueOfNumberByTypeWithTimeMF(String num, String numberType, String phone, String time) {
        return database.getSumValueOfNumberByTypeWithTimeMF(num, TypeMessage.RECEIVED, numberType, phone, time);
    }

    public static void deleteAllXien(String phone, TypeMessage typeMessage) {
        database.deleteAllXien(phone, typeMessage);
    }

    public static void deleteAllXien(String phone) {
        database.deleteAllXien(phone);
    }

    public static void deleteAllXien() {
        database.deleteAllXien();
    }

    public static Cursor getNumberFromMessage(String phone, long time) {
        return database.getNumberByMessage(phone, time);
    }

    public static Cursor getNumberMessageForBuild(String phone, long time) {
        return database.getNumberByMessageForBuild(phone, time);
    }

    public static String getAllPhoneFromNumber(String number, String type) {
        return database.getAllPhoneFromNumber(number, type);
    }

    public static void reUpdateSumCache() {
        database.reUpdateAllSumCache();
    }

    public static String getContentGiusoTongDe() {
        return database.getContentGiusoTong(LotoType.de.name());
    }

    public static String getContentGiusoTongLo() {
        return database.getContentGiusoTong(LotoType.lo.name());
    }

    public static int  updateResultForNumber(FollowModel part) {
        return database.updateResultForNumber(part);
    }
}
