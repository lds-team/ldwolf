package bingo.com.helperdb.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;


/**
 * Created by nvan on 10/6/2016.
 */

public class LotoMsgProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.bingo.provider.Loto";
    static final String URL = "content://" + PROVIDER_NAME + "/lotomsgs";
    public static final Uri CONTENT_URI = Uri.parse(URL);
    public String DB_PATH = "";

    public static final String _ID = "_id";
    public static final String Address = "address";
    public static final String Date = "date";
    public static final String Body = "body";
    public static final String Body_Separated = "bodyseparated";

    private static HashMap<String, String> LOTOMSGS_PROJECTION_MAP;

    static final int LOGOMSGS = 1;
    static final int LOGOMSGS_ID = 2;
    //This Uri just for query. Other = exception.
    @SuppressWarnings("Maybe Exception")
    static final int LOGOMSGS_YEAR = 3;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "lotomsgs", LOGOMSGS);
        uriMatcher.addURI(PROVIDER_NAME, "lotomsgs/#", LOGOMSGS_ID);
        uriMatcher.addURI(PROVIDER_NAME, "lotomsgs/year/#", LOGOMSGS_YEAR);
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    private SQLiteDatabase yearDatabase;
    static final String DATABASE_NAME = "Loto" + Calendar.getInstance().get(Calendar.YEAR);
    static final String LOGOMSGS_TABLE_NAME = "lotomsgs";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + LOGOMSGS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " address TEXT NOT NULL, " +
                    " date TEXT NOT NULL, " +
                    " body TEXT NOT NULL, " +
                    " bodyseparated TEXT NOT NULL);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + LOGOMSGS_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        /**
         * Add a new student record
         */
        long rowID = db.insert(LOGOMSGS_TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */
        //No need to notify updated.
        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            /*getContext().getContentResolver().notifyChange(_uri, null);*/
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(LOGOMSGS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case LOGOMSGS:
                qb.setProjectionMap(LOTOMSGS_PROJECTION_MAP);
                break;

            case LOGOMSGS_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            case LOGOMSGS_YEAR:
                String year = uri.getPathSegments().get(2);

                if (year.equals("0000") && yearDatabase != null)
                {
                    yearDatabase.close();
                    return null;
                }

                if (checkDataBase("Loto" + year))
                {
                    yearDatabase = SQLiteDatabase.openDatabase(DB_PATH + "Loto" + year, null, SQLiteDatabase.OPEN_READONLY);

                    Cursor q = yearDatabase.query(LOGOMSGS_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                    q.setNotificationUri(getContext().getContentResolver(), uri);

                    return q;
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on student names
             */
            sortOrder = Date;
        }
        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case LOGOMSGS:
                count = db.delete(LOGOMSGS_TABLE_NAME, selection, selectionArgs);
                break;

            case LOGOMSGS_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(LOGOMSGS_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case LOGOMSGS:
                count = db.update(LOGOMSGS_TABLE_NAME, values, selection, selectionArgs);
                break;

            case LOGOMSGS_ID:
                count = db.update(LOGOMSGS_TABLE_NAME, values, _ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            case LOGOMSGS_YEAR:
                String year = uri.getPathSegments().get(2);
                if (checkDataBase("Loto" + year))
                {
                    SQLiteDatabase database = SQLiteDatabase.openDatabase(DB_PATH + "Loto" + year, null, SQLiteDatabase.OPEN_READWRITE);
                    count = database.update(LOGOMSGS_TABLE_NAME, values, selection, selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case LOGOMSGS:
                return URL;

            case LOGOMSGS_ID:
                return URL + "/#";

            case LOGOMSGS_YEAR:
                return URL + "/year/#";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    private boolean checkDataBase(String dbName)
    {
        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = getContext().getApplicationInfo().dataDir + "/databases/";
        }
        else
        {
            DB_PATH = getContext().getFilesDir().getPath() + getContext().getPackageName() + "/databases/";
        }

        File dbFile = new File(DB_PATH + dbName);
        //Log.v("TrungNH", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }
}
