package bingo.com.helperdb.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;

import bingo.com.model.CongnoModel;
import bingo.com.utils.Utils;

public class CongnoDatabase extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE = "CongnoDatabase";
    public static final String DETAIL_TABLE = "detail";

    public static final String DETAIL_NAME = "name";
    public static final String DETAIL_PHONE = "phone";
    public static final String DETAIL_TYPE = "type";
    public static final String DETAIL_POINT = "point";
    public static final String DETAIL_POINT_WIN = "pointwin";
    public static final String DETAIL_DATE = "date";
    public static final String DETAIL_MONEY = "money";
    public static final String DETAIL_CUSTOMER = "type_customer";

    String CREATE_DETAIL_TABLE = "CREATE TABLE "
            + DETAIL_TABLE + "( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DETAIL_NAME + " TEXT, "
            + DETAIL_PHONE + " TEXT,"
            + DETAIL_TYPE + " TEXT,"
            + DETAIL_POINT + " TEXT,"
            + DETAIL_POINT_WIN + " TEXT,"
            + DETAIL_DATE + " TEXT,"
            + DETAIL_MONEY + " TEXT,"
            + DETAIL_CUSTOMER + " TEXT)";

    public CongnoDatabase(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_DETAIL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS detail");
        onCreate(db);

        setWriteAheadLoggingEnabled(true);
    }

    public void insert(CongnoModel... models) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            for (CongnoModel model : models)
            {
                ContentValues values = getValueFromModel(model);

                if (validateExist(db, model))
                {
                    int row = db.update(DETAIL_TABLE, values,
                            DETAIL_PHONE + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_DATE + " =? ",
                            new String[]{model.getPhone(), model.getType().name(), model.getDate()});

                    Log.d("CongnoDatabase", "Updated cong no row: " + row + " phone: " + model.getPhone() + " type: " + model.getType().name());
                }
                else
                {
                    long row = db.insert(DETAIL_TABLE, null, values);

                    Log.d("CongnoDatabase", "Insert cong no row: " + row + " phone: " + model.getPhone() + " type: " + model.getType().name());
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void insertWithOutConflict(CongnoModel... models) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            for (CongnoModel model : models)
            {
                ContentValues values = getValueFromModel(model);

                Cursor cursor = db.query(DETAIL_TABLE, null,
                        DETAIL_PHONE + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_DATE + " =? ",
                        new String[]{model.getPhone(), model.getType().name(), model.getDate()},
                        null, null, null);

                boolean result = (cursor == null || cursor.getCount() == 0);

                if (!result)
                {
                    cursor.moveToFirst();
                    double current = cursor.getDouble(cursor.getColumnIndex(DETAIL_MONEY));

                    values.put(DETAIL_MONEY, (Double.parseDouble(model.getMoney()) + current));

                    int row = db.update(DETAIL_TABLE, values,
                            DETAIL_PHONE + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_DATE + " =? ",
                            new String[]{model.getPhone(), model.getType().name(), model.getDate()});

                    Log.d("CongnoDatabase", "Updated cong no row: " + row + " phone: " + model.getPhone() + " type: " + model.getType().name());
                }
                else
                {
                    long row = db.insert(DETAIL_TABLE, null, values);

                    Log.d("CongnoDatabase", "Insert cong no row: " + row + " phone: " + model.getPhone() + " type: " + model.getType().name());
                }

                cursor.close();
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private boolean validateExist(SQLiteDatabase db, CongnoModel model) {
        Cursor cursor = db.query(DETAIL_TABLE, null,
                DETAIL_PHONE + " =? AND " + DETAIL_TYPE + " =? AND " + DETAIL_DATE + " =? ",
                new String[]{model.getPhone(), model.getType().name(), model.getDate()},
                null, null, null);

        boolean result = (cursor == null || cursor.getCount() == 0);

        if (cursor != null) cursor.close();

        return !result;

    }

    private ContentValues getValueFromModel(CongnoModel model) {
        ContentValues values = new ContentValues();
        values.put(DETAIL_NAME, model.getName());
        values.put(DETAIL_PHONE, model.getPhone());
        values.put(DETAIL_TYPE, model.getType().name());
        values.put(DETAIL_POINT, model.getPoint());
        values.put(DETAIL_POINT_WIN, model.getPointWin());
        values.put(DETAIL_DATE, model.getDate());
        values.put(DETAIL_MONEY, model.getMoney());
        values.put(DETAIL_CUSTOMER, model.getCustomerType());

        return values;
    }

    public Cursor getCongNoByPhone(String phone) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            Cursor cursor = db.rawQuery("SELECT *" +
                            " FROM " + DETAIL_TABLE +
                            " WHERE " + DETAIL_PHONE + " =? AND " + DETAIL_DATE + " =? " +
                            " ORDER BY LENGTH(type) ASC , " + DETAIL_TYPE + " ASC",
                    new String[]{phone, Utils.getCurrentDate()});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllCongNo() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            Cursor cursor = db.rawQuery("SELECT *, SUM(CAST( "+ DETAIL_MONEY +" AS REAL)) as sum" +
                            " FROM " + DETAIL_TABLE +
                            " GROUP BY " + DETAIL_PHONE +
                            " ORDER BY " + DETAIL_CUSTOMER + " DESC ," + DETAIL_NAME + " ASC",
                    new String[]{});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public double getNoCu(String phone) {
        SQLiteDatabase db = getReadableDatabase();

        double sum = 0;

        try {
            db.beginTransaction();

            Cursor cursor = db.rawQuery("SELECT *, SUM(CAST( "+ DETAIL_MONEY +" AS REAL)) as sum" +
                            " FROM " + DETAIL_TABLE +
                            " WHERE " + DETAIL_DATE + " !=? AND " + DETAIL_PHONE + " =?" +
                            " GROUP BY " + DETAIL_PHONE +
                            " ORDER BY " + DETAIL_PHONE + " ASC",
                    new String[]{Utils.convertDate(String.valueOf(Calendar.getInstance().getTimeInMillis())), phone});

            if (cursor != null && cursor.moveToFirst())
            {
                sum = cursor.getDouble(cursor.getColumnIndex("sum"));
            }

            cursor.close();

            db.setTransactionSuccessful();
            return sum;
        } finally {
            db.endTransaction();
        }
    }

    public double getNoCuTodate(String phone, String date) {
        SQLiteDatabase db = getReadableDatabase();

        double sum = 0;

        try {
            db.beginTransaction();

            Cursor cursor = db.rawQuery("SELECT *, SUM(CAST( " + DETAIL_MONEY + " AS REAL)) as summoney" +
                            " FROM " + DETAIL_TABLE +
                            " WHERE " + DETAIL_PHONE + " =? " +
                            " GROUP BY " + DETAIL_DATE +
                            " ORDER BY (substr(date, 7, 4) || '/' || substr(date, 4, 2) || '/' || substr(date, 1, 2)) ASC",
                    new String[]{phone});

            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    double money = cursor.getDouble(cursor.getColumnIndex("summoney"));
                    String dateInCursor = cursor.getString(cursor.getColumnIndex("date"));

                    if (dateInCursor.equals(date)) break;

                    sum += money;

                    cursor.moveToNext();
                }
            }

            cursor.close();

            db.setTransactionSuccessful();
            return sum;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getCongNoHistory(String phone, String date) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            Cursor cursor = db.rawQuery("SELECT *" +
                            " FROM " + DETAIL_TABLE +
                            " WHERE " + DETAIL_PHONE + " =? AND " + DETAIL_DATE + " =?" +
                            " ORDER BY " + DETAIL_DATE + " DESC",
                    new String[]{phone, date});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getPhoneGroupDate(String phone) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            Cursor cursor = db.rawQuery("SELECT *, SUM(CAST( " + DETAIL_MONEY + " AS REAL)) as summoney" +
                            " FROM " + DETAIL_TABLE +
                            " WHERE " + DETAIL_PHONE + " =? " +
                            " GROUP BY " + DETAIL_DATE +
                            " ORDER BY (substr(date, 7, 4) || '/' || substr(date, 4, 2) || '/' || substr(date, 1, 2)) DESC",
                    new String[]{phone});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAlls() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DETAIL_TABLE, DETAIL_DATE + " =? ", new String[]{Utils.getCurrentDate()});
    }

    public void eraseAlls() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DETAIL_TABLE, null, null);
    }

    public void deleteCongNo(String phone) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DETAIL_TABLE, DETAIL_DATE + " =? AND " + DETAIL_PHONE + " =?", new String[]{Utils.getCurrentDate(), phone});
    }

    public void deleteAllCustomerCongNo(String phone) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            db.delete(DETAIL_TABLE, DETAIL_PHONE + " =?", new String[]{phone});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
