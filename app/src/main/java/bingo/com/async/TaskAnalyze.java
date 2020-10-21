package bingo.com.async;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bingo.com.MainActivity;
import bingo.com.R;
import bingo.com.base.BaseActivity;
import bingo.com.controller.DeleteController;
import bingo.com.controller.InsertController;
import bingo.com.enumtype.TypeMessage;
import bingo.com.helperdb.FollowDBHelper;
import bingo.com.helperdb.MessageDBHelper;
import bingo.com.helperdb.db.FollowDatabase;
import bingo.com.helperdb.db.MessageDatabase;
import bingo.com.model.ChooseMessage;
import bingo.com.model.MessageForm;
import bingo.com.model.ResultReturnModel;
import bingo.com.pref.ConfigPreference;
import bingo.com.screen.configscreen.SettingsHelper;
import bingo.com.utils.Action;
import bingo.com.utils.Utils;

/**
 * This function in class is duplicated. Because of change the way to analyze message. It must to be deleted.
 */
@Deprecated
public class TaskAnalyze {

    public static final String TAG = "TaskAnalyze";

    public static void begin(final Context context, final String name, final String phone, final String message, final long time, final long timeCheck, final TypeMessage typeMessage, final boolean forceAdd) {

        CommonTask.AsyncSingleThread.get().execute(new Runnable() {
            @Override
            public void run() {
                startAnalyze(context, name, phone, message, time, timeCheck, typeMessage, forceAdd, true);
            }
        });
    }

    private static void startAnalyze(Context context, String name, String phone, String message, long time, long timeCheck, TypeMessage typeMessage, boolean forceAdd, boolean requestUpdate) {
        Log.d(TAG, "Message received: " + "from: " + phone + " body: " + message + " time: " + time);

        /*Parcelable[] resultPs = intent.getParcelableArrayExtra(RESULT);
        ResultReturnModel[] results = new ResultReturnModel[]{(ResultReturnModel) resultPs[0], (ResultReturnModel) resultPs[1]};*/

        boolean receiveDe = (Utils.getTimeFromConfig(SettingsHelper.getTimeReceiveDe(getSaved(context), phone)) > timeCheck) || forceAdd;
        boolean receiveLo = Utils.getTimeFromConfig(SettingsHelper.getTimeReceiveLoXien(getSaved(context), phone)) > timeCheck;

        int response = SettingsHelper.getReplyMessageConfig(getSaved(context));

        String error = InsertController.analyzeSingleMessage(
                name, phone, message, String.valueOf(time), typeMessage, ConfigPreference.getRegexCustom(context), receiveDe, receiveLo, (typeMessage == TypeMessage.SENT), null);

        Log.d("AnalyzeService", "Import Message Received: Error: " + error);

        if (requestUpdate && context != null && context instanceof BaseActivity)
        {
            requestUpdate(context);
        }

        if (error == null)
        {
            if (!MessageDBHelper.hasResponse(phone, String.valueOf(time)) && (typeMessage == TypeMessage.RECEIVED))
            {
                responseMessage(phone, time, response, message, receiveDe, receiveLo);

                MessageDBHelper.updateResponseStatus(phone, String.valueOf(time), true);
            }
        }
    }

    private static void responseMessage(String phone, long time, int type, String message, boolean receivedDe, boolean receivedLo) {
        Cursor cursor = MessageDBHelper.getDb().getMessageForResponse(phone, Utils.convertDate(String.valueOf(time)));
        String mes = null;

        int count = cursor.getCount();
        for (int i = 0; i < count; i++)
        {
            cursor.moveToPosition(i);
            String timeInCursor = cursor.getString(cursor.getColumnIndex(MessageDatabase.TIME));

            if (timeInCursor.equals(String.valueOf(time)))
            {
                mes = "OK ".concat(receivedLo ? "" : message.matches(".*(de|bacang).*") ? "de " : "").concat("tin ").concat(String.valueOf(i + 1));

                switch (type)
                {
                    case SettingsHelper.OK_TIN_VA_NOI_DUNG:
                        mes = mes.concat("\n").concat(cursor.getString(cursor.getColumnIndex(MessageDatabase.ANALYZE_CONTENT)));
                        break;

                    case SettingsHelper.OK_TIN:
                        break;

                    case SettingsHelper.OK_TIN_DA_NHAN:
                        mes = mes + "\n" + message.trim();
                        break;

                    default:
                        mes = null;
                        break;
                }
            }
        }

        cursor.close();

        if (!TextUtils.isEmpty(mes))
        {
            mes = mes.replaceAll("<br>|</br>", " ");
            mes = mes.replaceAll("([,])\\s+([0-9])", "$1$2");
            mes = receivedLo ? mes : mes.concat("\nBỏ lô xiên vì quá giờ.");
            startSend(phone, mes);
        }
    }

    private static void startSend(String phone, String message) {
        try {

            if (message.length() > 160)
            {
                SmsManager sms = SmsManager.getDefault();
                ArrayList<String> parts = sms.divideMessage(message);
                sms.sendMultipartTextMessage(phone, null, parts, null, null);
            }
            else
            {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, message, null, null);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static SharedPreferences getSaved(Context context) {
        return context.getSharedPreferences(BaseActivity.PREF_NAME, Context.MODE_PRIVATE);
    }

    private static void notifyError(Context context, String message) {

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        if (message.length() > 20)
            message = message.substring(0, 20);

        Notification notification  = new Notification.Builder(context)
                .setContentTitle("Bingo Application")
                .setContentText("Error: " + message + "...")
                .setSmallIcon(R.drawable.ic_menu)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1010, notification);
    }

    protected static void requestUpdate(Context context) {
        Intent intent = new Intent(Action.ACTION_REQUEST_UPDATE);
        context.sendBroadcast(intent);
    }

    public static class TaskReloadNewConfig extends AsyncTask<String, Integer, String> {

        Context context;

        ProgressDialog progressDialog;

        Cursor cursor;

        String phone;

        TypeMessage typeMessage;

        public TaskReloadNewConfig(Context context, String phone, TypeMessage typeMessage) {
            this.context = context;

            this.phone = phone;
            this.typeMessage = typeMessage;
        }

        @Override
        protected void onPreExecute() {

            cursor = MessageDBHelper.getMessageByPhoneWithDate(phone, typeMessage, Utils.getCurrentDate());

            int max = cursor.getCount();

            progressDialog = new ProgressDialog(context);
            /*progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);*/
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
            if (!cursor.moveToFirst())
            {
                if (progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                return;
            }

            int count = cursor.getCount();
            List<String[]> cacheMessage = new ArrayList<>();

            for (int i = 0; i < count; i++)
            {
                String[] messageInfo = new String[4];
                messageInfo[0] = cursor.getString(cursor.getColumnIndex(MessageDatabase.NAME));
                messageInfo[1] = cursor.getString(cursor.getColumnIndex(MessageDatabase.CONTENT));
                messageInfo[2] = cursor.getString(cursor.getColumnIndex(MessageDatabase.TIME));
                messageInfo[3] = cursor.getString(cursor.getColumnIndex(MessageDatabase.TYPE));

                cacheMessage.add(messageInfo);
                cursor.moveToNext();
            }

            cursor.close();

            DeleteController.deleteDataUserHasAnalyze(phone, Utils.getCurrentDate(), false);

            for (int i = 0; i < count; i++)
            {
                String[] messageInfo = cacheMessage.get(i);

                MessageDBHelper.addMessage(new ChooseMessage(
                        messageInfo[0],
                        phone,
                        messageInfo[2],
                        messageInfo[1],
                        messageInfo[3]
                ));

//                startAnalyze(context, messageInfo[0], phone, messageInfo[1], Long.parseLong(messageInfo[2]), TypeMessage.convert(messageInfo[3]), false);
                InsertController.analyzeSingleMessage(
                        messageInfo[0], phone, messageInfo[1], String.valueOf(messageInfo[2]), TypeMessage.convert(messageInfo[3]), ConfigPreference.getRegexCustom(context),
                        true, true, (TypeMessage.convert(messageInfo[3]) == TypeMessage.SENT), null);

                int percent = (int) ((((double) i) / ((double) count)) * 100d);

                publishProgress(percent);
            }

            MessageDBHelper.updateResponseStatus(phone, Utils.getCurrentDate(), true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("TaskHelper", "TaskReloadNewConfig: isCancelled: " + this.isCancelled() + " ,Status: " + this.getStatus());

            progressDialog.dismiss();
            new TaskHelper.TaskCalculateResult(context, phone).execute();

            /*if (context != null)
            {
                requestUpdate();
            }*/
        }

        protected void requestUpdate() {
            Intent intent = new Intent(Action.ACTION_REQUEST_UPDATE);
            context.sendBroadcast(intent);
        }
    }
}