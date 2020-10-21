package bingo.com.helperdb.db2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import bingo.com.callbacks.UpdateSync;
import bingo.com.enumtype.TableType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.model.KeepModel;
import bingo.com.model.NumberTypeModel;
import bingo.com.utils.Constant;
import bingo.com.utils.Utils;

public class FollowDatabase2 extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE = "FollowDatabase";

    //------------------
    public static final String DE_TABLE = "de_table";

    public static final String DE_NUMBER = "number";//primary, not duplicate.
    public static final String DE_DATE = "date";
    public static final String DE_TYPE = "type";
    public static final String DE_RECEIVED = "received";
    public static final String DE_KEEP = "keep";
    public static final String DE_SENT = "sent";
    public static final String DE_WIN = "win";
    public static final String DE_COUNTWIN = "countwin";

    //----------------
    public static final String LO_TABLE = "lo_table";

    public static final String LO_NUMBER = "number";//primary, not duplicate.
    public static final String LO_DATE = "date";
    public static final String LO_TYPE = "type";
    public static final String LO_RECEIVED = "received";
    public static final String LO_KEEP = "keep";
    public static final String LO_SENT = "sent";
    public static final String LO_WIN = "win";
    public static final String LO_COUNTWIN = "countwin";

    //-----------------
    public static final String XIEN_TABLE = "xien_table";

    public static final String XIEN_NUMBER = "number";//primary, not duplicate.
    public static final String XIEN_DATE = "date";
    public static final String XIEN_TYPE = "type";
    public static final String XIEN_RECEIVED = "received";
    public static final String XIEN_KEEP = "keep";
    public static final String XIEN_SENT = "sent";
    public static final String XIEN_WIN = "win";
    public static final String XIEN_COUNTWIN = "countwin";
    
    //--------------
    public static final String BACANG_TABLE = "bacang_table";

    public static final String BACANG_NUMBER = "number";//primary, not duplicate.
    public static final String BACANG_DATE = "date";
    public static final String BACANG_TYPE = "type";
    public static final String BACANG_RECEIVED = "received";
    public static final String BACANG_KEEP = "keep";
    public static final String BACANG_SENT = "sent";
    public static final String BACANG_WIN = "win";
    public static final String BACANG_COUNTWIN = "countwin";

    //-----------------
    public static final String KEEP_TABLE = "keep_table";

    public static final String KEEP_NAME= "name";
    public static final String KEEP_PHONE = "phone";
    public static final String KEEP_DATE = "date";
    public static final String KEEP_NUMBER = "number";
    public static final String KEEP_TIME = "time";
    public static final String KEEP_TYPE = "type";
    public static final String KEEP_PARENTTYPE = "parenttype";
    public static final String KEEP_PRICE = "price";
    public static final String KEEP_ACTUAL = "actualcollect";


    String CREATE_DE_TABLE = "CREATE TABLE "
            + DE_TABLE + "( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DE_NUMBER + " TEXT, "
            + DE_DATE + " TEXT,"
            + DE_TYPE + " TEXT,"
            + DE_RECEIVED + " TEXT,"
            + DE_KEEP + " TEXT,"
            + DE_SENT + " TEXT,"
            + DE_WIN + " TEXT,"
            + DE_COUNTWIN + " TEXT)";

    String CREATE_LO_TABLE = "CREATE TABLE "
            + LO_TABLE + "( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + LO_NUMBER + " TEXT, "
            + LO_DATE + " TEXT,"
            + LO_TYPE + " TEXT,"
            + LO_RECEIVED + " TEXT,"
            + LO_KEEP + " TEXT,"
            + LO_SENT + " TEXT,"
            + LO_WIN + " TEXT,"
            + LO_COUNTWIN + " TEXT)";

    String CREATE_XIEN_TABLE = "CREATE TABLE "
            + XIEN_TABLE + "( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + XIEN_NUMBER + " TEXT, "
            + XIEN_DATE + " TEXT,"
            + XIEN_TYPE + " TEXT,"
            + XIEN_RECEIVED + " TEXT,"
            + XIEN_KEEP + " TEXT,"
            + XIEN_SENT + " TEXT,"
            + XIEN_WIN + " TEXT,"
            + XIEN_COUNTWIN + " TEXT)";

    String CREATE_BACANG_TABLE = "CREATE TABLE "
            + BACANG_TABLE + "( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + BACANG_NUMBER + " TEXT, "
            + BACANG_DATE + " TEXT,"
            + BACANG_TYPE + " TEXT,"
            + BACANG_RECEIVED + " TEXT,"
            + BACANG_KEEP + " TEXT,"
            + BACANG_SENT + " TEXT,"
            + BACANG_WIN + " TEXT,"
            + BACANG_COUNTWIN + " TEXT)";

    String CREATE_KEEP_TABLE = "CREATE TABLE "
            + KEEP_TABLE + "( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEEP_NAME + " TEXT, "
            + KEEP_PHONE + " TEXT,"
            + KEEP_DATE + " TEXT,"
            + KEEP_TIME + " TEXT,"
            + KEEP_NUMBER + " TEXT,"
            + KEEP_TYPE + " TEXT,"
            + KEEP_PARENTTYPE + " TEXT,"
            + KEEP_PRICE + " TEXT,"
            + KEEP_ACTUAL + " TEXT)";

    public FollowDatabase2(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_DE_TABLE);
        db.execSQL(CREATE_LO_TABLE);
        db.execSQL(CREATE_XIEN_TABLE);
        db.execSQL(CREATE_BACANG_TABLE);
        db.execSQL(CREATE_KEEP_TABLE);

        setWriteAheadLoggingEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS de_table");
        db.execSQL("DROP TABLE IF EXISTS lo_table");
        db.execSQL("DROP TABLE IF EXISTS xien_table");
        db.execSQL("DROP TABLE IF EXISTS bacang_table");
        db.execSQL("DROP TABLE IF EXISTS keep_table");
        onCreate(db);
    }

    public void insert(Object model, TableType table, TypeMessage typeMessage) {
        insert(model, table, typeMessage, null);
    }

    public void insert(Object model, TableType table, TypeMessage typeMessage, UpdateSync sync) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            if (model instanceof KeepModel)
            {
                ContentValues values = getValueFromKeepModel((KeepModel) model);
                insertKeep(db, (KeepModel) model, values);
            }
            else
            {
                if (typeMessage == TypeMessage.RECEIVED)
                {
                    ContentValues values = getValueFromNumberModel((NumberTypeModel) model);
                    insertNumber(db, table.name(), (NumberTypeModel) model, values);
                }
                else if (typeMessage == TypeMessage.SENT)
                {
                    insertSentNumber(db, table.name(), (NumberTypeModel) model);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();

            if (sync != null)
            {
                sync.onUpdate();
            }
        }
    }

    public boolean insertArray(List<ContentValues> valuesList) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            for (ContentValues values : valuesList)
            {
                String type = values.getAsString("type");
                type = type.contains("xien") ? "xien" : type;
                String table = type + "_table";

                db.insert(table, null, values);
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e){
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean updateArray(List<ContentValues> valuesList, UpdateSync sync) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            for (ContentValues values : valuesList)
            {
                String type = values.getAsString("type");
                type = type.contains("xien") ? "xien" : type;
                String table = type + "_table";
                String number = values.getAsString("number");
                String date = values.getAsString("date");

                db.update(table, values, "date = ? AND number = ?", new String[]{date, number});

                if (sync != null)
                {
                    sync.onUpdate();
                }
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e){
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean insertKeepArray(List<ContentValues> valuesList, UpdateSync sync) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            for (ContentValues values : valuesList)
            {
                int rowUpdated = 0;

                String phone = values.getAsString("phone");
                if (phone.equals(Constant.UNKNOWN_PHONE_GIUXIEN) || phone.equals(Constant.UNKNOWN_PHONE_GIUBACANG))
                {
                    String number = values.getAsString("number");
                    String type = values.getAsString("type");
                    String date = values.getAsString("date");

                    rowUpdated = db.update(KEEP_TABLE, values, "number = ? AND type = ? AND date = ? AND phone = ?", new String[]{number, type, date, phone});
                }

                if (rowUpdated == 0)
                {
                    db.insert(KEEP_TABLE, null, values);
                }

                if (sync != null)
                {
                    sync.onUpdate();
                }
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e){
            return false;
        } finally {
            db.endTransaction();
        }
    }

    /*public void insertRun(RunnableDb runnable) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            runnable.run(db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }*/

    private void insertNumber(SQLiteDatabase db, String table, NumberTypeModel model, ContentValues values) {

        Cursor cursor = db.query(table, null,
                "number =? AND date =? ",
                new String[]{model.getNumber(), model.getDate()},
                null, null, null);

        boolean result = (cursor == null || cursor.getCount() == 0);

        if (!result)
        {
            cursor.moveToFirst();
            int sum = cursor.getInt(cursor.getColumnIndex("received"));

            sum = sum + model.getPrice();

            int keep = cursor.getInt(cursor.getColumnIndex("keep"));
            int sent = cursor.getInt(cursor.getColumnIndex("sent"));

            values.put("received", sum);
            values.put("sent", sent);
            values.put("keep", keep);

            Log.d("FollowDatabase", "insertNumber: " + "hasExist" + " num= " + values.getAsString("number"));
            db.update(table, values,
                    "number =? AND date =? ",
                    new String[]{model.getNumber(), model.getDate()});
        }
        else
        {
            Log.d("FollowDatabase", "insertNumber: " + "addNew" + " num= " + values.getAsString("number"));
            values.put("sent", 0);
            values.put("keep", 0);
            db.insert(table, null, values);
        }

        if (cursor != null) cursor.close();
    }

    private void insertSentNumber(SQLiteDatabase db, String table, NumberTypeModel model) {
        Cursor cursor = db.query(table, null,
                "number =? AND date =? ",
                new String[]{model.getNumber(), model.getDate()},
                null, null, null);

        boolean result = (cursor == null || cursor.getCount() == 0);

        ContentValues values = new ContentValues();

        if (!result)
        {
            cursor.moveToFirst();

            int sent = cursor.getInt(cursor.getColumnIndex("sent"));
            sent += model.getPrice();
            values.put("sent", sent);

            Log.d("FollowDatabase", "insertNumber: " + "hasExist" + " num= " + model.getNumber());
            db.update(table, values,
                    "number =? AND date =? ",
                    new String[]{model.getNumber(), model.getDate()});
        }
        else
        {
            Log.d("FollowDatabase", "insertNumber: " + "addNew" + " num= " + model.getNumber());
            values.put("sent", model.getPrice());
            db.insert(table, null, values);
        }

        if (cursor != null) cursor.close();
    }

    public void insertKeep(SQLiteDatabase db, KeepModel model, ContentValues values) {

        String query = "SELECT " +
                " (SELECT SUM(price) FROM keep_table WHERE " + KEEP_DATE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =?) AS sumall, " +
//                " (SELECT SUM(price) FROM keep_table WHERE " + KEEP_DATE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =? AND " + KEEP_PHONE + " =? AND " + KEEP_TIME + " =?) AS sumonce, " +
                " (SELECT SUM(price) FROM keep_table WHERE " + KEEP_DATE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =? AND " + KEEP_PHONE + " =?) AS sum " +
                " FROM keep_table";

        Cursor sum = db.rawQuery(query, new String[]{model.getDate(), model.getNumber(), model.getType(),
//                model.getDate(), model.getNumber(), model.getType(), model.getPhone(), model.getTime(),
                model.getDate(), model.getNumber(), model.getType(), model.getPhone()});

        int currentPrice = 0;
        int sumall = 0;
//        int sumonce = 0;

        if (sum != null && sum.moveToFirst())
        {
            currentPrice = sum.getInt(sum.getColumnIndex("sum"));
            sumall = sum.getInt(sum.getColumnIndex("sumall"));
//            sumonce = sum.getInt(sum.getColumnIndex("sumonce"));
        }


        if (sum != null) sum.close();

        //Calculate keep price.
        int price = Integer.parseInt(model.getPrice());

        int max = model.getMax();

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
                return;
            }
        }

        //Update keep table.
        /*Cursor cursor = db.query(KEEP_TABLE, null,
                KEEP_PHONE + " =? AND " + KEEP_DATE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =? AND " + KEEP_TIME + " =?",
                new String[]{model.getPhone(), model.getDate(), model.getNumber(), model.getType(), model.getTime()},
                null, null, null);

        boolean result = (cursor == null || cursor.getCount() == 0);
        if (cursor != null) cursor.close();

        if (!result)
        {
            values.put(KEEP_PRICE, sumonce + price);

            Log.d("FollowDatabase", "updateKeep: " + "hasExist" + " phone= " + values.getAsString("phone"));
            db.update(KEEP_TABLE, values,
                    KEEP_PHONE + " =? AND " + KEEP_TIME + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =?",
                    new String[]{model.getPhone(), model.getTime(), model.getNumber(), model.getType()});
        }
        else
        {*/
            values.put(KEEP_PRICE, price);

            Log.d("FollowDatabase", "insertKeep: " + "addNew" + " phone= " + values.getAsString("phone"));
            db.insert(KEEP_TABLE, null, values);
        /*}*/

        //Update sum at table detail.
        ContentValues valuesSum = new ContentValues();
        valuesSum.put("keep", sumall + price);

        String table = model.getParenttype() + "_table";
        db.update(table, valuesSum, "number = ? AND date =?", new String[]{model.getNumber(), model.getDate()});
    }

    public void updateKeepValueAfterDeleteAll(List<ContentValues> valuesList, UpdateSync sync) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            for (ContentValues values : valuesList)
            {
                String date = values.getAsString("date");
                String number = values.getAsString("number");
                String type = values.getAsString("type");
                String phone = values.getAsString("phone");
                String parenttype = values.getAsString("parenttype");
                int price = values.getAsInteger("price");
                int max = values.getAsInteger("max");

                String query = "SELECT " +
                        " (SELECT SUM(price) FROM keep_table WHERE " + KEEP_DATE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =?) AS sumall, " +
                        " (SELECT SUM(price) FROM keep_table WHERE " + KEEP_DATE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =? AND " + KEEP_PHONE + " =?) AS sum " +
                        " FROM keep_table";

                Cursor sum = db.rawQuery(query, new String[]{date, number, type,
                        date, number, type, phone});

                int currentPrice = 0;
                int sumall = 0;

                if (sum != null && sum.moveToFirst())
                {
                    currentPrice = sum.getInt(sum.getColumnIndex("sum"));
                    sumall = sum.getInt(sum.getColumnIndex("sumall"));
                }


                if (sum != null) sum.close();

                //Calculate keep price.
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
                        return;
                    }
                }

                values.put(KEEP_PRICE, price);
                values.remove("max");

                Log.d("FollowDatabase", "insertKeep: " + "addNew" + " phone= " + values.getAsString("phone"));
                db.insert(KEEP_TABLE, null, values);

                //Update sum at table detail.
                ContentValues valuesSum = new ContentValues();
                valuesSum.put("keep", sumall + price);

                String table = parenttype + "_table";
                db.update(table, valuesSum, "number = ? AND date =?", new String[]{number, date});

                if (sync != null)
                {
                    sync.onUpdate();
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    //TODO Hotfix.
    @Deprecated
    public void insertKeepXienBaCang(KeepModel model) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT SUM(price) as sum FROM keep_table WHERE " + KEEP_DATE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =? ";

            Cursor sum = db.rawQuery(query, new String[]{model.getDate(), model.getNumber(), model.getType()});

            int currentPrice = 0;

            if (sum != null && sum.moveToFirst())
            {
                currentPrice = sum.getInt(sum.getColumnIndex("sum"));
            }

            if (sum != null) sum.close();

            //Update keep table.
            Cursor cursor = db.query(KEEP_TABLE, null,
                    KEEP_PHONE + " =? AND " + KEEP_DATE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =? ",
                    new String[]{model.getPhone(), model.getDate(), model.getNumber(), model.getType()},
                    null, null, null);

            boolean result = (cursor == null || cursor.getCount() == 0);

            ContentValues values = getValueFromKeepModel(model);

            int price = Integer.parseInt(model.getPrice());

            int oldPrice = 0;

            if (!result)
            {
                cursor.moveToFirst();
                oldPrice = cursor.getInt(cursor.getColumnIndex(KEEP_PRICE));

                Log.d("FollowDatabase", "updateKeep: " + "hasExist" + " phone= " + values.getAsString("phone"));
                db.update(KEEP_TABLE, values,
                        KEEP_PHONE + " =? AND " + KEEP_DATE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =?",
                        new String[]{model.getPhone(), model.getDate(), model.getNumber(), model.getType()});
            }
            else
            {
                if (price != 0)
                {
                    Log.d("FollowDatabase", "insertKeep: " + "addNew" + " phone= " + values.getAsString("phone"));
                    db.insert(KEEP_TABLE, null, values);
                }
                else
                {
                    return;
                }
            }

            if (cursor != null) cursor.close();

            //Update sum at table detail.
            ContentValues valuesSum = new ContentValues();
            valuesSum.put("keep", currentPrice - oldPrice + price);

            String table = model.getParenttype() + "_table";
            db.update(table, valuesSum, "number = ? AND date =?", new String[]{model.getNumber(), model.getDate()});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void insertKeepDelo(KeepModel model) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT SUM(price) as sum, COUNT(*) as count FROM keep_table WHERE " + KEEP_DATE + " =? AND " + KEEP_PHONE + " =? AND " + KEEP_TYPE + " =? AND " + KEEP_NUMBER + " =?";

            Cursor sum = db.rawQuery(query, new String[]{model.getDate(), Constant.UNKNOWN_PHONE_GIUDE, model.getType(), model.getNumber()});

            int currentPrice = 0;

            int size = 0;

            if (sum != null && sum.moveToFirst())
            {
                currentPrice = sum.getInt(sum.getColumnIndex("sum"));
                size =  sum.getInt(sum.getColumnIndex("count"));
            }

            if (sum != null) sum.close();

            ContentValues values = getValueFromKeepModel(model);

            int price = Integer.parseInt(model.getPrice());

            if (size == 1)
            {
                values.put(KEEP_PRICE, currentPrice + price);

                Log.d("FollowDatabase", "updateKeep: " + "hasExist" + " phone= " + values.getAsString("phone"));
                db.update(KEEP_TABLE, values,
                        KEEP_PHONE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =?",
                        new String[]{Constant.UNKNOWN_PHONE_GIUDE, model.getNumber(), model.getType()});
            }
            else
            {
                Log.d("FollowDatabase", "insertKeep: " + "addNew" + " phone= " + values.getAsString("phone"));
                db.insert(KEEP_TABLE, null, values);
            }

            //
            String qSumAll = "SELECT SUM(price) as sum FROM keep_table WHERE " + KEEP_DATE + " =? AND " + KEEP_TYPE + " =? AND " + KEEP_NUMBER + " =?";

            Cursor cAll = db.rawQuery(qSumAll, new String[]{model.getDate(), model.getType(), model.getNumber()});

            int sumAll = 0;

            if (cAll.moveToFirst())
            {
                sumAll = cAll.getInt(cAll.getColumnIndex("sum"));
            }

            cAll.close();

            //Update sum at table detail.
            ContentValues valuesSum = new ContentValues();
            valuesSum.put("keep", sumAll);

            String table = model.getParenttype() + "_table";
            int row = db.update(table, valuesSum, "number = ? AND date =?", new String[]{model.getNumber(), model.getDate()});

            if (row == 0)
            {
                //Case keep number is not received.
                valuesSum.put(XIEN_NUMBER, model.getNumber());
                valuesSum.put(XIEN_DATE, model.getDate());
                valuesSum.put(XIEN_TYPE, model.getType());
                valuesSum.put(XIEN_RECEIVED, "0");
                valuesSum.put(XIEN_SENT, "0");
                valuesSum.put(XIEN_WIN, "0");
                valuesSum.put(XIEN_COUNTWIN, "0");
                db.insert(table, null, valuesSum);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public int deleteKeepDelo(String type, String date) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            int row = db.delete(KEEP_TABLE, "phone = ? AND date = ? AND type = ?", new String[]{Constant.UNKNOWN_PHONE_GIUDE, date, type});

            db.setTransactionSuccessful();

            return row;
        } finally {
            db.endTransaction();
        }
    }

    public boolean deleteKeepNumberFromMessage(String table, String number, String price, String date) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT * " +
                    " FROM " + table +
                    " WHERE date = ? AND number = ?";

            Cursor cursor = db.rawQuery(query, new String[]{date, number});

            boolean hasNumber;

            if (cursor.moveToFirst())
            {
                hasNumber = true;

                int curKeep = cursor.getInt(cursor.getColumnIndex("keep"));

                curKeep = curKeep - Integer.parseInt(price);

                int curReceived = cursor.getInt(cursor.getColumnIndex("received"));
                int curSent = cursor.getInt(cursor.getColumnIndex("sent"));

                if (curKeep == 0 && curReceived == 0 && curSent == 0)
                {
                    db.delete(table, "date = ? AND number = ?", new String[]{date, number});
                }
                else
                {
                    ContentValues values = new ContentValues();
                    values.put(XIEN_KEEP, curKeep);

                    db.update(table, values, "date = ? AND number = ?", new String[]{date, number});
                }
            }
            else
            {
                hasNumber = false;
            }

            cursor.close();

            db.setTransactionSuccessful();

            return hasNumber;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllXienBacang() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT * " +
                    " FROM " + XIEN_TABLE +
                    " WHERE date = ? " +
                    " UNION ALL " +
                    " SELECT * " +
                    " FROM " + BACANG_TABLE +
                    " WHERE date = ? ";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), Utils.getCurrentDate()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllKeepXienBacangFromOther() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT _id, number, type, SUM(price) as sumkeep " +
                    " FROM " + KEEP_TABLE +
                    " WHERE date = ? AND (parenttype = ? OR parenttype = ?) AND (phone = ? OR phone = ?) " +
                    " GROUP BY number, type";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), "xien", "bacang", Constant.UNKNOWN_PHONE_GIUXIEN, Constant.UNKNOWN_PHONE_GIUBACANG});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    private ContentValues getValueFromNumberModel(NumberTypeModel model) {
        ContentValues values = new ContentValues();
        values.put(XIEN_NUMBER, model.getNumber());
        values.put(XIEN_DATE, model.getDate());
        values.put(XIEN_TYPE, model.getType());
        values.put(XIEN_RECEIVED, model.getPrice());
        values.put(XIEN_WIN, model.getWin());
        values.put(XIEN_COUNTWIN, model.getCountwin());

        return values;
    }

    public ContentValues getValueFromKeepModel(KeepModel model) {
        ContentValues values = new ContentValues();
        values.put(KEEP_NAME, model.getName());
        values.put(KEEP_PHONE, model.getPhone());
        values.put(KEEP_NUMBER, model.getNumber());
        values.put(KEEP_DATE, model.getDate());
        values.put(KEEP_TIME, model.getTime());
        values.put(KEEP_TYPE, model.getType());
        values.put(KEEP_PARENTTYPE, model.getParenttype());
        values.put(KEEP_PRICE, model.getPrice());
        values.put(KEEP_ACTUAL, model.getActualcollect());

        return values;
    }

    public Cursor getAllNumberFilterByType(String table, TypeMessage sortType, String... type) {
        String customAppendQuery = "";

        if (type != null && type.length > 0)
        {
            customAppendQuery = Utils.orQuery("type", type);
        }

        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String sort;

            if (sortType == null)
            {
                sort = "sort1";
            }
            else if (sortType == TypeMessage.RECEIVED)
            {
                sort = "sort1";
            }
            else if (sortType == TypeMessage.SENT)
            {
                sort = "sort2";
            }
            else if (sortType == TypeMessage.KEEP)
            {
                sort = "sort3";
            }
            else
            {
                sort = "sms";
            }

            String query = "SELECT *, CAST(received as REAL) as sort1, CAST(sent as REAL) as sort2, CAST(keep as REAL) as sort3," +
                    " CAST(received AS REAL) - CAST(sent AS REAL) - CAST(keep AS REAL) as sms " +
                    " FROM " + table +
                    " WHERE date = ? " + customAppendQuery +
                    " ORDER BY " + sort + " DESC";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllNumber(String date) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT * " +
                    " FROM " + DE_TABLE +
                    " WHERE date = ? " +
                    " UNION ALL " +
                    " SELECT * " +
                    " FROM " + LO_TABLE +
                    " WHERE date = ? " +
                    " UNION ALL " +
                    " SELECT * " +
                    " FROM " + XIEN_TABLE +
                    " WHERE date = ? " +
                    " UNION ALL " +
                    " SELECT * " +
                    " FROM " + BACANG_TABLE +
                    " WHERE date = ? ";

            Cursor cursor = db.rawQuery(query, new String[]{date, date, date, date});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getKeepFromNumber(String phone, String date) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *, SUM(price) as sumkeep " +
                    " FROM " + KEEP_TABLE +
                    " WHERE date = ? AND phone = ? GROUP BY number, type";

            Cursor cursor = db.rawQuery(query, new String[]{date, phone});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public void deleteNumberFromMessage(String table, String number, String price, String date, String time, String type) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            String qKeep = "SELECT _id, SUM(price) as sumprice" +
                    " FROM " + KEEP_TABLE +
                    " WHERE time = ? AND number = ? AND type = ?";

            Cursor cKeep = db.rawQuery(qKeep, new String[]{time, number, type});

            int keep = 0;

            if (cKeep.moveToFirst())
            {
                keep = cKeep.getInt(cKeep.getColumnIndex("sumprice"));
            }

            cKeep.close();

            String query = "SELECT * " +
                    " FROM " + table +
                    " WHERE date = ? AND number = ?";

            Cursor cursor = db.rawQuery(query, new String[]{date, number});

            if (cursor.moveToFirst())
            {
                int curPrice = cursor.getInt(cursor.getColumnIndex("received"));
                int curKeep = cursor.getInt(cursor.getColumnIndex("keep"));
                int curSent = cursor.getInt(cursor.getColumnIndex("sent"));

                curKeep = curKeep - keep;
                curPrice = curPrice - Integer.parseInt(price);

                if (curPrice == 0 && curSent == 0 && curKeep == 0)
                {
                    db.delete(table, "date = ? AND number = ?", new String[]{date, number});
                }
                else
                {
                    ContentValues values = new ContentValues();
                    values.put(XIEN_RECEIVED, curPrice);
                    values.put(XIEN_KEEP, curKeep);

                    db.update(table, values, "date = ? AND number = ?", new String[]{date, number});
                }
            }

            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteSentNumberFromMessage(String table, String number, String price, String date) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT * " +
                    " FROM " + table +
                    " WHERE date = ? AND number = ?";

            Cursor cursor = db.rawQuery(query, new String[]{date, number});

            if (cursor.moveToFirst())
            {
                int curSent = cursor.getInt(cursor.getColumnIndex("sent"));

                curSent = curSent - Integer.parseInt(price);

                int curReceived = cursor.getInt(cursor.getColumnIndex("received"));
                int curKeep = cursor.getInt(cursor.getColumnIndex("keep"));

                if (curSent == 0 && curReceived == 0 && curKeep == 0)
                {
                    db.delete(table, "date = ? AND number = ?", new String[]{date, number});
                }
                else
                {
                    ContentValues values = new ContentValues();
                    values.put(XIEN_SENT, curSent);

                    db.update(table, values, "date = ? AND number = ?", new String[]{date, number});
                }
            }

            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public int deleteKeepValue(String phone, String time) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            int count = db.delete(KEEP_TABLE, "phone = ? AND time = ?", new String[]{phone, time});

            db.setTransactionSuccessful();

            return count;
        } finally {
            db.endTransaction();
        }
    }

    public void deleteKeepValueByDate(String phone, String date) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            db.delete(KEEP_TABLE, "phone = ? AND date = ?", new String[]{phone, date});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAllKeepValueByDate(String date) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            db.delete(KEEP_TABLE, "date = ?", new String[]{date});
            ContentValues values = new ContentValues();
            values.put(XIEN_KEEP, "0");

            db.update(DE_TABLE, values, "date = ?", new String[]{date});
            db.update(LO_TABLE, values, "date = ?", new String[]{date});
            db.update(XIEN_TABLE, values, "date = ?", new String[]{date});
            db.update(BACANG_TABLE, values, "date = ?", new String[]{date});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAlls() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DE_TABLE, "date = ?", new String[]{Utils.getCurrentDate()});
        db.delete(LO_TABLE, "date = ?", new String[]{Utils.getCurrentDate()});
        db.delete(XIEN_TABLE, "date = ?", new String[]{Utils.getCurrentDate()});
        db.delete(BACANG_TABLE, "date = ?", new String[]{Utils.getCurrentDate()});
        db.delete(KEEP_TABLE, "date = ?", new String[]{Utils.getCurrentDate()});
    }

    public void eraseAlls() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DE_TABLE, null, null);
        db.delete(LO_TABLE, null, null);
        db.delete(XIEN_TABLE, null, null);
        db.delete(BACANG_TABLE, null, null);
        db.delete(KEEP_TABLE, null, null);
    }

    public Cursor getAllNumberKeepFromPhone(String phone, String date) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *, SUM(price) as sumkeep " +
                    " FROM " + KEEP_TABLE +
                    " INNER JOIN " +
                    " (SELECT * FROM de_table where de_table.date = ? UNION ALL SELECT * FROM lo_table where lo_table.date = ? UNION ALL SELECT * FROM xien_table where xien_table.date = ? UNION ALL SELECT * FROM bacang_table where bacang_table.date = ?) as gtable" +
                    " ON keep_table.number = gtable.number AND keep_table.type = gtable.type " +
                    " WHERE keep_table.phone = ? AND keep_table.date = ? " +
                    " GROUP BY keep_table.number, keep_table.type ";

            Cursor cursor = db.rawQuery(query, new String[]{date, date, date, date, phone, date});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllKeepFromMessage(String phone, String time) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *, SUM(price) as sumkeep " +
                    " FROM " + KEEP_TABLE +
                    " WHERE phone = ? AND time = ? " +
                    " GROUP BY number, type ";

            Cursor cursor = db.rawQuery(query, new String[]{phone, time});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllKeepFromPhone(String phone, String date) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *, SUM(price) as sumkeep " +
                    " FROM " + KEEP_TABLE +
                    " WHERE phone = ? AND date = ? " +
                    " GROUP BY number, type ";

            Cursor cursor = db.rawQuery(query, new String[]{phone, date});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public void updateKeepValueOnNumberTable(List<ContentValues> values) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            for (ContentValues value : values)
            {
                String number = value.getAsString("number");
                String date = value.getAsString("date");
                String type = value.getAsString("type");

                String table;

                if (type.contains("xien"))
                {
                    table = XIEN_TABLE;
                }
                else
                {
                    table = type + "_table";
                }

                db.update(table, value, "number = ? AND date = ?", new String[]{number, date});
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAllBlankValue(String date) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();


            db.delete(DE_TABLE, "received = ? AND sent = ? AND keep = ? AND date = ?", new String[]{"0", "0", "0", date});
            db.delete(LO_TABLE, "received = ? AND sent = ? AND keep = ? AND date = ?", new String[]{"0", "0", "0", date});
            db.delete(XIEN_TABLE, "received = ? AND sent = ? AND keep = ? AND date = ?", new String[]{"0", "0", "0", date});
            db.delete(BACANG_TABLE, "received = ? AND sent = ? AND keep = ? AND date = ?", new String[]{"0", "0", "0", date});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateValueReceivedAndSent(List<ContentValues> contentValues, String date) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            for (ContentValues values : contentValues)
            {
                String type = values.getAsString("type");
                String number = values.getAsString("number");
                String table;
                if (type.contains("xien"))
                {
                    table = TableType.xien_table.name();
                }
                else
                {
                    table = type + "_table";
                }

                String query = "SELECT * " +
                        " FROM " + table +
                        " WHERE date = ? AND number = ?";

                Cursor cursor = db.rawQuery(query, new String[]{date, number});

                if (cursor.moveToFirst())
                {
                    int curPrice = cursor.getInt(cursor.getColumnIndex("received"));
                    int curSent = cursor.getInt(cursor.getColumnIndex("sent"));

                    if (values.containsKey("received"))
                    {
                        int received = values.getAsInteger("received");
                        values.put("received", curPrice - received);
                    }
                    else if (values.containsKey("sent"))
                    {
                        int sent = values.getAsInteger("sent");
                        values.put("sent", curSent - sent);
                    }

                    db.update(table, values, "date = ? AND number = ?", new String[]{date, number});
                }

                cursor.close();
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateKeepValueToNumberTable(List<ContentValues> valuesList, UpdateSync sync) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            for (ContentValues values : valuesList)
            {
                String date = values.getAsString("date");
                String number = values.getAsString("number");
                String type = values.getAsString("type");
                String phone = values.getAsString("phone");
                String parenttype = values.getAsString("parenttype");
                int price = values.getAsInteger("price");
                int max = values.getAsInteger("max");

                String query = "SELECT " +
                        " (SELECT SUM(price) FROM keep_table WHERE " + KEEP_DATE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =?) AS sumall, " +
                        " (SELECT SUM(price) FROM keep_table WHERE " + KEEP_DATE + " =? AND " + KEEP_NUMBER + " =? AND " + KEEP_TYPE + " =? AND " + KEEP_PHONE + " =?) AS sum " +
                        " FROM keep_table";

                Cursor sum = db.rawQuery(query, new String[]{date, number, type,
                        date, number, type, phone});

                int currentPrice = 0;
                int sumall = 0;

                if (sum != null && sum.moveToFirst())
                {
                    currentPrice = sum.getInt(sum.getColumnIndex("sum"));
                    sumall = sum.getInt(sum.getColumnIndex("sumall"));
                }


                if (sum != null) sum.close();

                //Calculate keep price.
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
                        return;
                    }
                }

                values.put(KEEP_PRICE, price);
                values.remove("max");

                Log.d("FollowDatabase", "insertKeep: " + "addNew" + " phone= " + values.getAsString("phone"));
                db.insert(KEEP_TABLE, null, values);

                //Update sum at table detail.
                ContentValues valuesSum = new ContentValues();
                valuesSum.put("keep", sumall + price);

                String table = parenttype + "_table";
                db.update(table, valuesSum, "number = ? AND date =?", new String[]{number, date});

                if (sync != null)
                {
                    sync.onUpdate();
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public void updateResult(String date, TableType table, String... numberWin) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            if (numberWin != null && numberWin.length > 0)
            {
                String appendQuery = Utils.orQuery("number", numberWin);

                ContentValues values = new ContentValues();
                values.put("countwin", "true");

                db.update(table.name(), values, "date = ? " + appendQuery, new String[]{date});
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public int[] getSumDeWinToDay() {

        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            int[] sum = new int[]{0, 0};
            String query = "SELECT " +
                    " de_table.type, CAST(de_table.received AS REAL) - CAST(de_table.sent AS REAL) as sum " +
                    " FROM " + "de_table" +
                    " WHERE de_table.date = ? AND de_table.countwin = ?" +
                    " UNION ALL " +
                    " SELECT " +
                    " bacang_table.type, CAST(bacang_table.received AS REAL) - CAST(bacang_table.sent AS REAL) as sum " +
                    " FROM " + "bacang_table" +
                    " WHERE bacang_table.date = ? AND bacang_table.countwin = ?";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), "true", Utils.getCurrentDate(), "true"});

            if (cursor != null && cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                if (cursor.getCount() == 2)
                {
                    sum[0] = cursor.getInt(cursor.getColumnIndex("sum"));
                    cursor.moveToNext();
                    sum[1] = cursor.getInt(cursor.getColumnIndex("sum"));
                }
                else if (cursor.getCount() == 1)
                {
                    String type = cursor.getString(0);
                    int s = cursor.getInt(cursor.getColumnIndex("sum"));

                    sum[0] = type.equals("de") ? s : 0;
                    sum[1] = type.equals("bacang") ? s : 0;
                }
                else
                {
                    sum[0] = sum[1] = -1;
                }
            }

            if (cursor != null)
                cursor.close();

            db.setTransactionSuccessful();

            return sum;
        } finally {
            db.endTransaction();
        }
    }
}
