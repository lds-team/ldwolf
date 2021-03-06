package bingo.com.helperdb.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

import bingo.com.helperdb.ContactColumnMaps;
import bingo.com.model.ContactModel;

public class ContactDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "contactdatabase";
    private static final String TABLE_NAME = "contact";

    private static final String KEY_ID = "_id";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String GIU_DE = "giu_de";
    public static final String GIU_LO = "giu_lo";

    public static final String GIU_BACANG = "giu_bacang";
    public static final String GIU_XIEN2 = "giu_xien2";
    public static final String GIU_XIEN3 = "giu_xien3";
    public static final String GIU_XIEN4 = "giu_xien4";

    public static final String GIU_DIEM_XIEN2 = "giu_diem_xien2";
    public static final String GIU_DIEM_XIEN3 = "giu_diem_xien3";
    public static final String GIU_DIEM_XIEN4 = "giu_diem_xien4";
    public static final String GIU_DIEM_BACANG = "giu_diem_bacang";

    public static final String MAX_DE = "max_de";
    public static final String MAX_LO = "max_lo";
    public static final String MAX_XIEN2 = "max_xien2";
    public static final String MAX_XIEN3 = "max_xien3";
    public static final String MAX_XIEN4 = "max_xien4";
    public static final String MAX_BACANG = "max_bacang";

    @Deprecated
    private static final String DAUDB = "daudb";
    @Deprecated
    private static final String DAUDB_LANAN = "daudb_lanan";
    public static final String DITDB = "ditdb";
    public static final String DITDB_LANAN = "ditdb_lanan";
    @Deprecated
    private static final String DAUNHAT = "daunhat";
    @Deprecated
    private static final String DAUNHAT_LANAN = "daunhat_lanan";
    @Deprecated
    private static final String DITNHAT = "ditnhat";
    @Deprecated
    private static final String DITNHAT_LANAN = "ditnhat_lanan";
    public static final String LO = "lo";
    public static final String LO_LANAN = "lo_lanan";
    public static final String XIEN2 = "xien2";
    public static final String XIEN2_LANAN = "xien2_lanan";
    public static final String XIEN3 = "xien3";
    public static final String XIEN3_LANAN = "xien3_lanan";
    public static final String XIEN4 = "xien4";
    public static final String XIEN4_LANAN = "xien4_lanan";
    public static final String BACANG = "bacang";
    public static final String BACANG_LANAN = "bacang_lanan";
    public static final String TYPE = "type";

    public static final String TYPE_KHACH_CHUYEN_SO = "0";
    public static final String TYPE_CHU_NHAN_SO = "1";

    public String[] columnsHeso = new String[]{DAUDB, DAUDB_LANAN, DITDB, DITDB_LANAN, DAUNHAT, DAUNHAT_LANAN, DITNHAT, DITNHAT_LANAN,
            LO, LO_LANAN, XIEN2, XIEN2_LANAN, XIEN3, XIEN3_LANAN, XIEN4, XIEN4_LANAN, BACANG, BACANG_LANAN};

    String CREATE_CONTACTS_TABLE = "CREATE TABLE "
            + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME + " TEXT,"
            + PHONE + " TEXT,"
            + GIU_DE + " TEXT,"
            + GIU_LO + " TEXT,"
            + GIU_BACANG + " TEXT,"
            + GIU_XIEN2 + " TEXT,"
            + GIU_XIEN3 + " TEXT,"
            + GIU_XIEN4 + " TEXT,"
            + GIU_DIEM_XIEN2 + " TEXT,"
            + GIU_DIEM_XIEN3 + " TEXT,"
            + GIU_DIEM_XIEN4 + " TEXT,"
            + GIU_DIEM_BACANG + " TEXT,"
            + MAX_DE + " TEXT,"
            + MAX_LO + " TEXT,"
            + MAX_XIEN2 + " TEXT,"
            + MAX_XIEN3 + " TEXT,"
            + MAX_XIEN4 + " TEXT,"
            + MAX_BACANG + " TEXT,"
            + DAUDB + " TEXT,"
            + DAUDB_LANAN + " TEXT,"
            + DITDB + " TEXT,"
            + DITDB_LANAN + " TEXT,"
            + DAUNHAT + " TEXT,"
            + DAUNHAT_LANAN + " TEXT,"
            + DITNHAT + " TEXT,"
            + DITNHAT_LANAN + " TEXT,"
            + LO + " TEXT,"
            + LO_LANAN + " TEXT,"
            + XIEN2 + " TEXT,"
            + XIEN2_LANAN + " TEXT,"
            + XIEN3 + " TEXT,"
            + XIEN3_LANAN + " TEXT,"
            + XIEN4 + " TEXT,"
            + XIEN4_LANAN + " TEXT,"
            + BACANG + " TEXT,"
            + BACANG_LANAN + " TEXT,"
            + TYPE + " TEXT"
            + ")";

    public ContactDatabase(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void addContact(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            db.insert(TABLE_NAME, null, values);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateContact(String phone, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            db.update(TABLE_NAME, values, PHONE + " =?", new String[]{phone});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateAllContact(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            db.update(TABLE_NAME, values, null, null);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteContact(String phone) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            db.delete(TABLE_NAME, PHONE + " =?", new String[]{phone});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAlls() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, TYPE + " DESC," + NAME + " ASC");

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public String getRandomPhone() {
        SQLiteDatabase db = getReadableDatabase();

        String phone = null;

        try {
            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToLast())
                phone = cursor.getString(ContactColumnMaps.PHONE);

            cursor.close();

            db.setTransactionSuccessful();

            return phone;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAlls(String type) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, TYPE + " =?", new String[]{type}, null, null, NAME + " ASC");

            db.setTransactionSuccessful();

            return cursor;
        } finally {
            db.endTransaction();
        }
    }

    public boolean isExist(String phone) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            boolean isExist = false;

            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID}, PHONE + " =?", new String[]{phone}, null, null, null);

            if (cursor == null || cursor.getCount() == 0)
                isExist = false;
            else
                isExist = true;

            cursor.close();
            db.setTransactionSuccessful();

            return isExist;
        } finally {
            db.endTransaction();
        }
    }

    public HashMap<String, ContactModel> getFullContact() {
        SQLiteDatabase db = this.getReadableDatabase();

        HashMap<String, ContactModel> contact = new HashMap<>();

        try {
            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, null,
                    null, null, null, null, null);

            int count = cursor.getCount();
            if (count > 0) {
                for (int i = 0; i < count; i++)
                {
                    cursor.moveToPosition(i);
                    ContactModel myContact = new ContactModel();
                    myContact.loadFromCursor(cursor);
                    contact.put(myContact.getPhone(), myContact);
                }
            }

            cursor.close();

            db.setTransactionSuccessful();
            return contact;
        } finally {
            db.endTransaction();
        }
    }

    public ContactModel getFullContact(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, PHONE + "=? OR " + NAME + " LIKE ?",
                    new String[]{phone, "%" + phone + "%"}, null, null, null, null);

            ContactModel myContact = null;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                myContact = new ContactModel();
                myContact.loadFromCursor(cursor);
            }

            if (cursor != null)
                cursor.close();

            db.setTransactionSuccessful();
            return myContact;
        } finally {
            db.endTransaction();
        }
    }

    public HashMap<Integer, Double> getHesoGiu(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        /*try {
            db.beginTransaction();*/

            Cursor cursor = db.query(TABLE_NAME, null, PHONE + "=? OR " + NAME + " LIKE ?",
                    new String[]{phone, "%" + phone + "%"}, null, null, null, null);

            HashMap<Integer, Double> heso = new HashMap<>();

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                heso.put(ContactColumnMaps.GIU_DE, cursor.getDouble(ContactColumnMaps.GIU_DE));
                heso.put(ContactColumnMaps.GIU_LO, cursor.getDouble(ContactColumnMaps.GIU_LO));
                heso.put(ContactColumnMaps.GIU_BACANG, cursor.getDouble(ContactColumnMaps.GIU_BACANG));
                heso.put(ContactColumnMaps.GIU_XIEN2, cursor.getDouble(ContactColumnMaps.GIU_XIEN2));
                heso.put(ContactColumnMaps.GIU_XIEN3, cursor.getDouble(ContactColumnMaps.GIU_XIEN3));
                heso.put(ContactColumnMaps.GIU_XIEN4, cursor.getDouble(ContactColumnMaps.GIU_XIEN4));
                heso.put(ContactColumnMaps.GIU_DIEM_XIEN2, cursor.getDouble(ContactColumnMaps.GIU_DIEM_XIEN2));
                heso.put(ContactColumnMaps.GIU_DIEM_XIEN3, cursor.getDouble(ContactColumnMaps.GIU_DIEM_XIEN3));
                heso.put(ContactColumnMaps.GIU_DIEM_XIEN4, cursor.getDouble(ContactColumnMaps.GIU_DIEM_XIEN4));
                heso.put(ContactColumnMaps.GIU_DIEM_BACANG, cursor.getDouble(ContactColumnMaps.GIU_DIEM_BACANG));
                heso.put(ContactColumnMaps.MAX_DE, cursor.getDouble(ContactColumnMaps.MAX_DE));
                heso.put(ContactColumnMaps.MAX_LO, cursor.getDouble(ContactColumnMaps.MAX_LO));
                heso.put(ContactColumnMaps.MAX_XIEN2, cursor.getDouble(ContactColumnMaps.MAX_XIEN2));
                heso.put(ContactColumnMaps.MAX_XIEN3, cursor.getDouble(ContactColumnMaps.MAX_XIEN3));
                heso.put(ContactColumnMaps.MAX_XIEN4, cursor.getDouble(ContactColumnMaps.MAX_XIEN4));
                heso.put(ContactColumnMaps.MAX_BACANG, cursor.getDouble(ContactColumnMaps.MAX_BACANG));
            } else {
                heso.put(ContactColumnMaps.GIU_DE, 0d);
                heso.put(ContactColumnMaps.GIU_LO, 0d);
                heso.put(ContactColumnMaps.GIU_BACANG, 0d);
                heso.put(ContactColumnMaps.GIU_XIEN2, 0d);
                heso.put(ContactColumnMaps.GIU_XIEN3, 0d);
                heso.put(ContactColumnMaps.GIU_XIEN4, 0d);
                heso.put(ContactColumnMaps.GIU_DIEM_XIEN2, 0d);
                heso.put(ContactColumnMaps.GIU_DIEM_XIEN3, 0d);
                heso.put(ContactColumnMaps.GIU_DIEM_XIEN4, 0d);
                heso.put(ContactColumnMaps.GIU_DIEM_BACANG, 0d);
                heso.put(ContactColumnMaps.MAX_DE, 0d);
                heso.put(ContactColumnMaps.MAX_LO, 0d);
                heso.put(ContactColumnMaps.MAX_XIEN2, 0d);
                heso.put(ContactColumnMaps.MAX_XIEN3, 0d);
                heso.put(ContactColumnMaps.MAX_XIEN4, 0d);
                heso.put(ContactColumnMaps.MAX_BACANG, 0d);
            }

            if (cursor != null)
                cursor.close();

            /*db.setTransactionSuccessful();*/
            return heso;
        /*} finally {
            db.endTransaction();
        }*/
    }

    public String getName(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        String name = null;

        try {
            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, PHONE + "=? OR " + NAME + " LIKE ?",
                    new String[]{phone, "%" + phone + "%"}, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                name = cursor.getString(ContactColumnMaps.NAME);
            }

            if (cursor != null)
                cursor.close();

            db.setTransactionSuccessful();
            return name;
        } finally {
            db.endTransaction();
        }
    }

    public String getNameKhachChuyen(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        String name = null;

        try {
            db.beginTransaction();

            Cursor cursor = db.query(TABLE_NAME, null, TYPE + " =? AND " + PHONE + " =?",
                    new String[]{"0", phone}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(ContactColumnMaps.NAME);
            }

            if (cursor != null)
                cursor.close();

            db.setTransactionSuccessful();
            return name;
        } finally {
            db.endTransaction();
        }
    }
}
