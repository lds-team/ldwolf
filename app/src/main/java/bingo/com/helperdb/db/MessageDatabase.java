package bingo.com.helperdb.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;

import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.SMSHelper;
import bingo.com.model.AnalyzeModel;
import bingo.com.model.ChooseMessage;
import bingo.com.model.ReceivedModel;
import bingo.com.utils.Constant;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.Utils;

public class MessageDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "messagedatabase";
    private static final String TABLE_NAME = "message";

    private static final String KEY_ID = "_id";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    private static final String MESSAGE_ID = "messageid";
    private static final String THREAD = "thread";
    public static final String CONTENT = "content";
    public static final String RAW_CONTENT = "rawcontent";
    public static final String ANALYZE_CONTENT = "analyzecontent";
    public static final String WIN_NUMBER = "winnumber";
    public static final String ERROR = "error";
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String TYPE = "type";
    public static final String DE = "de";
    public static final String LO = "lo";
    public static final String XIEN2 = "xien2";
    public static final String XIEN3 = "xien3";
    public static final String XIEN4 = "xien4";
    public static final String BACANG = "bacang";
    public static final String WIN_MESSAGE = "winmessage";
    public static final String HAS_ANALYZE = "analyze";
    public static final String HAS_RESPONSE = "response";

    //------------------
    public static final String RECEIVED_TABLE = "received_table";

    public static final String RECEIVED_NAME = "name";
    public static final String RECEIVED_PHONE = "phone";
    public static final String RECEIVED_DATE = "date";
    public static final String RECEIVED_TIME = "time";
    public static final String RECEIVED_TYPE_MESSAGE = "typemessage";
    public static final String RECEIVED_ACTUAL_COLLECT_DE = "actualcollectde";
    public static final String RECEIVED_ACTUAL_COLLECT_LO = "actualcollectlo";
    public static final String RECEIVED_ACTUAL_COLLECT_XIEN2 = "actualcollectxien2";
    public static final String RECEIVED_ACTUAL_COLLECT_XIEN3 = "actualcollectxien3";
    public static final String RECEIVED_ACTUAL_COLLECT_XIEN4 = "actualcollectxien4";
    public static final String RECEIVED_ACTUAL_COLLECT_BACANG = "actualcollectbacang";
    public static final String RECEIVED_DE_WIN = "dewin";
    public static final String RECEIVED_LO_WIN = "lowin";
    public static final String RECEIVED_LO_POINT_WIN = "lopointwin";
    public static final String RECEIVED_XIEN2_WIN= "xien2win";
    public static final String RECEIVED_XIEN3_WIN= "xien3win";
    public static final String RECEIVED_XIEN4_WIN= "xien4win";
    public static final String RECEIVED_BACANG_WIN = "bacangwin";
    public static final String RECEIVED_DETAIL = "detail";

    public Context context;

    String CREATE_MESSAGE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME + " TEXT, "
            + PHONE + " TEXT,"
            + MESSAGE_ID + " TEXT,"
            + THREAD + " TEXT,"
            + CONTENT + " TEXT,"
            + RAW_CONTENT + " TEXT,"
            + ANALYZE_CONTENT + " TEXT,"
            + WIN_NUMBER + " TEXT,"
            + ERROR + " TEXT,"
            + TIME + " TEXT,"
            + DATE + " TEXT,"
            + TYPE + " TEXT,"
            + DE + " TEXT,"
            + LO + " TEXT,"
            + XIEN2 + " TEXT,"
            + XIEN3 + " TEXT,"
            + XIEN4 + " TEXT,"
            + BACANG + " TEXT,"
            + WIN_MESSAGE + " TEXT,"
            + HAS_ANALYZE + " TEXT,"
            + HAS_RESPONSE + " TEXT" + ")";

    String CREATE_RECEIVED_TABLE = "CREATE TABLE "
            + RECEIVED_TABLE + "( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + RECEIVED_NAME + " TEXT, "
            + RECEIVED_PHONE + " TEXT,"
            + RECEIVED_DATE + " TEXT,"
            + RECEIVED_TIME + " TEXT,"
            + RECEIVED_TYPE_MESSAGE + " TEXT,"
            + RECEIVED_ACTUAL_COLLECT_DE + " TEXT,"
            + RECEIVED_ACTUAL_COLLECT_LO + " TEXT,"
            + RECEIVED_ACTUAL_COLLECT_XIEN2 + " TEXT,"
            + RECEIVED_ACTUAL_COLLECT_XIEN3 + " TEXT,"
            + RECEIVED_ACTUAL_COLLECT_XIEN4 + " TEXT,"
            + RECEIVED_ACTUAL_COLLECT_BACANG + " TEXT,"
            + RECEIVED_DE_WIN + " INTEGER,"
            + RECEIVED_LO_WIN + " TEXT,"
            + RECEIVED_LO_POINT_WIN + " TEXT,"
            + RECEIVED_XIEN2_WIN + " TEXT,"
            + RECEIVED_XIEN3_WIN + " TEXT,"
            + RECEIVED_XIEN4_WIN + " TEXT,"
            + RECEIVED_BACANG_WIN + " TEXT,"
            + RECEIVED_DETAIL + " TEXT)";

    public MessageDatabase(Context context) {
        super(context, DB_NAME, null, 1);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MESSAGE_TABLE);
        db.execSQL(CREATE_RECEIVED_TABLE);

        setWriteAheadLoggingEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS received_table");

        // Create tables again
        onCreate(db);
    }

    public void addMessageSent(ChooseMessage item) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();

            values.put(NAME, LoaderSQLite.getContactName(context, item.getPhone(), Utils.fixPhone(item.getPhone())));
            values.put(PHONE, Utils.fixPhone(item.getPhone()));
            values.put(CONTENT, item.getRawContent());
            values.put(RAW_CONTENT, item.getRawContent());
            values.put(ANALYZE_CONTENT, "");
            values.put(WIN_NUMBER, "");
            values.put(ERROR, "");
            values.put(TIME, item.getTime());
            values.put(DATE, convertDate(item.getTime()));
            values.put(TYPE, item.getType());
            values.put(DE, "0");
            values.put(LO, "0");
            values.put(XIEN2, "0");
            values.put(XIEN3, "0");
            values.put(XIEN4, "0");
            values.put(BACANG, "0");
            values.put(WIN_MESSAGE, "");
            values.put(HAS_ANALYZE, "1");
            values.put(HAS_RESPONSE, "1");

            Cursor cursor = db.query(TABLE_NAME, null, TYPE + "=? AND " + PHONE + " =? AND " + TIME + " =?",
                    new String[]{values.getAsString(TYPE), values.getAsString(PHONE), values.getAsString(TIME)}, null, null, null, null);

            if (cursor == null || cursor.getCount() == 0)
            {
                db.insert(TABLE_NAME, null, values);
            }
            else
            {
                int rowId = db.update(TABLE_NAME, values, TYPE + "=? AND " + PHONE + " =? ",
                        new String[]{values.getAsString(TYPE), values.getAsString(PHONE)});
            }

            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void addMessage(ChooseMessage item) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();

            values.put(NAME, item.getName());
            values.put(PHONE, Utils.fixPhone(item.getPhone()));
            values.put(CONTENT, item.getRawContent());
            values.put(RAW_CONTENT, item.getRawContent());
            values.put(ANALYZE_CONTENT, "");
            values.put(WIN_NUMBER, "");
            values.put(ERROR, "");
            values.put(TIME, item.getTime());
            values.put(DATE, convertDate(item.getTime()));
            values.put(TYPE, item.getType());
            values.put(DE, "0");
            values.put(LO, "0");
            values.put(XIEN2, "0");
            values.put(XIEN3, "0");
            values.put(XIEN4, "0");
            values.put(BACANG, "0");
            values.put(WIN_MESSAGE, "");
            values.put(HAS_ANALYZE, "0");
            values.put(HAS_RESPONSE, "0");

            updateIfNeed(db, values);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateIfNeed(SQLiteDatabase db, ContentValues values) {
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

            values.remove(HAS_RESPONSE);

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

    @Deprecated
    @SuppressWarnings("Just use for reload message")
    public void loadMessage(String phone, TypeMessage type, long timeEnd) {

        Uri uri = null;

        if (type == TypeMessage.RECEIVED)
        {
            uri = SMSHelper.RECEIVED_MESSAGE_CONTENT_PROVIDER;
        }
        else if (type == TypeMessage.SENT)
        {
            uri = SMSHelper.SENT_MESSAGE_CONTENT_PROVIDER;
        }


        if (uri == null)
        {
            insertDb(phone, TypeMessage.RECEIVED, timeEnd, SMSHelper.RECEIVED_MESSAGE_CONTENT_PROVIDER);
            insertDb(phone, TypeMessage.SENT, timeEnd, SMSHelper.SENT_MESSAGE_CONTENT_PROVIDER);
        }
        else
        {
            insertDb(phone, type, timeEnd, uri);
        }
    }

    @Deprecated
    private void insertDb(String phone, TypeMessage type, long timeEnd, Uri uri) {

        String[] time = getStartAndEndTime(timeEnd, false);

        String phone2 = "";

        if (phone.startsWith("0"))
            phone2 = phone.replaceFirst("0", "+84");
        if (phone.startsWith("+84"))
            phone2 = phone.replaceFirst("\\+84", "0");

        String[] reqCols = new String[] { "_id", "thread_id", "address", "body", "date", "person" };
        Cursor c = this.context.getContentResolver().query(uri, null, "date >=? AND date <=? AND (address =? OR address =?)" , new String[]{time[0], time[1], phone, phone2}, null);


        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            if (c.moveToFirst()) {

                while (!c.isAfterLast()) {

                    ContentValues values = new ContentValues();

                    values.put(MESSAGE_ID, c.getString(c.getColumnIndex("_id")));
                    values.put(THREAD, c.getString(c.getColumnIndex("thread_id")));

                    String address = c.getString(c.getColumnIndex("address"));

                    values.put(NAME, LoaderSQLite.getContactName(context, address, Utils.fixPhone(address)));
                    values.put(PHONE, Utils.fixPhone(address));
                    values.put(CONTENT, c.getString(c.getColumnIndex("body")));
                    values.put(RAW_CONTENT, c.getString(c.getColumnIndex("body")));
                    values.put(ANALYZE_CONTENT, "");
                    values.put(WIN_NUMBER, "");
                    values.put(ERROR, "");
                    values.put(TIME, c.getString(c.getColumnIndex("date")));
                    values.put(DATE, convertDate(c.getString(c.getColumnIndex("date"))));
                    values.put(TYPE, type.name());
                    values.put(DE, "0");
                    values.put(LO, "0");
                    values.put(XIEN2, "0");
                    values.put(XIEN3, "0");
                    values.put(XIEN4, "0");
                    values.put(BACANG, "0");
                    values.put(WIN_MESSAGE, "");
                    values.put(HAS_ANALYZE, "0");
                    values.put(HAS_RESPONSE, "1");

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

            Cursor cursor = db.query(TABLE_NAME, null, PHONE + " =? AND " + DATE + " =? AND " + HAS_ANALYZE + " =? AND " + "(" + TYPE + " =? OR " + TYPE + " =?) ", new String[]{phone, date, "1", arg1, arg2}, null, null, TIME + " DESC");

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getMessageForResponse(String phone, String date) {

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            db.beginTransaction();

            String query = "SELECT * " +
                    " FROM message " +
                    " WHERE " + PHONE + " =? AND " + DATE + " =? AND " + "(" + TYPE + " =?) " +
                    " ORDER BY " + TIME + " ASC";

            Cursor cursor = db.rawQuery(query, new String[]{phone, date, TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getMessageByPhone(String phone, TypeMessage type, long timeinDay, boolean isTimeEnd) {

        String[] time = getStartAndEndTime(timeinDay, isTimeEnd);

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

            String query = "SELECT * " +
                    " FROM message JOIN received_table ON message.phone = received_table.phone AND message.time = received_table.time " +
                    " WHERE message." + PHONE + " =? AND message." + TIME + " >=? AND message." + TIME + " <=? AND " + "(message." + TYPE + " =? OR message." + TYPE + " =?) " + " AND message." + HAS_ANALYZE + " =? " +
                    " ORDER BY message." + TIME + " ASC";

            Cursor cursor = db.rawQuery(query, new String[]{phone, time[0], time[1], arg1, arg2, "1"});

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    private String[] getStartAndEndTime(long timeinDay) {

        return getStartAndEndTime(timeinDay, false);
    }

    private String[] getStartAndEndTime(long timeinDay, boolean isTimeEnd) {

        Calendar calendar = Calendar.getInstance();

        if (timeinDay != 0)
        {
            calendar.setTimeInMillis(timeinDay);

            if (!isTimeEnd)
            {
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
            }
        }
        else
        {
            calendar.setTimeInMillis(Utils.convertLongTime(Utils.getCurrentDate()));

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
        }

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

    public int updateSingleMessageAnalyze(boolean hasAnalyze, String phone, String time) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(HAS_ANALYZE, hasAnalyze ? "1" : "0");

            int row = db.update(TABLE_NAME, values, PHONE + " =? AND " + TIME + " =?", new String[]{phone, time});

            db.setTransactionSuccessful();

            return row;
        } finally {
            db.endTransaction();
        }

    }

    public void updateAllAnalyzeStatus(boolean hasAnalyze) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] time = getStartAndEndTime(0);

        ContentValues values = new ContentValues();
        values.put(HAS_ANALYZE, hasAnalyze ? "1" : "0");

        db.update(TABLE_NAME, values, DATE + " =?", new String[]{Utils.getCurrentDate()});
    }

    public Cursor getCursorStattisticNoAnalyze(String keyNameOrPhone){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                null,
                DATE + "=? AND " + HAS_ANALYZE + "=? AND " +
                        "(" + NAME + " LIKE ? OR " + PHONE + " LIKE ?)",
                new String[]{Utils.getCurrentDate(), "0","%" + keyNameOrPhone + "%", "%" + keyNameOrPhone + "%"},
                null, null, TIME + " ASC");

        return cursor;
    }

    public Cursor getCursorStattisticNoAnalyze(){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                null,
                DATE + "=? AND " + HAS_ANALYZE + "=? ",
                new String[]{Utils.getCurrentDate(), "0"},
                null, null, TIME + " ASC");

        return cursor;
    }

    public int getCountNoAnalyze(){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                null,
                DATE + "=? AND " + HAS_ANALYZE + "=? ",
                new String[]{Utils.getCurrentDate(), "0"},
                null, null, TIME + " ASC");

        int count = cursor.getCount();

        cursor.close();

        return count;
    }

    public void updateError(String phone, String time, String error) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ERROR, error);

        if (!TextUtils.isEmpty(error))
            values.put(HAS_ANALYZE, "0");

        db.update(TABLE_NAME, values, TIME + " =? AND " + PHONE + " =?", new String[]{time, phone});
    }

    public boolean updateTimeMessage(String oldTime, String phone, String newTime) {

        String _id = null;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, PHONE + " =? AND " + TIME + " =? ",
                new String[]{phone, oldTime}, null, null, null);

        if (cursor.moveToFirst() && cursor.getCount() == 1)
        {
            _id = cursor.getString(cursor.getColumnIndex("_id"));
        }

        cursor.close();

        if (_id != null)
        {
            ContentValues values = new ContentValues();
            values.put(TIME, newTime);

            db.update(TABLE_NAME, values, "_id =? ", new String[]{_id});
        }

        return _id != null;
    }

    public int updateContentNornal(String phone, String time, String content) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONTENT, content);
        values.put(HAS_ANALYZE, "0");

        return db.update(TABLE_NAME, values, TIME + " =? AND " + PHONE + " =?", new String[]{time, phone});
    }

    public int updateContentEditted(String phone, String time, String content) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONTENT, content);
        values.put(HAS_ANALYZE, "0");
        values.put(TIME, String.valueOf(Long.parseLong(time) + 1));

        return db.update(TABLE_NAME, values, TIME + " =? AND " + PHONE + " =?", new String[]{time, phone});
    }

    public void deleteMessageByDay(String phone, String date) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, DATE + " =? AND " + PHONE + " =?", new String[]{date, phone});
        db.delete(RECEIVED_TABLE, DATE + " =? AND " + PHONE + " =?", new String[]{date, phone});
    }

    public void deleteMessage(String phone, String time) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, TIME + " =? AND " + PHONE + " =?", new String[]{time, phone});
        db.delete(RECEIVED_TABLE, TIME + " =? AND " + PHONE + " =?", new String[]{time, phone});
    }

    public void deleteAlls() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NAME, "date = ?", new String[]{Utils.getCurrentDate()});
        db.delete(RECEIVED_TABLE, "date = ?", new String[]{Utils.getCurrentDate()});
    }

    public void eraseAlls() {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NAME, null, null);
        db.delete(RECEIVED_TABLE, null, null);
    }

    public void deleteDataUser(String phone, String date) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NAME, PHONE + " =? AND " + DATE + " =? AND " + HAS_ANALYZE + " =?", new String[]{phone, date, "1"});
        db.delete(RECEIVED_TABLE, PHONE + " =? AND " + DATE + " =? AND " + RECEIVED_DETAIL + " !=''", new String[]{phone, date});
    }

    public void addFromTemple(String name, String phone, String content, String error, String time, String date, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(PHONE, Utils.fixPhone(phone));
        values.put(CONTENT, content);
        values.put(RAW_CONTENT, content);
        values.put(ANALYZE_CONTENT, "");
        values.put(WIN_NUMBER, "");
        values.put(ERROR, error);
        values.put(TIME, time);
        values.put(DATE, date);
        values.put(TYPE, type);
        values.put(DE, "0");
        values.put(LO, "0");
        values.put(XIEN2, "0");
        values.put(XIEN3, "0");
        values.put(XIEN4, "0");
        values.put(BACANG, "0");
        values.put(WIN_MESSAGE, "");
        values.put(HAS_ANALYZE, "0");
        values.put(HAS_RESPONSE, "0");

        db.insert(TABLE_NAME, null, values);
    }

    @Deprecated
    public Cursor getAllMessage(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        try {

            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, PHONE + " =? AND " + DATE + " =? ",
                    new String[]{phone, Utils.getCurrentDate()}, null, null, null);

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public boolean hasResponse(String phone, String time) {
        SQLiteDatabase db = this.getReadableDatabase();

        boolean hasResponse = false;

        try {

            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, PHONE + " =? AND " + TIME + " =? ",
                    new String[]{phone, time}, null, null, null);

            if (cursor != null && cursor.moveToFirst())
            {
                hasResponse = cursor.getString(cursor.getColumnIndex(HAS_RESPONSE)).equals("1");
            }

            cursor.close();

            db.setTransactionSuccessful();

            return hasResponse;
        } finally {
            db.endTransaction();
        }
    }

    public boolean hasMessageBefore(String phone, String date, String content) {
        SQLiteDatabase db = this.getReadableDatabase();

        boolean hasBefore = false;

        try {

            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, PHONE + " =? AND " + DATE + " =? AND " + RAW_CONTENT + " =?",
                    new String[]{phone, date, content}, null, null, null);

            hasBefore = cursor != null && cursor.moveToFirst();

            cursor.close();

            db.setTransactionSuccessful();

            return hasBefore;
        } finally {
            db.endTransaction();
        }
    }

    public int updateResponseStatus(String phone, String time, boolean hasResponse) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HAS_RESPONSE, hasResponse ? "1" : "0");

        return db.update(TABLE_NAME, values, TIME + " =? AND " + PHONE + " =?", new String[]{time, phone});
    }

    public int updateResponseStatusOnDay(String phone, String date, boolean hasResponse) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HAS_RESPONSE, hasResponse ? "1" : "0");

        return db.update(TABLE_NAME, values, DATE + " =? AND " + PHONE + " =?", new String[]{date, phone});
    }

    public String getError(String phone, String time) {
        SQLiteDatabase db = this.getReadableDatabase();

        String error = null;

        try {

            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, PHONE + " =? AND " + TIME + " =? ",
                    new String[]{phone, time}, null, null, null);

            if (cursor != null && cursor.moveToFirst())
            {
                error = cursor.getString(cursor.getColumnIndex(ERROR));
            }

            cursor.close();

            db.setTransactionSuccessful();

            return error;
        } finally {
            db.endTransaction();
        }
    }

    public int updateAnalyzeContent(String phone, String time, String analyzeContent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ANALYZE_CONTENT, analyzeContent);

        return db.update(TABLE_NAME, values, TIME + " =? AND " + PHONE + " =?", new String[]{time, phone});
    }

    public void updateWinNumber(String phone, String time, String numberWinSyntax) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, PHONE + " =? AND " + TIME + " =? ",
                    new String[]{phone, time}, null, null, null);

            String win;

            if (cursor != null && cursor.moveToFirst())
            {
                win = cursor.getString(cursor.getColumnIndex(WIN_NUMBER));

                /*win = (TextUtils.isEmpty(win) ? numberWinSyntax : win.concat(Constant.SEPARATE).concat(numberWinSyntax));*/

                if (TextUtils.isEmpty(win))
                {
                    win = numberWinSyntax;
                }
                else
                {
                    if (!win.contains(numberWinSyntax))
                    {
                        win = win.concat(Constant.SEPARATE).concat(numberWinSyntax);
                    }
                }

                ContentValues values = new ContentValues();
                values.put(WIN_NUMBER, win);

                db.update(TABLE_NAME, values, TIME + " =? AND " + PHONE + " =?", new String[]{time, phone});
            }

            cursor.close();

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    public void updateWinMessage(String phone, String time, String messageType, String numberWinSyntax) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(ANALYZE_CONTENT, numberWinSyntax);

            db.update(TABLE_NAME, values, TIME + " =? AND " + PHONE + " =? AND " + TYPE + " =?", new String[]{time, phone, messageType});

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    public void removeAllWinNumber(String phone, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(/*WIN_NUMBER*/ANALYZE_CONTENT, "");

        db.update(TABLE_NAME, values, DATE + " =? AND " + PHONE + " =?", new String[]{date, phone});
    }

    //---------------
    public int updateAnalyzeResult(String phone, String time, AnalyzeModel object) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DE, object.getDe());
        values.put(LO, object.getLo());
        values.put(XIEN2, object.getXien2());
        values.put(XIEN3, object.getXien3());
        values.put(XIEN4, object.getXien4());
        values.put(BACANG, object.getBacang());

        return db.update(TABLE_NAME, values, TIME + " =? AND " + PHONE + " =?", new String[]{time, phone});
    }

    public int updateWinMessage(String phone, String time, String winmessage) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WIN_MESSAGE, winmessage);

        return db.update(TABLE_NAME, values, TIME + " =? AND " + PHONE + " =?", new String[]{time, phone});
    }


    //---------------------
    public void insertReceivedTable(ReceivedModel model) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues values = getValueFromAnalyzeModel(model);
            insertReceived(db, model, values);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void insertReceived(SQLiteDatabase db, ReceivedModel model, ContentValues values) {

        if (validateReceivedExist(db, model))
        {
            Log.d("FollowDatabase", "insertReceived: " + "hasExist" + " phone= " + values.getAsString("phone"));
            db.update(RECEIVED_TABLE, values,
                    "phone =? AND time =? ",
                    new String[]{model.getPhone(), model.getTime()});
        }
        else
        {
            Log.d("FollowDatabase", "insertReceived: " + "addNew" + " phone= " + values.getAsString("phone"));
            db.insert(RECEIVED_TABLE, null, values);
        }
    }

    private boolean validateReceivedExist(SQLiteDatabase db, ReceivedModel model) {
        Cursor cursor = db.query(RECEIVED_TABLE, null,
                RECEIVED_TIME + " =? AND " + RECEIVED_PHONE + " =?",
                new String[]{model.getTime(), model.getPhone()},
                null, null, null);

        boolean result = (cursor == null || cursor.getCount() == 0);

        if (cursor != null) cursor.close();

        return !result;
    }

    private ContentValues getValueFromAnalyzeModel(ReceivedModel model) {
        ContentValues values = new ContentValues();
        values.put(RECEIVED_NAME, model.getName());
        values.put(RECEIVED_PHONE, model.getPhone());
        values.put(RECEIVED_TIME, model.getTime());
        values.put(RECEIVED_DATE, Utils.convertDate(model.getTime()));
        values.put(RECEIVED_TYPE_MESSAGE, model.getTypemessage());
        values.put(RECEIVED_ACTUAL_COLLECT_DE, model.getActualcollect_de());
        values.put(RECEIVED_ACTUAL_COLLECT_LO, model.getActualcollect_lo());
        values.put(RECEIVED_ACTUAL_COLLECT_XIEN2, model.getActualcollect_xien2());
        values.put(RECEIVED_ACTUAL_COLLECT_XIEN3, model.getActualcollect_xien3());
        values.put(RECEIVED_ACTUAL_COLLECT_XIEN4, model.getActualcollect_xien4());
        values.put(RECEIVED_ACTUAL_COLLECT_BACANG, model.getActualcollect_bacang());
        values.put(RECEIVED_DE_WIN, model.getDe_win());
        values.put(RECEIVED_LO_WIN, model.getLo_win());
        values.put(RECEIVED_LO_POINT_WIN, model.getLo_point_win());
        values.put(RECEIVED_XIEN2_WIN, model.getXien2_win());
        values.put(RECEIVED_XIEN3_WIN, model.getXien3_win());
        values.put(RECEIVED_XIEN4_WIN, model.getXien4_win());
        values.put(RECEIVED_BACANG_WIN, model.getBacang_win());
        values.put(RECEIVED_BACANG_WIN, model.getBacang_win());
        values.put(RECEIVED_DETAIL, model.getDetail().toString());

        return values;
    }

    public void updateResult(ReceivedModel model) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();

            values.put(RECEIVED_DE_WIN, model.getDe_win());
            values.put(RECEIVED_LO_WIN, model.getLo_win());
            values.put(RECEIVED_LO_POINT_WIN, model.getLo_point_win());
            values.put(RECEIVED_XIEN2_WIN, model.getXien2_win());
            values.put(RECEIVED_XIEN3_WIN, model.getXien3_win());
            values.put(RECEIVED_XIEN4_WIN, model.getXien4_win());
            values.put(RECEIVED_BACANG_WIN, model.getBacang_win());
            values.put(RECEIVED_BACANG_WIN, model.getBacang_win());

            db.update(RECEIVED_TABLE, values, RECEIVED_PHONE + " =? AND " + RECEIVED_TIME + " =? AND " + RECEIVED_TYPE_MESSAGE + " =?",
                    new String[]{model.getPhone(), model.getTime(), model.getTypemessage()});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getCustomerDetail() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT message._id as _id, message.phone, message.name, message.type, " +
                    " (SUM(message.de)) as sumde, " +
                    " (SUM(message.lo)) as sumlo, " +
                    " (SUM(message.xien2)) as sumxien2, " +
                    " (SUM(message.xien3)) as sumxien3, " +
                    " (SUM(message.xien4)) as sumxien4, " +
                    " (SUM(message.bacang)) as sumbacang, " +
                    " (SUM(received_table.actualcollectde)) as sumactde, " +
                    " (SUM(received_table.actualcollectlo)) as sumactlo, " +
                    " (SUM(received_table.actualcollectxien2)) as sumactxien2, " +
                    " (SUM(received_table.actualcollectxien3)) as sumactxien3, " +
                    " (SUM(received_table.actualcollectxien4)) as sumactxien4, " +
                    " (SUM(received_table.actualcollectbacang)) as sumactbacang, " +
                    " (SUM(received_table.dewin)) as sumwinde, " +
                    " (SUM(received_table.lowin)) as sumwinlo, " +
                    " (SUM(received_table.lopointwin)) as sumpointwinlo, " +
                    " (SUM(received_table.xien2win)) as sumwinxien2, " +
                    " (SUM(received_table.xien3win)) as sumwinxien3, " +
                    " (SUM(received_table.xien4win)) as sumwinxien4, " +
                    " (SUM(received_table.bacangwin)) as sumwinbacang " +
                    " FROM message JOIN received_table ON message.phone = received_table.phone AND message.time = received_table.time " +
                    " WHERE received_table.date = ? AND received_table.typemessage = ? GROUP BY received_table.phone " +
                    " UNION ALL " +
                    " SELECT received_table._id as _id, received_table.phone, message.name, message.type, " +
                    " (SUM(message.de)) as sumde, " +
                    " (SUM(message.lo)) as sumlo, " +
                    " (SUM(message.xien2)) as sumxien2, " +
                    " (SUM(message.xien3)) as sumxien3, " +
                    " (SUM(message.xien4)) as sumxien4, " +
                    " (SUM(message.bacang)) as sumbacang, " +
                    " (SUM(received_table.actualcollectde)) as sumactde, " +
                    " (SUM(received_table.actualcollectlo)) as sumactlo, " +
                    " (SUM(received_table.actualcollectxien2)) as sumactxien2, " +
                    " (SUM(received_table.actualcollectxien3)) as sumactxien3, " +
                    " (SUM(received_table.actualcollectxien4)) as sumactxien4, " +
                    " (SUM(received_table.actualcollectbacang)) as sumactbacang, " +
                    " (SUM(received_table.dewin)) as sumwinde, " +
                    " (SUM(received_table.lowin)) as sumwinlo, " +
                    " (SUM(received_table.lopointwin)) as sumpointwinlo, " +
                    " (SUM(received_table.xien2win)) as sumwinxien2, " +
                    " (SUM(received_table.xien3win)) as sumwinxien3, " +
                    " (SUM(received_table.xien4win)) as sumwinxien4, " +
                    " (SUM(received_table.bacangwin)) as sumwinbacang " +
                    " FROM message JOIN received_table ON message.phone = received_table.phone AND message.time = received_table.time " +
                    " WHERE received_table.date = ? AND received_table.typemessage = ? GROUP BY received_table.phone ORDER BY message.type DESC, message.name";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), TypeMessage.SENT.name(), Utils.getCurrentDate(), TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getRemainDetail() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT message._id, received_table.typemessage, " +
                    " (SUM(message.de)) as sumde, " +
                    " (SUM(message.lo)) as sumlo, " +
                    " (SUM(message.xien2)) as sumxien2, " +
                    " (SUM(message.xien3)) as sumxien3, " +
                    " (SUM(message.xien4)) as sumxien4, " +
                    " (SUM(message.bacang)) as sumbacang, " +
                    " (SUM(received_table.actualcollectde)) as sumactde, " +
                    " (SUM(received_table.actualcollectlo)) as sumactlo, " +
                    " (SUM(received_table.actualcollectxien2)) as sumactxien2, " +
                    " (SUM(received_table.actualcollectxien3)) as sumactxien3, " +
                    " (SUM(received_table.actualcollectxien4)) as sumactxien4, " +
                    " (SUM(received_table.actualcollectbacang)) as sumactbacang, " +
                    " (SUM(received_table.dewin)) as sumwinde, " +
                    " (SUM(received_table.lowin)) as sumwinlo, " +
                    " (SUM(received_table.lopointwin)) as sumpointwinlo, " +
                    " (SUM(received_table.xien2win)) as sumwinxien2, " +
                    " (SUM(received_table.xien3win)) as sumwinxien3, " +
                    " (SUM(received_table.xien4win)) as sumwinxien4, " +
                    " (SUM(received_table.bacangwin)) as sumwinbacang " +
                    " FROM message JOIN received_table ON message.phone = received_table.phone AND message.time = received_table.time " +
                    " WHERE received_table.date = ? GROUP BY received_table.typemessage ORDER BY received_table.typemessage";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate()});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public JSONArray getMapNumberFromMessage(String phone, String time) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM received_table " +
                    " WHERE phone = ? AND time = ?";

            Cursor cursor = db.rawQuery(query, new String[]{phone, time});

            JSONArray array;

            if (cursor.moveToFirst())
            {
                String detail = cursor.getString(cursor.getColumnIndex(RECEIVED_DETAIL));
                array = new JSONArray(detail);
            }
            else
            {
                array = new JSONArray();
            }

            cursor.close();
            db.setTransactionSuccessful();

            return array;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllMessageFromPhone(String phone, String date) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM received_table " +
                    " WHERE date = ? AND phone = ?";

            Cursor cursor = db.rawQuery(query, new String[]{date, phone});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllReceivedMessage(String date) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM received_table " +
                    " WHERE date = ? AND typemessage = ? ORDER BY phone";

            Cursor cursor = db.rawQuery(query, new String[]{date, TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllMessageInDB(String date) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM received_table " +
                    " WHERE date = ? ORDER BY phone";

            Cursor cursor = db.rawQuery(query, new String[]{date});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getReceivedMessageFromPhone(String phone, String date) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT *" +
                    " FROM received_table " +
                    " WHERE date = ? AND phone = ? AND typemessage = ?";

            Cursor cursor = db.rawQuery(query, new String[]{date, phone, TypeMessage.RECEIVED.name()});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getResultCalculate() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT message._id, message.name, message.phone, received_table.typemessage, " +
                    " (SUM(message.de)) as sumde, " +
                    " (SUM(message.lo)) as sumlo, " +
                    " (SUM(message.xien2)) as sumxien2, " +
                    " (SUM(message.xien3)) as sumxien3, " +
                    " (SUM(message.xien4)) as sumxien4, " +
                    " (SUM(message.bacang)) as sumbacang, " +
                    " (SUM(received_table.actualcollectde)) as sumactde, " +
                    " (SUM(received_table.actualcollectlo)) as sumactlo, " +
                    " (SUM(received_table.actualcollectxien2)) as sumactxien2, " +
                    " (SUM(received_table.actualcollectxien3)) as sumactxien3, " +
                    " (SUM(received_table.actualcollectxien4)) as sumactxien4, " +
                    " (SUM(received_table.actualcollectbacang)) as sumactbacang, " +
                    " (SUM(received_table.dewin)) as sumwinde, " +
                    " (SUM(received_table.lowin)) as sumwinlo, " +
                    " (SUM(received_table.lopointwin)) as sumpointwinlo, " +
                    " (SUM(received_table.xien2win)) as sumwinxien2, " +
                    " (SUM(received_table.xien3win)) as sumwinxien3, " +
                    " (SUM(received_table.xien4win)) as sumwinxien4, " +
                    " (SUM(received_table.bacangwin)) as sumwinbacang " +
                    " FROM message JOIN received_table ON message.phone = received_table.phone AND message.time = received_table.time " +
                    " WHERE received_table.date = ? GROUP BY received_table.phone ORDER BY received_table.phone";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate()});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getResultCalculate(String phone) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT message._id, message.name, message.phone, received_table.typemessage, " +
                    " (SUM(message.de)) as sumde, " +
                    " (SUM(message.lo)) as sumlo, " +
                    " (SUM(message.xien2)) as sumxien2, " +
                    " (SUM(message.xien3)) as sumxien3, " +
                    " (SUM(message.xien4)) as sumxien4, " +
                    " (SUM(message.bacang)) as sumbacang, " +
                    " (SUM(received_table.actualcollectde)) as sumactde, " +
                    " (SUM(received_table.actualcollectlo)) as sumactlo, " +
                    " (SUM(received_table.actualcollectxien2)) as sumactxien2, " +
                    " (SUM(received_table.actualcollectxien3)) as sumactxien3, " +
                    " (SUM(received_table.actualcollectxien4)) as sumactxien4, " +
                    " (SUM(received_table.actualcollectbacang)) as sumactbacang, " +
                    " (SUM(received_table.dewin)) as sumwinde, " +
                    " (SUM(received_table.lowin)) as sumwinlo, " +
                    " (SUM(received_table.lopointwin)) as sumpointwinlo, " +
                    " (SUM(received_table.xien2win)) as sumwinxien2, " +
                    " (SUM(received_table.xien3win)) as sumwinxien3, " +
                    " (SUM(received_table.xien4win)) as sumwinxien4, " +
                    " (SUM(received_table.bacangwin)) as sumwinbacang " +
                    " FROM message JOIN received_table ON message.phone = received_table.phone AND message.time = received_table.time " +
                    " WHERE received_table.date = ? AND received_table.phone = ?";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), phone});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllContact() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE date = ? AND analyze = ? GROUP BY phone ORDER BY type DESC";

            Cursor cursor = db.rawQuery(query, new String[]{Utils.getCurrentDate(), "1"});

            db.setTransactionSuccessful();
            return cursor;
        } finally {
            db.endTransaction();
        }
    }
}
