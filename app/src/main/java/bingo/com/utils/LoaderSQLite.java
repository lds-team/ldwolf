package bingo.com.utils;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import bingo.com.callbacks.LoadSuccessListener;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.LotoMsgHelper;
import bingo.com.helperdb.db.LotoMsgProvider;
import bingo.com.model.ContactModel;
import bingo.com.model.ResultModel;
import bingo.com.model.ResultReturnModel;
import bingo.com.service.AnalyzeService;

import static android.provider.ContactsContract.PhoneLookup;

public class LoaderSQLite {

    /**
     * Tra ve ket qua boc tach theo ngay.
     *
     * @param date Ngay muon lay ket qua. Format : dd/MM.
     * @param year Nam muon lay ket qua. Neu su dung nam hien tai: format : yyyy, year = null.
     * @param split Co tach lay 2 so cuoi khong.
     * @param callback callback tra ve ket qua.
     */
    public static void getResultFromDate(ContentResolver resolver, @NonNull String date, String year, final boolean split, LoadSuccessListener callback) {

        final WeakReference<LoadSuccessListener> mDataLoadListener = new WeakReference<LoadSuccessListener>(callback);

        WeakReference<AsyncQueryHandler> mHandleQuery = new WeakReference<AsyncQueryHandler>(new AsyncQueryHandler(resolver) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if (mDataLoadListener.get() != null)
                {
                    if (cursor != null && cursor.moveToFirst())
                    {
                        String body = cursor.getString(cursor.getColumnIndex(LotoMsgProvider.Body));
                        String result = cursor.getString(cursor.getColumnIndex(LotoMsgProvider.Body_Separated));

                        mDataLoadListener.get().onReceiveResult(body, ResultModel.extractAlls(result, split));
                    }
                    else
                    {
                        mDataLoadListener.get().onReceiveResult(null, null);
                    }

                    mDataLoadListener.clear();

                    if (cursor != null)
                    {
                        cursor.close();
                    }
                }
            }
        });

        if (year == null || year.equals("")) year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        mHandleQuery.get().startQuery(0,
                null,
                getUriForQueryLoto(year),
                null,
                LotoMsgProvider.Date + "=?",
                new String[]{date},
                null);
    }

    public static ResultReturnModel getResultFromDate(Context context, String date, String year, final boolean split) {
        CursorLoader loader = new CursorLoader(context,
                getUriForQueryLoto(year),
                null,
                LotoMsgProvider.Date + "=?",
                new String[]{date},
                null);

        Cursor cursor = loader.loadInBackground();

         if (cursor != null && cursor.getCount() == 1)
        {
            cursor.moveToFirst();

            final String body = cursor.getString(cursor.getColumnIndex(LotoMsgProvider.Body));
            final String result = cursor.getString(cursor.getColumnIndex(LotoMsgProvider.Body_Separated));

            cursor.close();
            closeOpenDb(context);
            return new ResultReturnModel(body, ResultModel.extractAlls(result, split));
        }

        if (cursor != null) cursor.close();
        closeOpenDb(context);
//        return null;

        //TODO
        return new ResultReturnModel("", null);
    }

    public static Uri getUriForQueryLoto(String year)
    {

        return LotoMsgHelper.getUriWithYear(year);
    }

    private static void closeOpenDb(Context context) {
        CursorLoader loaderClose = new CursorLoader(context, getUriForQueryLoto("0000"), null, null, null, null);
        loaderClose.loadInBackground();
    }

    /**
     * Get Contact Config Parameter.
     *
     * @param context only use ApplicationContext.
     * @param phoneOrName phone or name in app. Name can the same.
     * @return contact params.
     */
    public static ContactModel getConfigParameter(String phoneOrName) {
        ContactModel config =  ContactDBHelper.getFullContact(phoneOrName);

        if (config == null)
        {
            config = Default_Heso.loadDefaultContact();
        }

        return config;
    }

    public static String getContactName(Context context, String phoneNumber, String iferror) {
        String contactName = iferror;

        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);

        if (cursor == null) {
            return contactName;
        }

        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public static void startAnalyzing(Context context, String name, String phone, String message, long time, String type, ResultReturnModel[]results) {
        startAnalyzing(context, name, phone, message, time, type, null, false);
    }

    public static void startForceAnalyzing(Context context, String name, String phone, String message, long time, String type, ResultReturnModel[]results) {
        startAnalyzing(context, name, phone, message, time, type, null, true);
    }

    private static void startAnalyzing(Context context, String name, String phone, String message, long time, String type, ResultReturnModel[]results, boolean forceAdd) {
        Log.d("LoaderSQLite", "Start Analyze: " + "from: " + phone + " body: " + message + " time: " + time);

        Intent intent = new Intent(context, AnalyzeService.class);
        /*intent.setAction()*/
        intent.putExtra(AnalyzeService.NAME, name == null ? ContactDBHelper.getName(phone) : name);
        intent.putExtra(AnalyzeService.PHONE, phone);
        intent.putExtra(AnalyzeService.MESSAGE, message);
        intent.putExtra(AnalyzeService.TIME, time);
        intent.putExtra(AnalyzeService.TYPE, type);
        /*intent.putExtra(AnalyzeService.RESULT, results);*/
        intent.putExtra(AnalyzeService.FORCE_ADD, forceAdd);
        context.startService(intent);
    }
}
