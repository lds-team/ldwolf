package bingo.com.helperdb;

import android.content.Context;
import android.database.Cursor;

import bingo.com.helperdb.db.CongnoDatabase;
import bingo.com.model.CongnoModel;

public class CongnoDBHelper {

    private static CongnoDatabase database;

    public static CongnoDatabase init(Context context) {

        synchronized (CongnoDBHelper.class)
        {
            if (database == null)
            {
                database = new CongnoDatabase(context);
            }
        }

        return database;
    }

    public static void closeDB() {

        synchronized (CongnoDBHelper.class)
        {
            if(database != null )
            {
                database.close();
            }

            database = null;
        }
    }

    public static void insert(CongnoModel... model) {
        database.insert(model);
    }

    public static void insertWithOutConflict(CongnoModel... model) {
        database.insertWithOutConflict(model);
    }

    public static Cursor getCongNoByPhone(String phone) {
        return database.getCongNoByPhone(phone);
    }

    public static Cursor getAllCongno() {
        return database.getAllCongNo();
    }

    public static double getNoCu(String phone) {
        return database.getNoCu(phone);
    }

    public static double getNoCuTodate(String phone, String date) {
        return database.getNoCuTodate(phone, date);
    }

    public static Cursor getCongNoHistory(String phone, String date) {
        return database.getCongNoHistory(phone, date);
    }

    public static Cursor getPhoneGroupDate(String phone) {
        return database.getPhoneGroupDate(phone);
    }

    public static void deleteAlls() {
        database.deleteAlls();
    }

    public static void eraseAlls() {
        database.eraseAlls();
    }

    public static void deleteCongNo(String phone) {
        database.deleteCongNo(phone);
    }

    public static void deleteAllCustomerCongNo(String phone) {
        database.deleteAllCustomerCongNo(phone);
    }
}
