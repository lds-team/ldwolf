package bingo.com.async;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bingo.analyze.AnalyzeSMSNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import bingo.com.BuildConfig;
import bingo.com.base.BaseActivity;
import bingo.com.controller.ResultUpdate;
import bingo.com.enumtype.LotoType;
import bingo.com.enumtype.TableType;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.CongnoDBHelper;
import bingo.com.helperdb.ContactDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.SMSHelper;
import bingo.com.helperdb.db.CongnoDatabase;
import bingo.com.helperdb.db.LotoMsgProvider;
import bingo.com.helperdb.db.MessageDatabase;
import bingo.com.helperdb.db2.FollowDBHelper2;
import bingo.com.model.CongnoModel;
import bingo.com.model.ContactModel;
import bingo.com.model.LotoMessage;
import bingo.com.model.ReceivedModel;
import bingo.com.model.ResultModel;
import bingo.com.model.ResultReturnModel;
import bingo.com.screen.configscreen.SettingsHelper;
import bingo.com.utils.Action;
import bingo.com.utils.JSONAnalyzeBuilder;
import bingo.com.utils.LoaderSQLite;
import bingo.com.utils.StatisticControlImport;
import bingo.com.utils.Utils;

public class TaskHelper {

    public static class TaskLoadResult extends AsyncTask<String, String, String> {

        public static final String TAG = "TaskLoadResult";

        private Context context;

        public TaskLoadResult(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);

            String endDate = String.valueOf(calendar.getTimeInMillis());

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            String startDate = String.valueOf(calendar.getTimeInMillis());

            String[] reqCols = new String[] { "_id", "thread_id", "address", "body", "date", "person" };
            Cursor c = this.context.getContentResolver().query(SMSHelper.RECEIVED_MESSAGE_CONTENT_PROVIDER, reqCols, "date >=? AND date <=? AND (address =? OR address =?)" , new String[]{startDate, endDate, "997", "8085"}, null);

            if (c != null && c.getCount() > 0)
            {
                c.moveToFirst();

                String address = c.getString(c.getColumnIndex("address"));

                String body = c.getString(c.getColumnIndex("body"));

                String time = c.getString(c.getColumnIndex("date"));

                importKetqua(context, address, body, Long.parseLong(time));
            }

            if (c != null)
                c.close();

            //TODO dummy--------
            String body = "MB 10/10\n" +
                    "DB:80541\n" +
                    "1:33541\n" +
                    "2:33286-14434\n" +
                    "3:92813-64461-68111-38236-25172-13933\n" +
                    "4:2124-9078-9472-1787\n" +
                    "5:5006-3666-1835-9101-4380-2954\n" +
                    "6:173-075-100\n" +
                    "7:22-47-25-19";

            importKetqua(context, "997", body, Calendar.getInstance().getTimeInMillis());
            //-------------

            return  null;
        }

        private void importKetqua(final Context context, String address, String body, long time) {
            try {
                List<LotoMessage> lsLotoMessage = new ArrayList<>();

                Date dateMsg = new Date(time);
                Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                LotoMessage mLotoMessage = new LotoMessage();
                mLotoMessage.Address = address;
                mLotoMessage.Body = body;
                mLotoMessage.SentDate = dateMsg;

                if (body == null)
                {
                    return;
                }

                lsLotoMessage.add(mLotoMessage);


                if (lsLotoMessage.size() > 0) {
                    String mBody = lsLotoMessage.get(0).Body;
                    String bodySeparated = ResultModel.buildFrom(mBody);

                    if (TextUtils.isEmpty(bodySeparated))
                    {
                        mBody = ""; //Remove when not enough prize.
                    }

                    String date = ResultModel.getDateFrom(mBody);

                    if (date == null) {
                        return;
                    }

                    ContentValues values = new ContentValues();

                    values.put(LotoMsgProvider.Address, lsLotoMessage.get(0).Address);
                    values.put(LotoMsgProvider.Date, date);
                    values.put(LotoMsgProvider.Body, mBody);
                    values.put(LotoMsgProvider.Body_Separated, bodySeparated);

                    ContentResolver resolver = context.getContentResolver();

                    //IF it's new year, update old db.
                    if (isYearTrans(date, lsLotoMessage.get(0).SentDate)) {
                        int row = resolver.update(
                                LoaderSQLite.getUriForQueryLoto(String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1)),
                                values,
                                LotoMsgProvider.Date + "=?",
                                new String[]{date});

                        Log.d(TAG, "Import Result: " + "updated in row: " + row + ", case YearTrans = true");
                        return;
                    }

                    Cursor c = resolver.query(LotoMsgProvider.CONTENT_URI, null, LotoMsgProvider.Date + "=?", new String[]{date}, null);

                    try {
                        if (c == null || c.getCount() == 0) {
                            // Insert DB.
                            Uri uri = resolver.insert(
                                    LotoMsgProvider.CONTENT_URI, values);

                            Log.d(TAG, "Import Result: " + uri);
                        } else {
                            //Update DB If exist.
                            int row = resolver.update(
                                    LotoMsgProvider.CONTENT_URI, values, LotoMsgProvider.Date + "=?", new String[]{date});

                            Log.d(TAG, "Import Result: " + "updated in row: " + row + ", case YearTrans = false");
                        }
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }

                }

            } catch (Exception ex) {
                if (BuildConfig.DEBUG)
                {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }

        private boolean isYearTrans(String dateResult, Date dateMessage) {
            String[] date = dateResult.trim().split("/");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateMessage);

            return date[0] == "31" && date[1] == "12" && calendar.get(Calendar.DAY_OF_MONTH) == 1 && calendar.get(Calendar.MONTH) == 0;
        }
    }

    public static class TaskCalculateResult extends AsyncTask<String, Integer, String> {

        Context context;

        ResultReturnModel[] result;

        private AnalyzeSMSNew analyzeSMS;

        ProgressDialog progressDialog;

        Cursor cursor;

        String phone;

        boolean hasResult;

        public TaskCalculateResult(Context context, String phone) {
            this.context = context;

            this.phone = phone;
        }

        @Override
        protected void onPreExecute() {

            if (phone == null)
            {
                cursor = MessageDBHelper.getDb().getAllMessageInDB(Utils.getCurrentDate());
            }
            else
            {
                cursor = MessageDBHelper.getDb().getAllMessageFromPhone(phone, Utils.getCurrentDate());
            }

            int max = cursor.getCount();

            progressDialog = new ProgressDialog(context);
            /*progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);*/
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(max);
            progressDialog.show();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Utils.getLongTimeWithCurrentConfigDate());

            result = StatisticControlImport.getResultWithDay(context, calendar);

            analyzeSMS = new AnalyzeSMSNew();
            hasResult = analyzeSMS.setResult(result[0].getBody());

            calendar.clear();
        }

        @Override
        protected String doInBackground(String... params) {

            if (hasResult)
            {
                doInCursor(cursor);
            }
            else
            {
                doInCursor(null);
            }

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

            Set<String> numberDeWin = new HashSet<>();
            Set<String> numberLoWin = new HashSet<>();
            Set<String> numberXienWin = new HashSet<>();
            Set<String> numberBacangWin = new HashSet<>();

            String oldPhone = cursor.getString(cursor.getColumnIndex(MessageDatabase.RECEIVED_PHONE));

            int count = cursor.getCount();

            ResultUpdate calculateResult = new ResultUpdate(analyzeSMS);

            for (int i = 0; i < count; i++)
            {
                String phone = cursor.getString(cursor.getColumnIndex(MessageDatabase.RECEIVED_PHONE));

                String time = cursor.getString(cursor.getColumnIndex(MessageDatabase.RECEIVED_TIME));

                String messageType = cursor.getString(cursor.getColumnIndex(MessageDatabase.RECEIVED_TYPE_MESSAGE));

                boolean changePhone = !oldPhone.equals(phone);

                if (i == 0 || changePhone)
                {
                    MessageDBHelper.removeAllWinnumber(phone, Utils.getCurrentDate());
                    calculateResult.setPhone(phone);
                    oldPhone = phone;
                }

                String detail = cursor.getString(cursor.getColumnIndex(MessageDatabase.RECEIVED_DETAIL));

                try {

                    JSONArray arrayAnalyze = new JSONArray(detail);
                    calculateResult.setData(time, messageType, arrayAnalyze);

                    int sizeAnalyze = arrayAnalyze.length();
                    for (int m = 0; m < sizeAnalyze; m++)
                    {
                        JSONArray jsonArray = arrayAnalyze.getJSONArray(m);
                        int size = jsonArray.length();

                        boolean isLastSyntax;

                        for (int j = 0; j < size; j++)
                        {
                            JSONObject object = jsonArray.getJSONObject(j);
                            String number = object.getString(JSONAnalyzeBuilder.KEY_NUMBER);
                            String type = object.getString(JSONAnalyzeBuilder.KEY_TYPE);
                            String price = object.getString(JSONAnalyzeBuilder.KEY_PRICE);
                            String unit = type.contains("lo") ? "d" : "n";

                            isLastSyntax = (j == size - 1);

                            if (!isLastSyntax)
                            {
                                JSONObject nextObject = jsonArray.getJSONObject(j + 1);
                                String nextType = nextObject.getString(JSONAnalyzeBuilder.KEY_TYPE);
                                String nextPrice = nextObject.getString(JSONAnalyzeBuilder.KEY_PRICE);

                                if (!nextType.equals(type) || !nextPrice.equals(price))
                                {
                                    isLastSyntax = true;
                                }
                            }

                            boolean hasWin = calculateResult.calculateNumber(number, type, price, unit, isLastSyntax);

                            if (hasWin)
                            {
                                if (type.contains("de"))
                                {
                                    numberDeWin.add(number);
                                }
                                else if (type.contains("lo"))
                                {
                                    numberLoWin.add(number);
                                }
                                else if (type.contains("xien"))
                                {
                                    numberXienWin.add(number);
                                }
                                else if (type.contains("bacang"))
                                {
                                    numberBacangWin.add(number);
                                }
                            }
                        }

                        calculateResult.endSyntaxAnalyze();
                    }

                    int percent = (int) ((((double) i) / ((double) count)) * 100d);

                    publishProgress(percent);

                    cursor.moveToNext();

                } catch (JSONException e) {
                    publishProgress(-1);
                }

                ReceivedModel model = calculateResult.build();
                MessageDBHelper.getDb().updateResult(model);
                FollowDBHelper2.getDatabase().updateResult(Utils.getCurrentDate(), TableType.de_table, numberDeWin.toArray(new String[numberDeWin.size()]));
                FollowDBHelper2.getDatabase().updateResult(Utils.getCurrentDate(), TableType.lo_table, numberLoWin.toArray(new String[numberLoWin.size()]));
                FollowDBHelper2.getDatabase().updateResult(Utils.getCurrentDate(), TableType.xien_table, numberXienWin.toArray(new String[numberXienWin.size()]));
                FollowDBHelper2.getDatabase().updateResult(Utils.getCurrentDate(), TableType.bacang_table, numberBacangWin.toArray(new String[numberBacangWin.size()]));
            }

            cursor.close();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int progress = values[0];

            if (progress == -1)
            {
                progressDialog.dismiss();

                Utils.showAlert(context, "Lỗi", "Update kết quả không thành công!", "Ok", null);
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

            if (hasResult)
            {
                CommonTask.AsyncSingleThread.get().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (phone == null)
                        {
                            calculate();
                        }
                        else
                        {
                            String name = ContactDBHelper.getName(phone);
                            Cursor cursor = MessageDBHelper.getDb().getResultCalculate(phone);
                            if (cursor.moveToFirst())
                            {
                                calculateOnce(cursor, name, phone);
                            }
                            cursor.close();
                        }

                        phone = null;

                        if (context != null)
                        {
                            requestUpdate();
                        }
                    }
                }).clean();
            }
        }

        private void calculate() {
            Cursor cursor = MessageDBHelper.getDb().getResultCalculate();

            try {

                if (cursor != null && cursor.moveToFirst())
                {
                    while (!cursor.isAfterLast())
                    {
                        String name = cursor.getString(cursor.getColumnIndex(MessageDatabase.NAME));
                        String phone = cursor.getString(cursor.getColumnIndex(MessageDatabase.PHONE));

                        calculateOnce(cursor, name, phone);

                        cursor.moveToNext();
                    }
                }

            } finally {
                cursor.close();
            }
        }

        public void calculateOnce(Cursor cursor, final String name, final String phone) {

            int sumde = cursor.getInt(cursor.getColumnIndex("sumde"));
            int sumlo = cursor.getInt(cursor.getColumnIndex("sumlo"));
            int sumxien2 = cursor.getInt(cursor.getColumnIndex("sumxien2"));
            int sumxien3 = cursor.getInt(cursor.getColumnIndex("sumxien3"));
            int sumxien4 = cursor.getInt(cursor.getColumnIndex("sumxien4"));
            int sumbacang = cursor.getInt(cursor.getColumnIndex("sumbacang"));

            double actDe = cursor.getDouble(cursor.getColumnIndex("sumactde"));
            double actLo = cursor.getDouble(cursor.getColumnIndex("sumactlo"));
            double actXien2 = cursor.getDouble(cursor.getColumnIndex("sumactxien2"));
            double actXien3 = cursor.getDouble(cursor.getColumnIndex("sumactxien3"));
            double actXien4 = cursor.getDouble(cursor.getColumnIndex("sumactxien4"));
            double actBacang = cursor.getDouble(cursor.getColumnIndex("sumactbacang"));

            int winDe = cursor.getInt(cursor.getColumnIndex("sumwinde"));
            int winLo = cursor.getInt(cursor.getColumnIndex("sumwinlo"));
            int winXien2 = cursor.getInt(cursor.getColumnIndex("sumwinxien2"));
            int winXien3 = cursor.getInt(cursor.getColumnIndex("sumwinxien3"));
            int winXien4 = cursor.getInt(cursor.getColumnIndex("sumwinxien4"));
            int winBacang = cursor.getInt(cursor.getColumnIndex("sumwinbacang"));
            int winPointLo = cursor.getInt(cursor.getColumnIndex("sumpointwinlo"));

            String typeMessage = cursor.getString(3);
            String typeCustom = typeMessage.equals(TypeMessage.RECEIVED.name()) ? "0" : "1";

            CongnoModel modelDe = new CongnoModel(
                    name,
                    phone,
                    LotoType.valueOf("de"),
                    sumde,
                    winDe,
                    Utils.getCurrentDate(),
                    String.valueOf(winDe - actDe),
                    typeCustom
            );

            CongnoModel modelLo = new CongnoModel(
                    name,
                    phone,
                    LotoType.valueOf("lo"),
                    sumlo,
                    winPointLo,
                    Utils.getCurrentDate(),
                    String.valueOf(winLo - actLo),
                    typeCustom
            );

            CongnoModel modelXien = new CongnoModel(
                    name,
                    phone,
                    LotoType.valueOf("xien"),
                    sumxien2 + sumxien3 + sumxien4,
                    winXien2 + winXien3 + winXien4,
                    Utils.getCurrentDate(),
                    String.valueOf(winXien2 + winXien3 + winXien4 - actXien2 - actXien3 - actXien4),
                    typeCustom
            );

            CongnoModel modelBacang = new CongnoModel(
                    name,
                    phone,
                    LotoType.valueOf("bacang"),
                    sumbacang,
                    winBacang,
                    Utils.getCurrentDate(),
                    String.valueOf(winBacang - actBacang),
                    typeCustom
            );

            CongnoDBHelper.insert(modelDe, modelLo, modelXien, modelBacang);
        }

        protected void requestUpdate() {
            Intent intent = new Intent(Action.ACTION_REQUEST_UPDATE);
            context.sendBroadcast(intent);
        }
    }


    public static class TaskSendMessageChotTien extends AsyncTask<String, Integer, String> {

        Context context;

        ProgressDialog progressDialog;

        Cursor cursor;

        int max = 0;

        private HashMap<String, ContactModel> contacts;

        public TaskSendMessageChotTien(Context context) {
            this.context = context;

            contacts = ContactDBHelper.getFullContact();
        }

        @Override
        protected void onPreExecute() {

            cursor = MessageDBHelper.getDb().getAllContact();
            max = cursor.getCount();

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

            for (int i = 0; i < max; i++)
            {
                cursor.moveToPosition(i);
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                if (!phone.isEmpty())
                {
                    String message = getMessageChotTien(phone);
                    if (!TextUtils.isEmpty(message))
                    {
                        if (message.equals("empty")) continue;

                        sendMessage(phone, message);
                        publishProgress(i);
                    }
                    else
                    {
                        publishProgress(-2);
                    }
                }
                else
                {
                    publishProgress(-1);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int progress = values[0];

            if (progress == -1)
            {
                progressDialog.dismiss();

                Utils.showAlert(context, "Lỗi", "Có lõi xảy ra khi gửi tin nhắn.!", "Ok", null);
            }
            else if (progress == -2)
            {
                progressDialog.dismiss();

                Utils.showAlert(context, "Lỗi", "Chưa tính tiền khách này.!", "Ok", null);
            }
            else
            {
                progressDialog.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            Toast.makeText(context, "Đã gửi tin nhắn!", Toast.LENGTH_SHORT).show();

            progressDialog = null;
            context = null;
        }

        private void sendMessage(final String phone, final String mes) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, mes, null, null);
        }

        private String getMessageChotTien(String phone) {

            double HS_THANG_DE = 1;
            double HS_THANG_BACANG = 1;
            boolean isCHu = false;
            ContactModel contact = contacts.get(phone);
            if (contact != null)
            {
                HS_THANG_DE = contact.getDITDB_LANAN() / 1000;
                HS_THANG_BACANG = contact.getBACANG_LANAN() / 1000;
                isCHu = contact.getType().equals("1");
            }
            else
            {
                return "empty";
            }

            String date = Utils.getCurrentDate();

            Cursor cursor = CongnoDBHelper.getCongNoByPhone(phone);

            try {

                if (cursor != null && cursor.moveToFirst())
                {
                    String mes = date.concat("\n");
                    int sum = 0;

                    while (!cursor.isAfterLast())
                    {
                        String type = cursor.getString(cursor.getColumnIndex(CongnoDatabase.DETAIL_TYPE));
                        double point = cursor.getDouble(cursor.getColumnIndex(CongnoDatabase.DETAIL_POINT));
                        int pointWin = cursor.getInt(cursor.getColumnIndex(CongnoDatabase.DETAIL_POINT_WIN));
                        if (type.equals(LotoType.de.name()))
                        {
                            pointWin = HS_THANG_DE == 0 ? 0 : (int) (pointWin / HS_THANG_DE);
                        }
                        else if (type.equals(LotoType.bacang.name()))
                        {
                            pointWin = HS_THANG_BACANG == 0 ? 0 : (int) (pointWin / HS_THANG_BACANG);
                        }

                        int money = cursor.getInt(cursor.getColumnIndex(CongnoDatabase.DETAIL_MONEY));
                        sum += money;

                        if (contact != null)
                        {
                            mes = mes.concat(type).concat(": ").concat(Utils.getDotNumberText(point)).concat("(").concat(Utils.getDotNumberText(pointWin))
                                    .concat(")").concat(" = ").concat(Utils.getDotNumberText(money)).concat("\n");
                        }

                        cursor.moveToNext();
                    }

                    sum = isCHu ? 0 - sum : sum;

                    mes = mes.concat("Tong ngay: ").concat(String.valueOf(sum));

                    int type = SettingsHelper.getChotSoDuConfig(((BaseActivity) context).getSaved());

                    if (type == SettingsHelper.CHOT_SO_DU_VA_CONG_NO_CU)
                    {
                        double congnocu = CongnoDBHelper.getNoCuTodate(phone, date);
                        congnocu = isCHu ? 0 - congnocu : congnocu;
                        mes = mes.concat("\nSo cu: ").concat(Utils.getDotNumberText(congnocu));
                        mes = mes.concat("\nTong: ").concat(Utils.getDotNumberText(sum + congnocu));
                    }

                    Log.d("CustomerAdapter", "MessageChot: " + mes);

                    return mes;
                }

            } finally {
                cursor.close();
            }

            return null;
        }
    }
}
