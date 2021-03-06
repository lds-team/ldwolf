package bingo.com.helperdb.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.Calendar;

import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.SMSHelper;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.Utils;

@Deprecated
public class TempleMsgDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "templemsgdatabase";
    private static final String TABLE_NAME = "message";

    private static final String KEY_ID = "_id";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    private static final String MESSAGE_ID = "messageid";
    public static final String CONTENT = "content";
    public static final String ERROR = "error";
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String HAS_ANALYZE = "analyze";
    public static final String TYPE = "type";

    public Context context;

    String CREATE_MESSAGE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME + " TEXT, "
            + PHONE + " TEXT,"
            + MESSAGE_ID + " TEXT,"
            + CONTENT + " TEXT,"
            + ERROR + " TEXT,"
            + TIME + " TEXT,"
            + DATE + " TEXT,"
            + HAS_ANALYZE + " TEXT,"
            + TYPE + " TEXT" + ")";

    public TempleMsgDatabase(Context context) {
        super(context, DB_NAME, null, 1);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MESSAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void syncDatabase(String phone, long timeEnd) {

        insertDb(phone, TypeMessage.RECEIVED, timeEnd, SMSHelper.RECEIVED_MESSAGE_CONTENT_PROVIDER);
        insertDb(phone, TypeMessage.SENT, timeEnd, SMSHelper.SENT_MESSAGE_CONTENT_PROVIDER);
    }

    private void insertDb(String phone, TypeMessage type, long timeEnd, Uri uri) {

        String[] time = getStartAndEndTime(timeEnd);

        String phone2 = "";

        if (phone.startsWith("0"))
            phone2 = phone.replaceFirst("0", "+84");
        if (phone.startsWith("+84"))
            phone2 = phone.replaceFirst("\\+84", "0");

        String[] reqCols = new String[] { "_id", "thread_id", "address", "body", "date", "person" };
        Cursor c = this.context.getContentResolver().query(uri, reqCols, "date >=? AND date <=? AND (address =? OR address =?)" , new String[]{time[0], time[1], phone, phone2}, null);


        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            if (c.moveToFirst()) {

                while (!c.isAfterLast()) {

                    ContentValues values = new ContentValues();
                    String address = c.getString(c.getColumnIndex("address"));
                    values.put(NAME, LoaderSQLite.getContactName(context, address, Utils.fixPhone(address)));
                    values.put(PHONE, Utils.fixPhone(address));
                    values.put(MESSAGE_ID, c.getString(c.getColumnIndex("_id")));
                    values.put(CONTENT, c.getString(c.getColumnIndex("body")));
                    values.put(ERROR, "");
                    values.put(TIME, c.getString(c.getColumnIndex("date")));
                    values.put(DATE, convertDate(c.getString(c.getColumnIndex("date"))));
                    values.put(TYPE, type.name());
                    values.put(HAS_ANALYZE, "0");

                    updateIfNeed(db, values);

                    c.moveToNext();
                }
            }

            c.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateIfNeed(SQLiteDatabase db, ContentValues values) {
        //TODO may be query with _id field.
        Cursor cursor = db.query(TABLE_NAME, null, TIME + "=? AND " + PHONE + " =? ",
                new String[]{values.getAsString(TIME), values.getAsString(PHONE)}, null, null, null, null);

        if (cursor == null || cursor.getCount() == 0)
        {
            db.insert(TABLE_NAME, null, values);
        }
        else
        {
            //Not update Message has analyze.
            values.remove(HAS_ANALYZE);

            //Not Update Content Editted.
            values.remove(CONTENT);

            //RemoveError.
            values.remove(ERROR);

            //Update Row.
            cursor.moveToFirst();

            int rowId = db.update(TABLE_NAME, values, TIME + "=? AND " + PHONE + " =? ",
                    new String[]{values.getAsString(TIME), values.getAsString(PHONE)});
        }

        cursor.close();
    }

    public Cursor getMessageByPhone(String phone, TypeMessage type, String date) {

        String arg1 = null;
        String arg2 = null;

        if (type == TypeMessage.SMS)
        {
            arg1 = TypeMessage.RECEIVED.name();
            arg2 = TypeMessage.SENT.name();
        }
        else
        {
            arg1 = arg2 = type.name();
        }

        SQLiteDatabase db = this.getReadableDatabase();

        try {
            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, PHONE + " =? AND " + DATE + " =? AND " + "(" + TYPE + " =? OR " + TYPE + " =?) " , new String[]{phone, date, arg1, arg2}, null, null, TIME + " DESC");

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public int updateError(String phone, String content, String error) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ERROR, error);
        values.put(HAS_ANALYZE, "0");

        return db.update(TABLE_NAME, values, CONTENT + " =? AND " + PHONE + " =?", new String[]{content, phone});
    }

    public Cursor getAlls(String phone, long timeinDay) {
        String[] time = getStartAndEndTime(timeinDay);

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, PHONE + " =? AND " + TIME + " >=? AND " + TIME + " <=? ", new String[]{phone, time[0], time[1]}, null, null, TIME + " DESC");

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    private String[] getStartAndEndTime(long timeinDay) {

        Calendar calendar = Calendar.getInstance();

        if (timeinDay != 0)
        {
            calendar.setTimeInMillis(timeinDay);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        String endDate = String.valueOf(calendar.getTimeInMillis());

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String startDate = String.valueOf(calendar.getTimeInMillis());

        return new String[]{startDate, endDate};
    }

    public String convertDate(String dateInMilliseconds) {

        return Utils.convertDate(dateInMilliseconds);
    }

    public void deleteAlls() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NAME, null, null);
    }

    public void deleteMessageWithId(String _id) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NAME, MESSAGE_ID + " =?", new String[]{_id});
    }
}
