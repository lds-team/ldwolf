package bingo.com.async;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bingo.com.callbacks.UpdateSync;
import bingo.com.controller.ConfigControl;
import bingo.com.controller.wrapper.ObjectWrapper;
import bingo.com.enumtype.LotoType;
import bingo.com.helperdb.ContactColumnMaps;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db.MessageDatabase;
import bingo.com.helperdb.db2.FollowDBHelper2;
import bingo.com.helperdb.db2.FollowDatabase2;
import bingo.com.model.ContactModel;
import bingo.com.utils.Action;
import bingo.com.utils.JSONAnalyzeBuilder;
import bingo.com.utils.Utils;

public class TaskUpdate {

    public static class TaskUpdateKeepValue extends AsyncTask<String, Integer, String> {

        Context context;

        ProgressDialog progressDialog;

        Cursor cursor;

        String phone;

        String name;

        String date;

        String typeChanged;

        HashMap<Integer, Double> percentMaps;

        public TaskUpdateKeepValue(Context context, String name, String phone, String date, String[] typeChanged) {
            this.context = context;

            this.phone = phone;
            this.name = name;
            this.date = date;
            this.typeChanged = Arrays.toString(typeChanged);
        }

        @Override
        protected void onPreExecute() {

            FollowDBHelper2.getDatabase().deleteKeepValueByDate(phone, date);

            cursor = MessageDBHelper.getDb().getReceivedMessageFromPhone(phone, date);
            percentMaps = ContactDBHelper.getHesoGiu(phone);

            int max = cursor.getCount() + 5;

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Kill app khi hệ thống đang chạy sẽ gây lỗi dữ liệu.");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(max);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            doInCursor(cursor);

            return null;
        }

        private void doInCursor(Cursor cursor) {
            if (cursor == null || !cursor.moveToFirst())
            {
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                return;
            }

            final int count = cursor.getCount();

            try {

                List<ContentValues> valuesList = new ArrayList<>();

                for (int i = 0; i < count; i++)
                {
                    String time = cursor.getString(cursor.getColumnIndex(MessageDatabase.RECEIVED_TIME));
                    String detail = cursor.getString(cursor.getColumnIndex(MessageDatabase.RECEIVED_DETAIL));
                    JSONArray array = new JSONArray(detail);

                    int size = array.length();
                    for (int j = 0; j < size; j++)
                    {
                        JSONObject object = array.getJSONObject(j);

                        String type = object.getString(JSONAnalyzeBuilder.KEY_TYPE);
                        String number = object.getString(JSONAnalyzeBuilder.KEY_NUMBER);
                        String price = object.getString(JSONAnalyzeBuilder.KEY_PRICE);

                        if (!typeChanged.contains(type))
                        {
                            continue;
                        }

                        Double newHeso = percentMaps.get(ContactColumnMaps.getKeepColumn(LotoType.convert(type)));

                        int max = percentMaps.get(ContactColumnMaps.getMaxColumn(LotoType.convert(type))).intValue();

                        int newPrice = (int) (newHeso * Integer.parseInt(price));

                        ContentValues values = new ContentValues();
                        values.put(FollowDatabase2.KEEP_NAME, name);
                        values.put(FollowDatabase2.KEEP_PHONE, phone);
                        values.put(FollowDatabase2.KEEP_DATE, date);
                        values.put(FollowDatabase2.KEEP_NUMBER, number);
                        values.put(FollowDatabase2.KEEP_TIME, time);
                        values.put(FollowDatabase2.KEEP_TYPE, type);
                        values.put(FollowDatabase2.KEEP_PARENTTYPE, type.contains("xien") ? "xien" : type);
                        values.put(FollowDatabase2.KEEP_PRICE, newPrice);
                        values.put(FollowDatabase2.KEEP_ACTUAL, "0");
                        values.put("max", max);

                        valuesList.add(values);
                    }

                    cursor.moveToNext();
                }

                //Insert all in one section.
                final int[] i = {0};
                final int size = (int) (valuesList.size() /** 0.2*/);
                FollowDBHelper2.getDatabase().updateKeepValueAfterDeleteAll(valuesList, new UpdateSync() {
                    @Override
                    public void onUpdate() {
                        i[0]++;
                        int percent = (int) ((((double) i[0]) / ((double) size)) * 100d);

                        publishProgress(percent);
                    }
                });
            } catch (JSONException e) {
                publishProgress(-1);
            } finally {
                cursor.close();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int progress = values[0];

            if (progress == -1)
            {
                progressDialog.dismiss();

                Utils.showAlert(context, "Lỗi", "Có lõi xảy ra khi update giá trị giữ số.!", "Ok", null);
            }
            else
            {
                progressDialog.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("TaskHelper", "TaskLoadResultNumber: isCancelled: " + this.isCancelled() + " ,Status: " + this.getStatus());

            progressDialog.dismiss();

            Toast.makeText(context, "Giữ số thành công!", Toast.LENGTH_SHORT).show();
            requestUpdate();

            percentMaps.clear();
            progressDialog = null;
            context = null;
        }

        protected void requestUpdate() {
            if (context != null)
            {
                Intent intent = new Intent(Action.ACTION_REQUEST_UPDATE);
                context.sendBroadcast(intent);
            }
        }
    }

    public static class TaskUpdateKeepValueS2 extends AsyncTask<String, Integer, String> {

        Context context;

        ProgressDialog progressDialog;

        Cursor cursor;

        String phone;

        String name;

        String date;

        String typeChanged;

        ContactModel config;

        ObjectWrapper wrapper;

        public TaskUpdateKeepValueS2(Context context, String name, String phone, String date, String[] typeChanged) {
            this.context = context;

            this.phone = phone;
            this.name = name;
            this.date = date;
            this.typeChanged = Arrays.toString(typeChanged);
        }

        @Override
        protected void onPreExecute() {

            cursor = MessageDBHelper.getDb().getReceivedMessageFromPhone(phone, date);
            config = ContactDBHelper.getFullContact(phone);

            Cursor cNumber = FollowDBHelper2.getDatabase().getAllNumber(date);
            Cursor cKeep = FollowDBHelper2.getDatabase().getAllKeepFromPhone(phone, date);
            wrapper = new ObjectWrapper(config, cNumber, cKeep);
            cNumber.close();
            cKeep.close();

            //Not recommend.
            wrapper.forceDeleteKeep();

            FollowDBHelper2.getDatabase().deleteKeepValueByDate(phone, date);

            int max = cursor.getCount() + 5;

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Kill app khi hệ thống đang chạy sẽ gây lỗi dữ liệu.");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(max);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            doInCursor(cursor);

            return null;
        }

        private void doInCursor(Cursor cursor) {
            if (cursor == null || !cursor.moveToFirst())
            {
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                return;
            }

            final int count = cursor.getCount();

            try {

                List<ContentValues> valuesList = new ArrayList<>();

                for (int i = 0; i < count; i++)
                {
                    String time = cursor.getString(cursor.getColumnIndex(MessageDatabase.RECEIVED_TIME));
                    String detail = cursor.getString(cursor.getColumnIndex(MessageDatabase.RECEIVED_DETAIL));
                    JSONArray arrayAnalyze = new JSONArray(detail);

                    int sizeAnalyze = arrayAnalyze.length();
                    for (int m = 0; m < sizeAnalyze; m++)
                    {
                        JSONArray array = arrayAnalyze.getJSONArray(m);

                        int size = array.length();
                        for (int j = 0; j < size; j++)
                        {
                            JSONObject object = array.getJSONObject(j);

                            String type = object.getString(JSONAnalyzeBuilder.KEY_TYPE);
                            String number = object.getString(JSONAnalyzeBuilder.KEY_NUMBER);
                            String price = object.getString(JSONAnalyzeBuilder.KEY_PRICE);

                        /*if (!typeChanged.contains(type))
                        {
                            continue;
                        }*/

                            Double newHeso = ConfigControl.getHeSo(config, type);

                            int newPrice = (int) (newHeso * Integer.parseInt(price));
                            int actPrice = wrapper.putKeep(newPrice, number, type);

                            if (actPrice > 0)
                            {
                                ContentValues values = new ContentValues();
                                values.put(FollowDatabase2.KEEP_NAME, name);
                                values.put(FollowDatabase2.KEEP_PHONE, phone);
                                values.put(FollowDatabase2.KEEP_DATE, date);
                                values.put(FollowDatabase2.KEEP_NUMBER, number);
                                values.put(FollowDatabase2.KEEP_TIME, time);
                                values.put(FollowDatabase2.KEEP_TYPE, type);
                                values.put(FollowDatabase2.KEEP_PARENTTYPE, type.contains("xien") ? "xien" : type);
                                values.put(FollowDatabase2.KEEP_PRICE, actPrice);
                                values.put(FollowDatabase2.KEEP_ACTUAL, "0");

                                valuesList.add(values);
                            }
                        }
                    }

                    cursor.moveToNext();
                }

                List<ContentValues> valuesListNumber = wrapper.buildToUpdate(date);

                //Insert all in one section.
                final int[] i = {0};
                final int size = valuesList.size() + valuesListNumber.size();

                FollowDBHelper2.getDatabase().updateArray(valuesListNumber, new UpdateSync() {
                    @Override
                    public void onUpdate() {
                        i[0]++;
                        int percent = (int) ((((double) i[0]) / ((double) size)) * 100d);

                        publishProgress(percent);
                    }
                });

                FollowDBHelper2.getDatabase().insertKeepArray(valuesList, new UpdateSync() {
                    @Override
                    public void onUpdate() {
                        i[0]++;
                        int percent = (int) ((((double) i[0]) / ((double) size)) * 100d);

                        publishProgress(percent);
                    }
                });
            } catch (Exception e) {
                publishProgress(-1);
            } finally {
                cursor.close();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int progress = values[0];

            if (progress == -1)
            {
                progressDialog.dismiss();

                Utils.showAlert(context, "Lỗi", "Có lõi xảy ra khi update giá trị giữ số.!", "Ok", null);
            }
            else
            {
                progressDialog.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("TaskHelper", "TaskLoadResultNumber: isCancelled: " + this.isCancelled() + " ,Status: " + this.getStatus());

            progressDialog.dismiss();

            Toast.makeText(context, "Giữ số thành công!", Toast.LENGTH_SHORT).show();
            requestUpdate();

            progressDialog = null;
            context = null;
        }

        protected void requestUpdate() {
            if (context != null)
            {
                Intent intent = new Intent(Action.ACTION_REQUEST_UPDATE);
                context.sendBroadcast(intent);
            }
        }
    }
}
