package bingo.com.helperdb.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import bingo.com.enumtype.LotoType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.FollowDBHelper;
import bingo.com.model.FollowModel;
import bingo.com.utils.Constant;
import bingo.com.utils.Utils;

public class FollowDatabase extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE = "FollowDatabase";
    public static final String DETAIL_TABLE = "detail";

    public static final String DETAIL_NAME = "name";
    public static final String DETAIL_PHONE = "phone";
    public static final String DETAIL_NUMBER = "number";
    public static final String DETAIL_CONTENT = "content";
    public static final String DETAIL_UNIT = "unit";
    //if is Lo = diem, other  = nghin.
    public static final String DETAIL_PRICE = "price";
    //all is nghin.
    public static final String DETAIL_AMOUNT_POINT = "amountpoint";
    public static final String DETAIL_ACTUAL_COLLECT = "actualcollect";
    public static final String DETAIL_COUNT_WIN = "countwin";
    public static final String DETAIL_POINT_WIN = "pointwin";
    public static final String DETAIL_GUEST_WIN = "guestwin";
    public static final String DETAIL_PARENT_TYPE = "parenttype";
    public static final String DETAIL_TYPE = "type";
    public static final String DETAIL_DATE = "date";
    public static final String DETAIL_TIME = "time";
    public static final String DETAIL_HAS_DELIVERY = "delivery";
    public static final String DETAIL_DELIVERY_PHONE = "deliveryphone";
    public static final String DETAIL_DELIVERY_TIME = "deliverytime";
    public static final String DETAIL_WINSYNTAX= "winsyntax";
    public static final String DETAIL_MESSAGE_TYPE = "messagetype";
    public static final String DETAIL_SUM_RECEIVED = "sumreceived";
    public static final String DETAIL_SUM_SENT = "sumsent";
    public static final String DETAIL_SUM_KEEP = "sumkeep";
    public static final String DETAIL_SUM_OTHER = "sumother";

    public static final String NOT_EXIST = "-1";

    String CREATE_DETAIL_TABLE = "CREATE TABLE "
            + DETAIL_TABLE + "( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DETAIL_NAME + " TEXT, "
            + DETAIL_PHONE + " TEXT,"
            + DETAIL_NUMBER + " TEXT,"
            + DETAIL_CONTENT + " TEXT,"
            + DETAIL_UNIT + " TEXT,"
            + DETAIL_PRICE + " TEXT,"
            + DETAIL_AMOUNT_POINT + " TEXT,"
            + DETAIL_ACTUAL_COLLECT + " TEXT,"
            + DETAIL_COUNT_WIN + " INTEGER,"
            + DETAIL_POINT_WIN + " TEXT,"
            + DETAIL_GUEST_WIN + " TEXT,"
            + DETAIL_TYPE + " TEXT,"
            + DETAIL_PARENT_TYPE + " TEXT,"
            + DETAIL_DATE + " TEXT,"
            + DETAIL_TIME + " TEXT,"
            + DETAIL_HAS_DELIVERY + " TEXT,"
            + DETAIL_DELIVERY_PHONE + " TEXT,"
            + DETAIL_DELIVERY_TIME + " TEXT,"
            + DETAIL_WINSYNTAX + " TEXT,"
            + DETAIL_MESSAGE_TYPE + " TEXT,"
            + DETAIL_SUM_RECEIVED + " TEXT,"
            + DETAIL_SUM_SENT + " TEXT,"
            + DETAIL_SUM_KEEP + " TEXT,"
            + DETAIL_SUM_OTHER + " TEXT)";

    public FollowDatabase(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_DETAIL_TABLE);

        setWriteAheadLoggingEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS detail");
        onCreate(db);
    }

    public void insert(FollowModel model) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues values = getValueFromModel(model);

            insertToDetail(db, model, values);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void insertToDetail(SQLiteDatabase db, FollowModel model, ContentValues values) {

        //TODO That will error when two same type (ex: lo ... lo...) in one message. So, thinking about remove check validate, all is insert?
        //Now, testing all is insert.
        if (/*validateExist(db, model)*/false)
        {
            Log.d("FollowDatabase", "insertToDetail: " + "hasExist" + " num= " + values.getAsString(DETAIL_NUMBER));
            updateResult(db, model, values);
        }
        else
        {
            Log.d("FollowDatabase", "insertToDetail: " + "addNew" + " num= " + values.getAsString(DETAIL_NUMBER));
            db.insert(DETAIL_TABLE, null, values);
        }
    }

    private boolean validateExist(SQLiteDatabase db, FollowModel model) {
        Cursor cursor = db.query(DETAIL_TABLE, null,
                DETAIL_TIME + " =? AND " + DETAIL_NUMBER + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_PHONE + " =? AND " + DETAIL_MESSAGE_TYPE + " =?",
                new String[]{model.getTime(), model.getNumber(), model.getType(), model.getPhone(), model.getMessageType().name()},
                null, null, null);

        boolean result = (cursor == null || cursor.getCount() == 0);

        if (cursor != null) cursor.close();

        return !result;

    }

    private void updateResult(SQLiteDatabase db, FollowModel model, ContentValues values) {

        //Remove Flag delivery.
        values.remove(DETAIL_HAS_DELIVERY);

        db.update(DETAIL_TABLE, values,
                DETAIL_TIME + " =? AND " + DETAIL_NUMBER + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_PHONE + " =? ",
                new String[]{model.getTime(), model.getNumber(), model.getType(), model.getPhone()});
    }

    private ContentValues getValueFromModel(FollowModel model) {
        ContentValues values = new ContentValues();
        values.put(DETAIL_NAME, model.getName());
        values.put(DETAIL_PHONE, model.getPhone());
        values.put(DETAIL_NUMBER, model.getNumber());
        values.put(DETAIL_CONTENT, model.getContent());
        values.put(DETAIL_UNIT, model.getUnit());
        values.put(DETAIL_PRICE, model.getPrice());
        values.put(DETAIL_AMOUNT_POINT, model.getAmountPoint());
        values.put(DETAIL_ACTUAL_COLLECT, model.getActualCollect());
        values.put(DETAIL_COUNT_WIN, model.getCountWin());
        values.put(DETAIL_POINT_WIN, model.getPointWin());
        values.put(DETAIL_GUEST_WIN, model.getGuestWin());
        values.put(DETAIL_TYPE, model.getType());
        values.put(DETAIL_PARENT_TYPE, (model.getType().contains("xq") || model.getType().contains("xien")) ? "xien" : model.getType());
        values.put(DETAIL_TIME, model.getTime());
        values.put(DETAIL_DATE, Utils.convertDate(model.getTime()));
        values.put(DETAIL_HAS_DELIVERY, "0");
        values.put(DETAIL_DELIVERY_PHONE, NOT_EXIST);
        values.put(DETAIL_DELIVERY_TIME, NOT_EXIST);
        values.put(DETAIL_WINSYNTAX , model.getWinSyntax());
        values.put(DETAIL_MESSAGE_TYPE, model.getMessageType().name());

        //TODO that is hot fix for sum in sql error.
        int received = sumValue(
                FollowDBHelper.getSumReceivedValueOfNumber(model.getNumber(),
                        model.getType()),
                Double.parseDouble(model.getPrice()),
                model.getMessageType().name());

        int sent = sumValue(
                FollowDBHelper.getSumSentValueOfNumber(model.getNumber(),
                        model.getType()),
                Double.parseDouble(model.getPrice()),
                model.getMessageType().name());

        int keep = sumValue(
                FollowDBHelper.getSumKeepValueOfNumber(model.getNumber(),
                        model.getType()),
                Double.parseDouble(model.getPrice()),
                model.getMessageType().name());

        values.put(DETAIL_SUM_RECEIVED, received);
        values.put(DETAIL_SUM_SENT, sent);
        values.put(DETAIL_SUM_KEEP, keep);
        values.put(DETAIL_SUM_OTHER, received - sent - keep);

        return values;
    }

    private int sumValue(int recent, double newPrice, String messageType) {
        if (messageType.equals(TypeMessage.RECEIVED.name()))
        {
            return (int) (recent + newPrice);
        }
        else
        {
            return 0;
        }
    }

    public void insertKeepValue(FollowModel model) {
        ContentValues values = getValueFromModel(model);

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            String type = model.getType();

            String date = Utils.convertDate(model.getTime());

            //Not care Conflict because has delete all data of phone before.
            Log.d("FollowDatabase", "Insert Keep value: " + model.getNumber() + " price: " + model.getPrice() + " type: " + type + " parentType: " + values.getAsString(DETAIL_PARENT_TYPE));

            //TODO redundant when check exist becauseof it has been deleted before.
            //TODO may be error if has one case insert the same value with price = 0 (only use with keep xien)
            if (isExistKeepNumber(db, model.getNumber(), type, model.getPhone(), date))
            {
                db.update(DETAIL_TABLE, values,
                        DETAIL_NUMBER + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_PHONE + " =? AND " + DETAIL_DATE + " =?",
                        new String[]{model.getNumber(), type, TypeMessage.KEEP.name(), model.getPhone(), date});
            }
            else
            {
                db.insert(DETAIL_TABLE, null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private boolean isExistKeepNumber(SQLiteDatabase db, String number, String type, String phone, String date) {
        Cursor cursor = db.query(DETAIL_TABLE, null,
                DETAIL_NUMBER + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_PHONE + " =? AND " + DETAIL_DATE + " =?",
                new String[]{number, type, TypeMessage.KEEP.name(), phone, date},
                null, null, null);

        boolean result = (cursor == null || cursor.getCount() == 0);

        if (cursor != null) cursor.close();

        return !result;
    }

    public void  insertSuaDanValue(FollowModel model) {
        ContentValues values = getValueFromModel(model);

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            /*if (isExistNumber(db, model.getNumber(), model.getType()))
            {*/
                Log.d("FollowDatabase", "Update Keep value: " + model.getNumber() + " price: " + model.getPrice() + " for: " + model.getPhone());

                db.insert(DETAIL_TABLE, null, values);
            /*}*/

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private boolean isExistNumber(SQLiteDatabase db, String number, String type) {
        Cursor cursor = db.query(DETAIL_TABLE, null,
                DETAIL_NUMBER + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_MESSAGE_TYPE + " =?",
                new String[]{number, type, TypeMessage.RECEIVED.name()},
                null, null, null);

        boolean result = (cursor == null || cursor.getCount() == 0);

        if (cursor != null) cursor.close();

        return !result;
    }

    public Cursor getAllNumber(String phone, String... typeChanged) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_PHONE + " =? AND " + DETAIL_MESSAGE_TYPE + " =? " + orQuery(DETAIL_TYPE, typeChanged) +
                    " GROUP BY " + DETAIL_NUMBER + "," + DETAIL_TYPE;

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), phone, TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllNumberSortByPhone() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? " +
                    " ORDER BY " + DETAIL_MESSAGE_TYPE + "," + DETAIL_PHONE + "," + DETAIL_TIME + "," + DETAIL_TYPE + ",CAST(" + DETAIL_PRICE + " AS REAL) DESC";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllNumberSortByPhone(String phone) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_PHONE + " =?" +
                    " ORDER BY " + DETAIL_TIME + "," + DETAIL_MESSAGE_TYPE + "," + DETAIL_PRICE + " DESC";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), phone});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllNumberReceived() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =?" +
                    " ORDER BY " + DETAIL_TIME + "," + DETAIL_PRICE + " DESC";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllNumberSent() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =?" +
                    " ORDER BY " + DETAIL_PHONE + "," + DETAIL_TIME + "," + DETAIL_MESSAGE_TYPE + "," + DETAIL_PRICE + " DESC";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), TypeMessage.SENT.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllNumberKeep() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =?" +
                    " ORDER BY " + DETAIL_PHONE + "," + DETAIL_TIME + "," + DETAIL_MESSAGE_TYPE + "," + DETAIL_PRICE + " DESC";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), TypeMessage.KEEP.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    private String orQuery(String field, String[] list) {
        if (list == null)
        {
            return "";
        }

        String first = " AND ( " + field + " = '";
        String join = TextUtils.join("' OR " + field + " = '", list);
        String last = "') ";

        return first + join + last;
    }

    public Cursor getAllXien() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_PARENT_TYPE + " =? AND " + DETAIL_MESSAGE_TYPE + " =?";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), LotoType.xien.name(), TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllXien(String phone) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_PARENT_TYPE + " =? AND " + DETAIL_PHONE + " =? AND " + DETAIL_MESSAGE_TYPE + " =?";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), LotoType.xien.name(), phone, TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllNumber() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *, SUM(CAST(price AS REAL)) as sum" +
/*                    " (SELECT SUM(CAST(price AS REAL)) WHERE " + DETAIL_MESSAGE_TYPE + " ='RECEIVED'" + " ), " +
                    " (SELECT SUM(CAST(price AS REAL)) WHERE " + DETAIL_MESSAGE_TYPE + " ='SENT'" + " ) as deliver, " +
                    " (SELECT SUM(CAST(price AS REAL)) WHERE " + DETAIL_MESSAGE_TYPE + " ='KEEP'" + " ) as keep " +*/
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? " +
                    " GROUP BY " + DETAIL_NUMBER;

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllNumberGroupType() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? " +
                    " GROUP BY " + DETAIL_NUMBER + "," + DETAIL_TYPE;

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public int getSumValueByType(String numberType, TypeMessage type) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *, " +
                    " (SUM(CAST(price AS REAL))) as sum " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_PARENT_TYPE + " =?";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type.name(), numberType});

            cursor.moveToFirst();

            int result = 0;

            result = cursor.getInt(cursor.getColumnIndex("sum"));

            db.setTransactionSuccessful();

            cursor.close();

            return result;
        } finally {
            db.endTransaction();
        }
    }

    public int getSumValueByTypeOfPhone(String numberType, TypeMessage type, String phone) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *, " +
                    " (SUM(CAST(price AS REAL))) as sum " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_PARENT_TYPE + " =? AND " + DETAIL_PHONE + " =?";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type.name(), numberType, phone});

            cursor.moveToFirst();

            int result = 0;

            result = cursor.getInt(cursor.getColumnIndex("sum"));

            db.setTransactionSuccessful();

            cursor.close();

            return result;
        } finally {
            db.endTransaction();
        }
    }

    public int getSumValueOfNumberByType(String number, TypeMessage type) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *, " +
                    " (SUM(CAST(price AS REAL))) as sum " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_NUMBER + " =?";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type.name(), number});

            cursor.moveToFirst();

            int result = 0;

            result = cursor.getInt(cursor.getColumnIndex("sum"));

            db.setTransactionSuccessful();

            cursor.close();

            return result;
        } finally {
            db.endTransaction();
        }
    }

    public int getSumValueOfNumberByType(String number, TypeMessage type, String numberType) {
        SQLiteDatabase db = getReadableDatabase();

        /*try {
            db.beginTransaction();*/

            String query = "SELECT *, " +
                    " (SUM(CAST(price AS REAL))) as sum " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_NUMBER + " =? AND " + DETAIL_TYPE + " =?";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type.name(), number, numberType});

            cursor.moveToFirst();

            int result = 0;

            result = cursor.getInt(cursor.getColumnIndex("sum"));

            /*db.setTransactionSuccessful();*/

            cursor.close();

            return result;
        /*} finally {
            db.endTransaction();
        }*/
    }

    public int getSumValueOfNumberByTypeMF(String number, TypeMessage type, String numberType, String phone) {
        SQLiteDatabase db = getReadableDatabase();

        /*try {
            db.beginTransaction();*/

            String query = "SELECT _id, " +
                    " (SUM(CAST(price AS REAL))) as sum " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_NUMBER + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_PHONE + " =?";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type.name(), number, numberType, phone});

            cursor.moveToFirst();

            int result = 0;

            result = cursor.getInt(cursor.getColumnIndex("sum"));

            /*db.setTransactionSuccessful();*/

            cursor.close();

            return result;
        /*} finally {
            db.endTransaction();
        }*/
    }

    public int getSumValueOfNumberByTypeWithTimeMF(String number, TypeMessage type, String numberType, String phone, String time) {
        SQLiteDatabase db = getReadableDatabase();

        /*try {
            db.beginTransaction();*/

        String query = "SELECT _id, " +
                " (SUM(CAST(price AS REAL))) as sum " +
                " FROM " + DETAIL_TABLE +
                " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_NUMBER + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_PHONE + " =? AND " + DETAIL_TIME + " =?";

        Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type.name(), number, numberType, phone, time});

        cursor.moveToFirst();

        int result = 0;

        result = cursor.getInt(cursor.getColumnIndex("sum"));

            /*db.setTransactionSuccessful();*/

        cursor.close();

        return result;
        /*} finally {
            db.endTransaction();
        }*/
    }

    public int getSumGuestWinByType(String numberType, TypeMessage type) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *, " +
                    " (SUM(CAST(pointwin AS REAL))) as sum " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_PARENT_TYPE + " =?";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type.name(), numberType});

            cursor.moveToFirst();

            int result = 0;

            result = cursor.getInt(cursor.getColumnIndex("sum"));

            db.setTransactionSuccessful();

            cursor.close();

            return result;
        } finally {
            db.endTransaction();
        }
    }

    public int getSumGuestWinByType(String numberType, TypeMessage type, String phone) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *, " +
                    " (SUM(CAST(pointwin AS REAL))) as sum " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_PARENT_TYPE + " =? AND " + DETAIL_PHONE + " =?";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type.name(), numberType, phone});

            cursor.moveToFirst();

            int result = 0;

            result = cursor.getInt(cursor.getColumnIndex("sum"));

            db.setTransactionSuccessful();

            cursor.close();

            return result;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllNumberFilterByType(String type, TypeMessage sortType) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String sort;

            if (sortType == null)
            {
                sort = "RECEIVED";
            }
            else
            {
                sort = sortType.name();
            }

            String query = "SELECT *, " +
                    " (MAX(CAST(sumreceived AS REAL))) as RECEIVED, " +
                    " (MAX(CAST(sumsent AS REAL))) as SENT, " +
                    " (MAX(CAST(sumkeep AS REAL))) as KEEP, " +
                    " (MAX(CAST(sumother AS REAL))) as SMS " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_PARENT_TYPE + " =?" +
                    " GROUP BY " + DETAIL_NUMBER +
                    " ORDER BY " + sort + " DESC";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getSumAllParentType(TypeMessage type) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT _id," + DETAIL_PARENT_TYPE + "," +
                    " SUM(CAST(price AS REAL)) as sumpoint," +
                    " SUM(CAST(pointwin AS REAL)) as sumpointwin," +
                    " CAST(SUM(CAST(guestwin AS REAL)) - (SUM(CAST(amountpoint AS REAL))) AS REAL) as summoney" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =?" +
                    " GROUP BY " + DETAIL_PARENT_TYPE;

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getSumAllParentType(TypeMessage type, String parentType) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT _id," + DETAIL_PARENT_TYPE + "," +
                    " SUM(CAST(price AS REAL)) as sumpoint," +
                    " SUM(CAST(pointwin AS REAL)) as sumpointwin," +
                    " CAST(SUM(CAST(guestwin AS REAL)) - (SUM(CAST(amountpoint AS REAL))) AS REAL) as summoney" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_PARENT_TYPE + " =?" +
                    " GROUP BY " + DETAIL_PARENT_TYPE;

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type.name(), parentType});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getSumAllParentType(TypeMessage type, String parentType, String phone) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT _id," + DETAIL_PARENT_TYPE + "," +
                    " SUM(CAST(price AS REAL)) as sumpoint," +
                    " SUM(CAST(pointwin AS REAL)) as sumpointwin," +
                    " SUM(CAST(guestwin AS REAL)) as summoney" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_PARENT_TYPE + " =? AND " + DETAIL_PHONE + " =?" +
                    " GROUP BY " + DETAIL_PARENT_TYPE;

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), type.name(), parentType, phone});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public String getAllPhoneFromNumber(String number, String type) {
        SQLiteDatabase db = getReadableDatabase();

        try {

            db.beginTransaction();

            String query = "SELECT "+ DETAIL_NAME + ", " + DETAIL_PHONE + ", SUM(CAST(price as INTEGER)) as sum" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_NUMBER + " = ? AND " + DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_DATE + " =?" +
                    " GROUP BY " + DETAIL_PHONE;

            Cursor cursor = db.rawQuery(query, new String[]{number, TypeMessage.RECEIVED.name(), type, Utils.getCurrentDate()});

            String s = "";

            if (cursor != null && cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    s = s.concat(cursor.getString(cursor.getColumnIndex(DETAIL_NAME))).concat(" ").concat(cursor.getString(cursor.getColumnIndex(DETAIL_PHONE))).concat(": ").concat(cursor.getString(cursor.getColumnIndex("sum")));
                    if (cursor.moveToNext())
                    {
                        s = s.concat("\n");
                    }
                }
            }

            cursor.close();

            db.setTransactionSuccessful();

            return s;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getNumberByMessage(String phone, long time) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_TIME + " = ? AND " + DETAIL_PHONE + " =?" +
                    " ORDER BY " + DETAIL_TYPE + "," + DETAIL_NUMBER;

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(time), phone});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getNumberByMessageForBuild(String phone, long time) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *, SUM(CAST(price AS REAL)) as sum " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_TIME + " = ? AND " + DETAIL_PHONE + " =?" +
                    " GROUP BY " + DETAIL_NUMBER + ", " + DETAIL_TYPE +
                    " ORDER BY sum, " + DETAIL_TYPE + " DESC";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(time), phone});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllPhone() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT _id, name, phone, messagetype " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " !=?" +
                    " GROUP BY " + DETAIL_PHONE;

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), TypeMessage.KEEP.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllPhoneKhach() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT _id, name, phone " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_MESSAGE_TYPE + " =?" +
                    " GROUP BY " + DETAIL_PHONE;

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getSumAllParentTypeWithPhone(String phone, String type) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT _id, name, phone, " + DETAIL_PARENT_TYPE + "," +
                    " SUM(CAST(price AS REAL)) as sumpoint," +
                    " SUM(CAST(pointwin AS REAL)) as sumpointwin," +
                    " CAST(SUM(CAST(guestwin AS REAL)) - (SUM(CAST(amountpoint AS REAL))) AS REAL) as summoney" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_PHONE + " =? AND " + DETAIL_MESSAGE_TYPE + " =?" +
                    " GROUP BY " + DETAIL_PARENT_TYPE;

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), phone, type});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getSumAllParentTypeWithPhoneAndTime(String phone, String time) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT _id, name, phone, " + DETAIL_PARENT_TYPE + "," +
                    " SUM(CAST(price AS REAL)) as sumpoint," +
                    " SUM(CAST(pointwin AS REAL)) as sumpointwin," +
                    " SUM(CAST(amountpoint AS REAL)) as summoney," +
                    " SUM(CAST(guestwin AS REAL)) as sumwin" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_PHONE + " =? AND " + DETAIL_TIME + " =? AND " + DETAIL_MESSAGE_TYPE + " != ?" +
                    " GROUP BY " + DETAIL_PARENT_TYPE;

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), phone, time, TypeMessage.KEEP.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getSumSentParentTypeWithPhoneAndTime(String phone, String time) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT _id, name, phone, " + DETAIL_PARENT_TYPE + "," +
                    " SUM(CAST(price AS REAL)) as sumpoint," +
                    " SUM(CAST(pointwin AS REAL)) as sumpointwin," +
                    " SUM(CAST(amountpoint AS REAL)) as summoney," +
                    " SUM(CAST(guestwin AS REAL)) as sumwin" +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_DATE + " = ? AND " + DETAIL_PHONE + " =? AND " + DETAIL_TIME + " =? AND " + DETAIL_MESSAGE_TYPE + " == ?" +
                    " GROUP BY " + DETAIL_PARENT_TYPE;

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), phone, time, TypeMessage.SENT.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getNumberForXuatSo(LotoType type) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            Cursor cursor = db.rawQuery("SELECT *," +
                    " SUM(CAST( " + DETAIL_PRICE + " as REAL)) as sum," +
                    " (SELECT SUM(CAST(price AS REAL)) WHERE " + DETAIL_MESSAGE_TYPE + " ='KEEP'" + " ) as keep, " +
                    " (SELECT SUM(CAST(price AS REAL)) WHERE " + DETAIL_MESSAGE_TYPE + " ='SENT'" + " ) as deliver " +
                    " FROM " + DETAIL_TABLE +
                    " WHERE " + DETAIL_PARENT_TYPE + " =? AND " + DETAIL_DATE + " =? AND " + DETAIL_MESSAGE_TYPE + " =?" +
                    " GROUP BY " + DETAIL_NUMBER +
                    " ORDER BY sum DESC",
                    new String[]{type.name(), Utils.getCurrentDate(), TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public void deleteMessage(String phone, String time) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DETAIL_TABLE, DETAIL_TIME + " =? AND " + DETAIL_PHONE + " =?", new String[]{time, phone});

        reUpdateAllSumCache();
    }

    public void deleteByDate(String phone, String date) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DETAIL_TABLE, DETAIL_DATE+ " =? AND " + DETAIL_PHONE + " =?", new String[]{date, phone});

        reUpdateAllSumCache();
    }

    public void deleteAlls() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DETAIL_TABLE, DETAIL_DATE + " =? ", new String[]{Utils.getCurrentDate()});
    }

    public int deleteValue(String phone, TypeMessage typeMessage) {
        SQLiteDatabase db = getWritableDatabase();

        int row = db.delete(DETAIL_TABLE, DETAIL_DATE + " =? AND " +  DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_PHONE + " =?", new String[]{Utils.getCurrentDate(), typeMessage.name(), phone});

        reUpdateAllSumCache();

        return row;
    }

    public void deleteValue(String phone, String number, TypeMessage typeMessage, LotoType type) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DETAIL_TABLE, DETAIL_DATE+ " =? AND " +  DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_PHONE + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_NUMBER + " =?",
                new String[]{Utils.getCurrentDate(), typeMessage.name(), phone, type.name(), number});

        reUpdateAllSumCache();
    }

    public void reUpdateSumCache(String number, String numberType) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();

            int received = FollowDBHelper.getSumReceivedValueOfNumber(number, numberType);
            int sent = FollowDBHelper.getSumSentValueOfNumber(number, numberType);
            int keep = FollowDBHelper.getSumKeepValueOfNumber(number, numberType);

            values.put(DETAIL_SUM_RECEIVED, received);
            values.put(DETAIL_SUM_SENT, sent);
            values.put(DETAIL_SUM_KEEP, keep);
            values.put(DETAIL_SUM_OTHER, received - sent - keep);

            db.update(DETAIL_TABLE, values, DETAIL_NUMBER + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_DATE + " =? AND " + DETAIL_MESSAGE_TYPE + " =?", new String[]{number, numberType, Utils.getCurrentDate(), TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void reUpdateAllSumCache() {
        Cursor cursor = getAllNumberGroupType();

        if (cursor != null && cursor.moveToFirst())
        {
            while (!cursor.isAfterLast())
            {
                String number = cursor.getString(cursor.getColumnIndex(DETAIL_NUMBER));
                String type = cursor.getString(cursor.getColumnIndex(DETAIL_TYPE));

                reUpdateSumCache(number, type);
                cursor.moveToNext();
            }
        }
    }

    public Cursor calculateCongnoKhach(String phone) {

        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            Cursor cursor = db.rawQuery("SELECT *, " +
                            " SUM(CAST( " + DETAIL_PRICE + " as REAL)) as sumprice," +
                            " SUM(CAST( " + DETAIL_AMOUNT_POINT + " as REAL)) as sumhit," +
                            " SUM(CAST( " + DETAIL_POINT_WIN + " as REAL)) as sumpointwin," +
                            " SUM(CAST( " + DETAIL_GUEST_WIN + " as REAL)) as sumwin" +
                            " FROM " + DETAIL_TABLE +
                            " WHERE " + DETAIL_PHONE + " =? AND " + DETAIL_DATE + " =? AND " + DETAIL_MESSAGE_TYPE + " = ?" +
                            " GROUP BY " + DETAIL_PARENT_TYPE +
                            " ORDER BY " + DETAIL_PARENT_TYPE + " ASC",
                    new String[]{phone, Utils.getCurrentDate(), TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAllXien(String phone, TypeMessage typeMessage) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DETAIL_TABLE, DETAIL_MESSAGE_TYPE + " =? AND " + DETAIL_PHONE + " =? AND " + DETAIL_PARENT_TYPE + " =?", new String[]{typeMessage.name(), phone, LotoType.xien.name()});

        reUpdateAllSumCache();
    }

    public void deleteAllXien(String phone) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DETAIL_TABLE, DETAIL_PHONE + " =? AND " + DETAIL_PARENT_TYPE + " =?", new String[]{phone, LotoType.xien.name()});

        reUpdateAllSumCache();
    }

    public void deleteAllXien() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DETAIL_TABLE, DETAIL_PARENT_TYPE + " =?", new String[]{LotoType.xien.name()});

        reUpdateAllSumCache();
    }

    public String getContentGiusoTong(String type) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String content = "";

            Cursor cursor = db.rawQuery("SELECT * " +
                            " FROM " + DETAIL_TABLE +
                            " WHERE " + DETAIL_PHONE + " =? AND " + DETAIL_DATE + " =? AND " + DETAIL_MESSAGE_TYPE + " = ? AND " + DETAIL_TYPE + " =?",
                    new String[]{Constant.UNKNOWN_PHONE_GIUDE, Utils.getCurrentDate(), TypeMessage.KEEP.name(), type});

            if (cursor != null && cursor.moveToFirst())
            {
                content = cursor.getString(cursor.getColumnIndex(FollowDatabase.DETAIL_CONTENT));
            }

            cursor.close();

            db.setTransactionSuccessful();
            return content;
        } finally {
            db.endTransaction();
        }
    }

    public int updateResultForNumber(FollowModel part) {
        //TODO it will be error on case 2 number same type on one message.

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DETAIL_AMOUNT_POINT, part.getAmountPoint());
        values.put(DETAIL_ACTUAL_COLLECT, part.getActualCollect());
        values.put(DETAIL_COUNT_WIN, part.getCountWin());
        values.put(DETAIL_POINT_WIN, part.getPointWin());
        values.put(DETAIL_GUEST_WIN, part.getGuestWin());
        values.put(DETAIL_WINSYNTAX , part.getWinSyntax());

        return db.update(DETAIL_TABLE, values,
                DETAIL_PHONE + " =? AND " + DETAIL_NUMBER + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_TIME + " =? AND " + DETAIL_PRICE + " =?",
                new String[]{part.getPhone(), part.getNumber(), part.getType(), part.getTime(), part.getPrice()});
    }
}
